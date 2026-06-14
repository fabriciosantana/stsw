package triangle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Condition Coverage: cada condição atômica deve assumir true e false pelo menos uma vez.
 *
 * Condições atômicas identificadas:
 *
 * Em isValid: side >= MIN_SIDE, side <= MAX_SIDE
 *   C1: a inválido (< 1)   → !isValid(a) = true
 *   C2: b inválido (< 1)   → !isValid(b) = true
 *   C3: c inválido (> 200)  → !isValid(c) = true
 *   C4: todos válidos        → !isValid = false para todos
 *
 * Em isTriangle: (a+b > c), (a+c > b), (b+c > a)
 *   C5: a+b <= c (falha primeira condição)
 *   C6: a+c <= b (falha segunda condição)
 *   C7: b+c <= a (falha terceira condição)
 *   C8: todas true (forma triângulo)
 *
 * Em equilátero: a == b, b == c
 *   C9:  a == b true, b == c true  → equilátero
 *   C10: a == b true, b == c false → não equilátero
 *   C11: a == b false              → não equilátero
 *
 * Em isósceles: a == b, a == c, b == c
 *   C12: a == b true  → isósceles
 *   C13: a == c true  → isósceles
 *   C14: b == c true  → isósceles
 *   C15: todas false  → escaleno
 */
@DisplayName("Condition Coverage — cada condição atômica true e false")
class ConditionCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("C1: !isValid(a) true — lado a abaixo do mínimo")
    void c1_aInvalido() {
        assertEquals("Lados inválidos", triangle.classify(0, 10, 10));
    }

    @Test
    @DisplayName("C2: !isValid(b) true — lado b abaixo do mínimo")
    void c2_bInvalido() {
        assertEquals("Lados inválidos", triangle.classify(10, 0, 10));
    }

    @Test
    @DisplayName("C3: !isValid(c) true — lado c acima do máximo")
    void c3_cInvalido() {
        assertEquals("Lados inválidos", triangle.classify(10, 10, 201));
    }

    @Test
    @DisplayName("C4: todos válidos — isValid false para todos")
    void c4_todosValidos() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }

    @Test
    @DisplayName("C5: a+b <= c — falha na primeira condição de isTriangle")
    void c5_falhaABmaiorC() {
        assertEquals("Não é um triângulo", triangle.classify(1, 2, 10));
    }

    @Test
    @DisplayName("C6: a+c <= b — falha na segunda condição de isTriangle")
    void c6_falhaACmaiorB() {
        assertEquals("Não é um triângulo", triangle.classify(1, 10, 2));
    }

    @Test
    @DisplayName("C7: b+c <= a — falha na terceira condição de isTriangle")
    void c7_falhaBCmaiorA() {
        assertEquals("Não é um triângulo", triangle.classify(10, 1, 2));
    }

    @Test
    @DisplayName("C9: a==b true e b==c true — equilátero")
    void c9_equilatero() {
        assertEquals("Equilátero", triangle.classify(7, 7, 7));
    }

    @Test
    @DisplayName("C10: a==b true e b==c false — isósceles (a==b)")
    void c10_abIgualBcDiferente() {
        assertEquals("Isósceles", triangle.classify(7, 7, 5));
    }

    @Test
    @DisplayName("C13: a==c true — isósceles (a==c)")
    void c13_acIgual() {
        assertEquals("Isósceles", triangle.classify(7, 5, 7));
    }

    @Test
    @DisplayName("C14: b==c true — isósceles (b==c)")
    void c14_bcIgual() {
        assertEquals("Isósceles", triangle.classify(5, 7, 7));
    }

    @Test
    @DisplayName("C15: a!=b, a!=c, b!=c — escaleno (todas false)")
    void c15_escaleno() {
        assertEquals("Escaleno", triangle.classify(4, 5, 6));
    }
}
