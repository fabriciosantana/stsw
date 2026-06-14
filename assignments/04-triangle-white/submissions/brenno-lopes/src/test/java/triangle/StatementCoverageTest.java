package triangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Statement Coverage: conjunto mínimo de testes que executa todas as instruções do método.
 *
 * Com apenas 2 testes conseguimos passar por todos os returns:
 * - Um teste que percorre o caminho completo até "Escaleno" (passa por todas as decisões como false)
 * - Um teste que aciona "Lados inválidos" (primeiro return)
 *
 * Porém, para cobrir TODAS as instruções (todos os returns), precisamos de pelo menos 5 testes.
 */
@DisplayName("Statement Coverage — executar todas as instruções")
class StatementCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Cobre return 'Lados inválidos'")
    void cobreLadosInvalidos() {
        assertEquals("Lados inválidos", triangle.classify(0, 5, 5));
    }

    @Test
    @DisplayName("Cobre return 'Não é um triângulo'")
    void cobreNaoETriangulo() {
        assertEquals("Não é um triângulo", triangle.classify(1, 2, 10));
    }

    @Test
    @DisplayName("Cobre return 'Equilátero'")
    void cobreEquilatero() {
        assertEquals("Equilátero", triangle.classify(5, 5, 5));
    }

    @Test
    @DisplayName("Cobre return 'Isósceles'")
    void cobreIsosceles() {
        assertEquals("Isósceles", triangle.classify(5, 5, 3));
    }

    @Test
    @DisplayName("Cobre return 'Escaleno'")
    void cobreEscaleno() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
