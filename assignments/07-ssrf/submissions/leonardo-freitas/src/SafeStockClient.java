import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Exemplo de aplicacao da politica ao criar a requisicao.
 * A rede NAO e chamada pelos testes deste projeto.
 * Para producao: egress firewall + DNS pinning + IP pos-conexao + auditoria.
 */
public final class SafeStockClient {
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    public int getStatus(String rawUrl) throws IOException, InterruptedException {
        URI approved = SsrfGuard.validateStockUrl(rawUrl);
        HttpRequest request = HttpRequest.newBuilder(approved)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
    }
}
