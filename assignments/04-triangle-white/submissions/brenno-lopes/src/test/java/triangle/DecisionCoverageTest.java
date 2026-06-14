package triangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Decision Coverage: cada decisão (if) deve assumir true e false pelo menos uma vez.
 *
 * Decisões do método classify:
 *   D1: !isValid(a) || !isValid(b) || !isValid(c)  → true / false
 *   D2: !isTriangle(a, b, c)                        → true / false
 *   D3: a == b && b == c                             → true / false
 *   D4: a == b || a == c || b == c                   → true / false
 */
@DisplayName("Decision Coverage — cada decisão true e false")
class DecisionCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("D1 true: lado inválido entra no if")
    void d1True() {
        assertEquals("Lados inválidos", triangle.classify(0, 5, 5));
    }

    @Test
    @DisplayName("D1 false, D2 true: lados válidos mas não formam triângulo")
    void d1FalseD2True() {
        assertEquals("Não é um triângulo", triangle.classify(1, 2, 10));
    }

    @Test
    @DisplayName("D1 false, D2 false, D3 true: equilátero")
    void d1FalseD2FalseD3True() {
        assertEquals("Equilátero", triangle.classify(10, 10, 10));
    }

    @Test
    @DisplayName("D1 false, D2 false, D3 false, D4 true: isósceles")
    void d1FalseD2FalseD3FalseD4True() {
        assertEquals("Isósceles", triangle.classify(10, 10, 5));
    }

    @Test
    @DisplayName("D1 false, D2 false, D3 false, D4 false: escaleno")
    void d1FalseD2FalseD3FalseD4False() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
