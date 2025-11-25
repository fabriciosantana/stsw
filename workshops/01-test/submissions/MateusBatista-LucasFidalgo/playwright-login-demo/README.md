
---

## Título

**Playwright** – *Mateus Batista e Lucas Fidalgo* – *03/10/2025*

---

## Introdução

O **Playwright** é um framework moderno de automação de testes de interface, criado pela Microsoft.
Ele permite escrever **testes End-to-End (E2E)** que simulam a experiência real do usuário em múltiplos navegadores: **Chromium, Firefox e WebKit**.

Na pirâmide de automação de testes, o Playwright está posicionado no **nível de interface (UI/E2E)**, validando a aplicação como um todo (frontend + backend).

---

## Principais Funcionalidades

* **Multi-browser**: suporta Chromium, Firefox e WebKit.
* **Seletores inteligentes**: `getByRole`, `getByLabel`, `getByText`.
* **Execução headless ou visual**.
* **Esperas automáticas** (reduz falhas por sincronização).
* **SlowMo & Trace Viewer** para depuração.
* **Screenshots, vídeos e logs de execução**.
* **Paralelismo nativo** para rodar testes em escala.


### Integrações disponíveis

* Funciona com **JUnit** e outros frameworks de teste.
* Integra-se facilmente em **pipelines CI/CD** (GitHub Actions, Jenkins, Azure DevOps).
* Suporte a execução em **containers** e em **nuvem** (BrowserStack, Sauce Labs).

---

## Lista de Frameworks Similares

* **Selenium** – clássico para automação de browsers.
* **Cypress** – foco em JavaScript, simples para frontend moderno.
* **TestCafe** – alternativa para testes em Node.js.
* **Puppeteer** – automação de Chromium, mas menos completo que o Playwright.

---

## Vantagens e Desvantagens

### Vantagens

* API moderna e intuitiva.
* Multi-browser em um só framework.
* Excelente documentação.
* Suporte a paralelismo e execução rápida.
* Recursos avançados de debug (trace viewer, screenshots, vídeos).

### Desvantagens

* Projeto relativamente novo comparado ao Selenium.
* Comunidade menor que frameworks mais antigos.
* Curva de aprendizado para quem vem de Selenium.
---

## Conclusão

O **Playwright** é uma ferramenta poderosa e moderna para testes de interface, ideal para cenários que exigem **confiabilidade e execução multiplataforma**.

### Recomendações

* **Adotar** quando o objetivo é validar a experiência completa do usuário em navegadores.
* **Evitar** em testes de unidade ou cenários que exigem apenas lógica de backend (use JUnit, TestNG, etc. para isso).

---

## 📂 Estrutura do Projeto

```
play-login-demo/
├─ pom.xml                 # Configuração do Maven e dependências
├─ src/
│  ├─ main/java/com/example/demo/
│  │  ├─ App.java          # Backend principal (rotas, filtros, servidor Spark)
│  │  ├─ Auth.java         # Utilitário JWT (gerar e validar tokens)
│  │  ├─ RateLimiter.java  # Implementação simples de rate-limit
│  │  └─ StaticPageRenderer.java # Renderizador de HTML estático
│  └─ main/resources/public/
│     ├─ login.html        # Página de login
│     ├─ dashboard.html    # (versão final é renderizada no servidor)
│     └─ error.html        # Página de erro
└─ src/test/java/com/example/demo/
   └─ LoginE2ETest.java    # Testes E2E com Playwright + JUnit
```

---

## 🔧 Tecnologias usadas

* **Java 17** → linguagem principal.
* **Maven** → gerenciamento de dependências e build.
* **Spark Java** → microframework web para rotas e servidor HTTP.
* **Java JWT (auth0)** → geração e validação de tokens JWT.
* **SLF4J Simple** → logging simples no console.
* **JUnit 5** → framework de testes.
* **Playwright Java** → testes de navegador (E2E) com suporte a Chromium, Firefox e WebKit.

---

## 🚀 Backend (Spark Java)

### ✨ Fluxo implementado

1. **Página `/login`** → formulário de usuário e senha.
2. **POST `/login`** → valida credenciais (`admin/123456`).

   * Em caso de sucesso:

     * gera um **JWT** com validade de 5 min,
     * guarda em um **cookie HttpOnly** chamado `session`.
   * Em caso de falha:

     * incrementa o **rate-limit** (máx. 5 falhas em 5min).
     * ao estourar o limite, bloqueia o IP por 5min.
3. **Rota `/dashboard`** → protegida por filtro que exige JWT válido.
4. **Rota `/logout`** → limpa cookie e redireciona para login.
5. **Rota `/error`** → exibe mensagens de erro genéricas.

---

## 🧪 Testes E2E (Playwright + JUnit)

O arquivo `LoginE2ETest.java` sobe o backend localmente e executa os cenários de teste:

1. **Login válido** → redireciona para `/dashboard` e grava cookie.
2. **Credencial inválida** → mostra mensagem de erro, sem cookie.
3. **Rate-limit** → após muitas falhas, redireciona para `/error`.
4. **Acesso direto ao `/dashboard` sem login** → redireciona para `/login`.
5. **Logout** → remove cookie e bloqueia acesso subsequente.
6. **Hard refresh** → mantém sessão ativa graças ao cookie HttpOnly.

### 🔍 Recursos Playwright usados

* `getByLabel` → busca inputs pelo atributo acessível (`aria-label`).
* `getByRole` → busca elementos pelo papel semântico (`button`, `alert`, `link`).
* `waitForURL` → espera redirecionamento antes de validar.
* `ctx.cookies()` → valida cookies no contexto do navegador.
* `setHeadless(false).setSlowMo(500)` → abre navegador em modo visual com delay.

---

## ▶️ Como rodar

1. Instalar dependências:

   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk maven -y
   ```

2. Instalar navegadores do Playwright:

   ```bash
   mvn -Dplaywright.cli.install=true test
   ```

3. Rodar os testes:

   ```bash
   mvn test
   ```

4. (Opcional) Subir servidor manualmente:

   ```bash
   mvn exec:java -Dexec.mainClass="com.example.demo.App"
   ```

   Acesse: [http://localhost:8080/login](http://localhost:8080/login)


## 🎥 Demonstração (modo visual)

Exemplo:

```java
browser = pw.chromium().launch(
    new BrowserType.LaunchOptions()
        .setHeadless(false) // abre a janela
        .setSlowMo(500)     // delay de 0.5s entre ações
);
```

Navegador irá abrir e cada etapa executada de forma lenta.

---
