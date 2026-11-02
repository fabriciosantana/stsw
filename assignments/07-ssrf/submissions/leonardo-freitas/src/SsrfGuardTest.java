import java.net.URI;

/** Testes locais da defesa; nao sao testes de resolucao do PortSwigger/Juice Shop. */
public final class SsrfGuardTest {
    private static int checks;

    public static void main(String[] args) {
        permit("https://stock-api.example.test/stock/1");
        permit("https://STOCK-API.EXAMPLE.TEST/stock/123");
        permit("https://stock-api.example.test:443/stock/9999999999");

        reject("http://stock-api.example.test/stock/1");
        reject("http://localhost/admin");
        reject("http://127.0.0.1/admin");
        reject("http://127.1/admin");
        reject("http://192.168.0.12:8080/admin");
        reject("http://169.254.169.254/latest/meta-data/");
        reject("https://evil.example.test/stock/1");
        reject("https://stock-api.example.test.evil.test/stock/1");
        reject("https://stock-api.example.test@evil.test/stock/1");
        reject("https://user@stock-api.example.test/stock/1");
        reject("https://stock-api.example.test:8080/stock/1");
        reject("https://stock-api.example.test/admin");
        reject("https://stock-api.example.test/stock/../admin");
        reject("https://stock-api.example.test/%73tock/1");
        reject("https://stock-api.example.test/stock/1?next=http://localhost/admin");
        reject("https://stock-api.example.test/stock/1#http://localhost/admin");
        reject("https://stock-api.example.test/stock/1\\@evil.test");
        reject("https://stock-api.example.test/stock/1\nX:bad");
        reject("https://stock-api.example.test/stock/");
        reject("https://stock-api.example.test/stock/-5");
        reject("file:///etc/passwd");
        reject("");
        reject(null);

        System.out.println("OK: " + checks + " verificacoes da politica SSRF passaram.");
    }

    private static void permit(String url) {
        URI actual = SsrfGuard.validateStockUrl(url);
        if (!url.equals(actual.toString())) throw new AssertionError("URI inesperada: " + actual);
        checks++;
    }

    private static void reject(String url) {
        try {
            SsrfGuard.validateStockUrl(url);
        } catch (IllegalArgumentException expected) {
            checks++;
            return;
        }
        throw new AssertionError("Aceitou URL insegura: " + url);
    }
}
