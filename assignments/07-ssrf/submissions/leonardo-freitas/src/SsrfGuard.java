import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

/**
 * Exemplo DIDATICO de politica restritiva para um endpoint de estoque.
 * Nao realiza requisicoes. Nao substitui DNS pinning, bloqueio de egress
 * nem controles de redirecionamento no cliente HTTP real.
 */
public final class SsrfGuard {
    private static final String APPROVED_HOST = "stock-api.example.test";

    private SsrfGuard() { }

    public static URI validateStockUrl(String input) {
        if (input == null || input.isEmpty() || input.length() > 2048) {
            throw new IllegalArgumentException("URL ausente ou muito longa");
        }
        // Rejeita controles, espacos e barra invertida antes do parser.
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (ch <= 0x20 || ch == 0x7f || ch == '\\') {
                throw new IllegalArgumentException("URL contem caractere proibido");
            }
        }

        final URI uri;
        try {
            uri = new URI(input);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("URL malformada", ex);
        }
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if (scheme == null || !"https".equals(scheme.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Somente HTTPS e permitido");
        }
        if (host == null || !APPROVED_HOST.equals(host.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Host fora da allowlist");
        }
        if (uri.getRawUserInfo() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Userinfo e fragmentos nao permitidos");
        }
        if (uri.getPort() != -1 && uri.getPort() != 443) {
            throw new IllegalArgumentException("Porta nao aprovada");
        }
        // No caso didatico, apenas um numero de produto, sem query e sem encoding.
        if (uri.getRawQuery() != null || uri.getRawPath() == null
                || !uri.getRawPath().matches("/stock/[0-9]{1,10}")) {
            throw new IllegalArgumentException("Caminho ou query nao aprovado");
        }
        return uri;
    }
}
