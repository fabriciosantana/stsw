package triangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Condition/Decision Coverage: combina os dois critérios anteriores.
 * - Cada decisão deve assumir true e false.
 * - Cada condição atômica deve assumir true e false.
 *
 * Os testes abaixo foram selecionados para satisfazer ambos simultaneamente,
 * garantindo que nenhuma decisão nem condição atômica fique sem os dois valores.
 */
@DisplayName("Condition/Decision Coverage — decisões e condições atômicas true/false")
class ConditionDecisionCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Lado a inválido → D1 true, !isValid(a) true")
    void ladoAInvalido() {
        assertEquals("Lados inválidos", triangle.classify(-1, 10, 10));
    }

    @Test
    @DisplayName("Lado b inválido → !isValid(b) true (curto-circuito: a válido)")
    void ladoBInvalido() {
        assertEquals("Lados inválidos", triangle.classify(10, 0, 10));
    }

    @Test
    @DisplayName("Lado c inválido → !isValid(c) true (curto-circuito: a,b válidos)")
    void ladoCInvalido() {
        assertEquals("Lados inválidos", triangle.classify(10, 10, 201));
    }

    @Test
    @DisplayName("Não forma triângulo → D1 false, D2 true; a+b<=c true")
    void naoFormaTriangulo() {
        assertEquals("Não é um triângulo", triangle.classify(1, 2, 10));
    }

    @Test
    @DisplayName("Não forma triângulo por b+c<=a → terceira condição de isTriangle false")
    void naoFormaTrianguloPorA() {
        assertEquals("Não é um triângulo", triangle.classify(10, 1, 2));
    }

    @Test
    @DisplayName("Equilátero → D3 true (a==b true, b==c true)")
    void equilatero() {
        assertEquals("Equilátero", triangle.classify(10, 10, 10));
    }

    @Test
    @DisplayName("Isósceles a==b → D3 false (b==c false), D4 true (a==b true)")
    void isoscelesAB() {
        assertEquals("Isósceles", triangle.classify(10, 10, 5));
    }

    @Test
    @DisplayName("Isósceles a==c → D4 true via a==c")
    void isoscelesAC() {
        assertEquals("Isósceles", triangle.classify(10, 5, 10));
    }

    @Test
    @DisplayName("Isósceles b==c → D4 true via b==c")
    void isoscelesBC() {
        assertEquals("Isósceles", triangle.classify(5, 10, 10));
    }

    @Test
    @DisplayName("Escaleno → D3 false, D4 false (todas atômicas false)")
    void escaleno() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
