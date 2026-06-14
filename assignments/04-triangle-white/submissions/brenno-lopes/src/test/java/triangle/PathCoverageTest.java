package triangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Path Coverage: caminhos representativos pelo método classify.
 *
 * O método possui 5 decisões independentes em sequência, gerando até 2^5 = 32
 * caminhos teóricos possíveis (muitos são infeasíveis). Os testes abaixo cobrem
 * os caminhos viáveis mais representativos:
 *
 * Caminho 1: a inválido → return "Lados inválidos"
 * Caminho 2: b inválido → return "Lados inválidos"
 * Caminho 3: c inválido → return "Lados inválidos"
 * Caminho 4: a acima do máximo → return "Lados inválidos"
 * Caminho 5: válidos, a+b <= c → return "Não é um triângulo"
 * Caminho 6: válidos, a+c <= b → return "Não é um triângulo"
 * Caminho 7: válidos, b+c <= a → return "Não é um triângulo"
 * Caminho 8: válidos, triângulo, a==b==c → return "Equilátero"
 * Caminho 9: válidos, triângulo, a==b, b!=c → return "Isósceles"
 * Caminho 10: válidos, triângulo, a!=b, a==c → return "Isósceles"
 * Caminho 11: válidos, triângulo, a!=b, a!=c, b==c → return "Isósceles"
 * Caminho 12: válidos, triângulo, todos diferentes → return "Escaleno"
 *
 * Nota: path coverage tende a explodir em número de casos. Para este exercício,
 * os 12 caminhos acima representam todos os caminhos viáveis no método.
 */
@DisplayName("Path Coverage — caminhos representativos")
class PathCoverageTest {

    private final Triangle triangle = new Triangle();

    @ParameterizedTest(name = "classify({0}, {1}, {2}) = {3}")
    @DisplayName("Caminhos representativos pelo método classify")
    @CsvSource({
        // Caminho 1-4: saída por validação de domínio
        "  0,  10,  10, Lados inválidos",
        " 10,  -5,  10, Lados inválidos",
        " 10,  10,   0, Lados inválidos",
        "201, 100, 100, Lados inválidos",

        // Caminho 5-7: saída por desigualdade triangular
        "  1,   2,  10, Não é um triângulo",
        "  1,  10,   2, Não é um triângulo",
        " 10,   1,   2, Não é um triângulo",

        // Caminho 8: equilátero
        " 10,  10,  10, Equilátero",

        // Caminho 9-11: isósceles (cada par de igualdade)
        " 10,  10,   5, Isósceles",
        " 10,   5,  10, Isósceles",
        "  5,  10,  10, Isósceles",

        // Caminho 12: escaleno
        "  3,   4,   5, Escaleno"
    })
    void caminhoRepresentativo(int a, int b, int c, String esperado) {
        assertEquals(esperado, triangle.classify(a, b, c));
    }
}
