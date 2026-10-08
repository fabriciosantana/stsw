import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TriangleTest {

    // =========================================================================
    // 1. TESTES DE CAIXA-BRANCA: CONDIÇÕES DE VALIDAÇÃO DE FAIXA DE VALORES (1 a 200)
    // Avalia individualmente cada uma das 6 subcondições do primeiro 'if'
    // =========================================================================
    @ParameterizedTest(name = "Faixa de valores: a={0}, b={1}, c={2} -> {3}")
    @CsvSource({
        "0, 100, 100, Lados inválidos",    // a < 1
        "201, 100, 100, Lados inválidos",  // a > 200
        "100, 0, 100, Lados inválidos",    // b < 1
        "100, 201, 100, Lados inválidos",  // b > 200
        "100, 100, 0, Lados inválidos",    // c < 1
        "100, 100, 201, Lados inválidos"   // c > 200
    })
    @DisplayName("Caixa-Branca: Cobertura de Condições Individuais de Lados Fora do Intervalo")
    public void testOutOfRangeConditions(int a, int b, int c, String expected) {
        assertEquals(expected, Triangle.classify(a, b, c));
    }

    // =========================================================================
    // 2. TESTES DE CAIXA-BRANCA: DESIGUALDADE TRIANGULAR
    // Avalia individualmente cada uma das 3 condições de não-triângulo
    // =========================================================================
    @ParameterizedTest(name = "Desigualdade Triangular: a={0}, b={1}, c={2} -> {3}")
    @CsvSource({
        "1, 2, 3, Não é um triângulo",    // a + b <= c  (1+2=3)
        "1, 3, 2, Não é um triângulo",    // a + c <= b  (1+2=3)
        "3, 1, 2, Não é um triângulo",    // b + c <= a  (1+2=3)
        "1, 2, 4, Não é um triângulo",    // a + b < c
        "1, 4, 2, Não é um triângulo",    // a + c < b
        "4, 1, 2, Não é um triângulo"     // b + c < a
    })
    @DisplayName("Caixa-Branca: Cobertura de Ramos de Violação da Desigualdade Triangular")
    public void testTriangleInequalityBranches(int a, int b, int c, String expected) {
        assertEquals(expected, Triangle.classify(a, b, c));
    }

    // =========================================================================
    // 3. TESTES DE CAIXA-BRANCA: CLASSIFICAÇÃO DOS TRIÂNGULOS VÁLIDOS
    // Testa combinações de igualdades de lados (Equilátero, Isósceles, Escaleno)
    // =========================================================================
    @ParameterizedTest(name = "Triângulo Válido: a={0}, b={1}, c={2} -> {3}")
    @CsvSource({
        "10, 10, 10, Equilátero",  // a == b && b == c
        "10, 10, 15, Isósceles",   // a == b (a != c, b != c)
        "10, 15, 10, Isósceles",   // a == c (a != b, b != c)
        "15, 10, 10, Isósceles",   // b == c (a != b, a != c)
        "3, 4, 5, Escaleno"        // a != b && a != c && b != c
    })
    @DisplayName("Caixa-Branca: Cobertura de Caminhos de Triângulos Válidos")
    public void testValidTriangleClassification(int a, int b, int c, String expected) {
        assertEquals(expected, Triangle.classify(a, b, c));
    }

    // =========================================================================
    // 4. TESTE DE INSTANCIAÇÃO
    // Cobertura do construtor padrão implícito
    // =========================================================================
    @Test
    @DisplayName("Caixa-Branca: Instanciação da classe Triangle")
    public void testConstructor() {
        Triangle triangle = new Triangle();
        assertNotNull(triangle);
    }

    // =========================================================================
    // 5. TESTES DE CAIXA-BRANCA: MÉTODO MAIN (ENTRADAS VIA ARGS E SCANNER)
    // Cobertura de 100% das instruções e ramos do método main
    // =========================================================================

    @Test
    @DisplayName("Main: 3 argumentos numéricos válidos via CLI")
    public void testMainWithValidArgs() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[]{"3", "4", "5"});
            assertTrue(outContent.toString().contains("Escaleno"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Main: 3 argumentos não numéricos via CLI (NumberFormatException)")
    public void testMainWithInvalidArgsFormat() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[]{"a", "b", "c"});
            assertTrue(outContent.toString().contains("Lados inválidos"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Main: Execução interativa via Scanner (3 valores numéricos válidos)")
    public void testMainInteractiveScannerValid() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("5 5 5\n".getBytes()));
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[0]);
            assertTrue(outContent.toString().contains("Equilátero"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Main: Execução interativa via Scanner (1º valor inválido)")
    public void testMainInteractiveScannerInvalidFirstInt() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("abc 5 5\n".getBytes()));
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[0]);
            assertTrue(outContent.toString().contains("Lados inválidos"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Main: Execução interativa via Scanner (2º valor inválido)")
    public void testMainInteractiveScannerInvalidSecondInt() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("5 abc 5\n".getBytes()));
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[0]);
            assertTrue(outContent.toString().contains("Lados inválidos"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    @DisplayName("Main: Execução interativa via Scanner (3º valor inválido)")
    public void testMainInteractiveScannerInvalidThirdInt() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream("5 5 abc\n".getBytes()));
            System.setOut(new PrintStream(outContent));
            Triangle.main(new String[0]);
            assertTrue(outContent.toString().contains("Lados inválidos"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
