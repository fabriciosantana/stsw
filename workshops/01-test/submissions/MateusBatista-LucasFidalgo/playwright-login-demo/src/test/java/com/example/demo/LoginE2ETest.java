package com.example.demo;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginE2ETest {

  static Playwright pw;
  static Browser browser;
  static BrowserContext ctx;
  static Page page;
  static final String BASE = "http://localhost:8080";

  @BeforeAll
  static void beforeAll() {
    // Sobe o servidor (em background)
    App.startServer(8080);

    pw = Playwright.create();
    browser = pw.chromium().launch(
    new BrowserType.LaunchOptions()
        .setHeadless(false)   // abre o navegador de verdade
        .setSlowMo(1000)       // adiciona 500ms de delay entre cada ação
        // 
    );
  }

  @AfterAll
  static void afterAll() {
    if (ctx != null) ctx.close();
    browser.close();
    pw.close();

    App.stopServer();
  }

  @BeforeEach
  void setup() {
    ctx = browser.newContext(new Browser.NewContextOptions()
        .setIgnoreHTTPSErrors(true)); // só por segurança
    page = ctx.newPage();
  }

  @AfterEach
  void cleanup() {
    ctx.close();
  }

  @Test @Order(1)
  void loginValidoRedirecionaDashboardEGravaCookie() {
    page.navigate(BASE + "/login");
    page.getByLabel("Usuário").fill(App.VALID_USER);
    page.getByLabel("Senha").fill(App.VALID_PASS);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
    page.waitForURL("**/dashboard");

    assertTrue(page.getByText("Olá, " + App.VALID_USER).isVisible());

    // Verifica cookie HttpOnly no contexto
    boolean hasSessionCookie = ctx.cookies().stream().anyMatch(c -> c.name.equals("session"));
    assertTrue(hasSessionCookie, "Deveria ter cookie de sessão após login");
  }

  @Test @Order(2)
  void credencialInvalidaMostraErroENaoSetaCookie() {
    page.navigate(BASE + "/login");
    page.getByLabel("Usuário").fill("xpto");
    page.getByLabel("Senha").fill("errada");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
    page.waitForURL("**/login?err=**");

    // A mensagem no login.html está marcada como role="alert"
    assertTrue(page.getByRole(AriaRole.ALERT).isVisible());
    boolean hasSessionCookie = ctx.cookies().stream().anyMatch(c -> c.name.equals("session"));
    assertFalse(hasSessionCookie, "Não deveria ter cookie de sessão com credencial inválida");
  }

  @Test @Order(3)
  void rateLimitBloqueiaAposMuitasTentativasInvalidas() {
    // 6 tentativas erradas → bloqueio
    for (int i = 0; i < 6; i++) {
      page.navigate(BASE + "/login");
      page.getByLabel("Usuário").fill("nope");
      page.getByLabel("Senha").fill("nope");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
      // as primeiras 5 devem voltar ao login com err, a 6ª redireciona para /error
    }
    page.waitForURL("**/error?msg=**");
    assertTrue(page.getByText("Bloqueado temporariamente").isVisible());
  }

  @Test @Order(4)
  void rotaProtegidaRedirecionaSeSemSessao() {
    // sem cookie de sessão
    page.navigate(BASE + "/dashboard");
    page.waitForURL("**/login?err=**");
    assertTrue(page.getByText("login novamente").isVisible());
  }

  @Test @Order(5)
  void logoutRemoveSessaoENaoPermiteAcesso() {
    // login
    page.navigate(BASE + "/login");
    page.getByLabel("Usuário").fill(App.VALID_USER);
    page.getByLabel("Senha").fill(App.VALID_PASS);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
    page.waitForURL("**/dashboard");
    assertTrue(page.getByText("Olá, " + App.VALID_USER).isVisible());

    // logout
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sair")).click();
    page.waitForURL("**/login?info=**");
    assertTrue(page.getByText("Você saiu da sessão.").isVisible());

    // tentar ir direto ao dashboard novamente
    page.navigate(BASE + "/dashboard");
    page.waitForURL("**/login?err=**");
  }

  @Test @Order(6)
  void hardRefreshPreservaSessao() {
    // login
    page.navigate(BASE + "/login");
    page.getByLabel("Usuário").fill(App.VALID_USER);
    page.getByLabel("Senha").fill(App.VALID_PASS);
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Entrar")).click();
    page.waitForURL("**/dashboard");
    assertTrue(page.getByText("Olá, " + App.VALID_USER).isVisible());

    // refresh
    page.reload();
    assertTrue(page.getByText("Olá, " + App.VALID_USER).isVisible(), "Sessão deveria persistir após refresh");
  }
}
