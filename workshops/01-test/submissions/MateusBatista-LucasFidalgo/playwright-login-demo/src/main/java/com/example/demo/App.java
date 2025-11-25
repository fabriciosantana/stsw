package com.example.demo;

import static spark.Spark.*;

import spark.Filter;
import spark.Request;
import spark.Response;

import java.util.Optional;


public class App {
  // Credenciais fixas de demonstração
  public static final String VALID_USER = "admin";
  public static final String VALID_PASS = "123456";
  private static final String COOKIE_NAME = "session";

  // Segredo do JWT (em hardcode apenas para demonstração)
  private static final String JWT_SECRET = "chave-secreta-para-demontrar";

  public static void main(String[] args) {
    startServer(8080); // Porta padrão para rodar "fora" do teste
  }

  /** Permite o teste subir/parar o servidor programaticamente */
  public static void startServer(int port) {
    port(port);
    staticFiles.location("/public"); // serve /public

    // Filtro para logs simples (útil para entender a sequência)
    before((req, res) -> System.out.printf("%s %s%n", req.requestMethod(), req.uri()));

    // Rota raiz redireciona para login
    get("/", (req, res) -> {
      res.redirect("/login");
      return "";
    });

    // GET /login -> página
    get("/login", (req, res) -> {
      res.type("text/html");
      return "";
    }, new StaticPageRenderer("login.html"));

    // POST /login -> valida credenciais, rate limit e emite cookie
    post("/login", (req, res) -> {
      String username = Optional.ofNullable(req.queryParams("username")).orElse("");
      String password = Optional.ofNullable(req.queryParams("password")).orElse("");
      String ip = clientKey(req);

      // Rate limit: 5 tentativas falhas a cada 5 min -> lock 5 min
      if (RateLimiter.isLocked(ip)) {
        res.redirect("/error?msg=Bloqueado%20temporariamente%20por%20muitas%20tentativas.");
        return "";
      }

      if (VALID_USER.equals(username) && VALID_PASS.equals(password)) {
        // sucesso: reseta contador e cria JWT
        RateLimiter.reset(ip);
        String token = Auth.createToken(JWT_SECRET, username, 5 /* expira em 5 min */);
        res.cookie("/", COOKIE_NAME, token, 300, false, true); // httpOnly
        res.redirect("/dashboard");
      } else {
        RateLimiter.registerFailedAttempt(ip);
        res.redirect("/login?err=Credenciais%20inv%C3%A1lidas");
      }
      return "";
    });

    // Protege /dashboard com filtro de autenticação
    before("/dashboard", authFilter());

    get("/dashboard", (req, res) -> {
      String token = req.cookie(COOKIE_NAME);
      String user = Auth.verifyAndGetSubject(JWT_SECRET, token).orElse("desconhecido");
      res.type("text/html");
      return String.format("""
        <!doctype html>
        <html lang="pt-br">
        <head><meta charset="utf-8"><title>Dashboard</title></head>
        <body style="font-family: sans-serif; max-width: 720px; margin: 40px auto;">
          <h1>Olá, %s</h1>
          <p>Esta é uma rota protegida por <strong>JWT</strong> em cookie HttpOnly.</p>
          <p><a href="/logout">Sair</a></p>
        </body>
        </html>
      """, user);
    });

    get("/logout", (req, res) -> {
      res.removeCookie("/", COOKIE_NAME);
      res.redirect("/login?info=Voc%C3%AA%20saiu%20da%20sess%C3%A3o.");
      return "";
    });

    // Página simples de erro
    get("/error", (req, res) -> {
      res.type("text/html");
      return "";
    }, new StaticPageRenderer("error.html"));

    awaitInitialization();
  }

  public static void stopServer() {
    stop();
    awaitStop();
  }

  private static Filter authFilter() {
    return (Request req, Response res) -> {
      String token = req.cookie(COOKIE_NAME);
      if (token == null || Auth.verifyAndGetSubject(JWT_SECRET, token).isEmpty()) {
        res.redirect("/login?err=Fa%C3%A7a%20login%20novamente%20(sess%C3%A3o%20inv%C3%A1lida%20ou%20expirada)");
        halt();
      }
    };
  }

  private static String clientKey(Request req) {
    // simples: usa IP remoto (em prod: prefira X-Forwarded-For + userId)
    return Optional.ofNullable(req.ip()).orElse("unknown");
  }
}

