import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Pequenos exemplos defensivos de Engenharia de Software para a atividade I2-06.
 * Não explora nenhum sistema e não altera o Juice Shop ou os laboratórios PortSwigger.
 */
public final class InjectionPreventionDemo {
    private InjectionPreventionDemo() {}

    /**
     * Exemplo de parametrização em JDBC. O e-mail é passado como dado, não
     * interpolado em código SQL. O chamador é responsável por fechar o statement.
     */
    public static PreparedStatement prepareUserLookup(Connection connection, String email)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "SELECT id, email FROM users WHERE email = ?"
        );
        statement.setString(1, email);
        return statement;
    }

    /**
     * Escape para um nó de TEXTO HTML, nunca para HTML rico ou código JavaScript.
     */
    public static String escapeHtmlText(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Exemplo de allow-list de esquemas para links HTTP(S) externos.
     * Em produção, restrições adicionais de destinos podem ser necessárias.
     */
    public static boolean isSafeExternalLink(String value) {
        if (value == null || value.isBlank()) return false;
        try {
            URI uri = URI.create(value.strip());
            String scheme = uri.getScheme();
            return scheme != null
                    && (scheme.equalsIgnoreCase("https") || scheme.equalsIgnoreCase("http"))
                    && uri.getHost() != null;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }
}