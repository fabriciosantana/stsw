import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/** Laboratório isolado: somente loopback, dados fictícios e endpoint vulnerável intencional. */
public final class AccessControlLab {
    private record Principal(int userId, boolean admin) {}
    private record Profile(int ownerId, String data) {}
    private static final Map<String, Principal> SESSIONS = new HashMap<>();
    private static final Map<Integer, Profile> PROFILES = Map.of(
            1, new Profile(1, "perfil-ficticio-usuario-1"),
            2, new Profile(2, "perfil-ficticio-usuario-2"));

    private AccessControlLab() {}

    private static String token(Principal principal) {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        String value = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        SESSIONS.put(value, principal);
        return value;
    }

    private static Principal principal(HttpExchange request) {
        String auth = request.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) return null;
        return SESSIONS.get(auth.substring("Bearer ".length()));
    }

    private static int requestedId(URI uri) {
        String query = uri.getRawQuery();
        if (query == null || !query.matches("userId=[0-9]+")) return -1;
        try {
            return Integer.parseInt(query.substring("userId=".length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void reply(HttpExchange request, int status, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        request.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        request.sendResponseHeaders(status, data.length);
        try (var output = request.getResponseBody()) {
            output.write(data);
        }
    }

    private static void profile(HttpExchange request, boolean secure) throws IOException {
        Principal actor = principal(request);
        if (actor == null) {
            reply(request, 401, "autenticacao exigida");
            return;
        }
        int id = requestedId(request.getRequestURI());
        if (id < 0) {
            reply(request, 400, "userId invalido");
            return;
        }
        Profile profile = PROFILES.get(id);
        if (profile == null) {
            reply(request, 404, "perfil inexistente");
            return;
        }
        // A versão insegura omite esta decisão por objeto: IDOR intencional.
        if (secure && actor.userId() != profile.ownerId() && !actor.admin()) {
            reply(request, 403, "acesso negado");
            return;
        }
        reply(request, 200, profile.data());
    }

    private static void adminReport(HttpExchange request) throws IOException {
        Principal actor = principal(request);
        if (actor == null) {
            reply(request, 401, "autenticacao exigida");
        } else if (!actor.admin()) {
            reply(request, 403, "acesso negado");
        } else {
            reply(request, 200, "relatorio-administrativo-ficticio");
        }
    }

    private static void check(HttpClient client, int port, String actor, String token,
                              String path, int expectedStatus, String expectedBody) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + port + path)).GET();
        if (token != null) builder.header("Authorization", "Bearer " + token);
        HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != expectedStatus || !response.body().equals(expectedBody)) {
            throw new AssertionError(actor + " " + path + ": recebido " + response.statusCode()
                    + " / " + response.body());
        }
        System.out.println(actor + " GET " + path + " -> HTTP " + response.statusCode()
                + " / " + response.body());
    }

    public static void main(String[] args) throws Exception {
        String user1 = token(new Principal(1, false));
        String user2 = token(new Principal(2, false));
        String admin = token(new Principal(99, true));
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        server.createContext("/insecure/profile", request -> profile(request, false));
        server.createContext("/secure/profile", request -> profile(request, true));
        server.createContext("/secure/admin/report", AccessControlLab::adminReport);
        server.start();
        try {
            int port = server.getAddress().getPort();
            HttpClient client = HttpClient.newHttpClient();
            check(client, port, "usuario-1", user1, "/secure/profile?userId=1", 200,
                    "perfil-ficticio-usuario-1");
            check(client, port, "usuario-1", user1, "/insecure/profile?userId=2", 200,
                    "perfil-ficticio-usuario-2");
            check(client, port, "usuario-1", user1, "/secure/profile?userId=2", 403,
                    "acesso negado");
            check(client, port, "usuario-2", user2, "/secure/profile?userId=2", 200,
                    "perfil-ficticio-usuario-2");
            check(client, port, "usuario-1", user1, "/secure/admin/report", 403,
                    "acesso negado");
            check(client, port, "admin", admin, "/secure/admin/report", 200,
                    "relatorio-administrativo-ficticio");
            check(client, port, "sem-sessao", null, "/secure/profile?userId=1", 401,
                    "autenticacao exigida");
            check(client, port, "admin", admin, "/secure/profile?userId=2", 200,
                    "perfil-ficticio-usuario-2");
            System.out.println("Resultado: 8/8 verificacoes HTTP locais passaram.");
        } finally {
            server.stop(0);
        }
    }
}

