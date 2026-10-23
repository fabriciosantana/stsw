/**
 * Testes independentes de bibliotecas externas para a pequena demonstração
 * defensiva. Estes testes NÃO são evidências de resolução dos 17 labs.
 */
public final class InjectionPreventionDemoTest {
    public static void main(String[] args) {
        assertEqual("&lt;script&gt;alert(1)&lt;/script&gt;",
                InjectionPreventionDemo.escapeHtmlText("<script>alert(1)</script>"));
        assertEqual("a&amp;b", InjectionPreventionDemo.escapeHtmlText("a&b"));
        assertEqual("&quot;&#39;", InjectionPreventionDemo.escapeHtmlText("\"'"));
        assertEqual("", InjectionPreventionDemo.escapeHtmlText(null));

        assertTrue(InjectionPreventionDemo.isSafeExternalLink("https://example.org/a?q=1"));
        assertTrue(InjectionPreventionDemo.isSafeExternalLink("http://example.org/"));
        assertFalse(InjectionPreventionDemo.isSafeExternalLink("javascript:alert(1)"));
        assertFalse(InjectionPreventionDemo.isSafeExternalLink("data:text/html,hi"));
        assertFalse(InjectionPreventionDemo.isSafeExternalLink("/relative-path"));
        assertFalse(InjectionPreventionDemo.isSafeExternalLink("https:sem-host"));
        System.out.println("OK: 10 verificações defensivas passaram.");
    }

    private static void assertEqual(String expected, String actual) {
        if (!expected.equals(actual)) throw new AssertionError("esperado=" + expected + " recebido=" + actual);
    }

    private static void assertTrue(boolean value) {
        if (!value) throw new AssertionError("Esperado true");
    }

    private static void assertFalse(boolean value) {
        if (value) throw new AssertionError("Esperado false");
    }
}