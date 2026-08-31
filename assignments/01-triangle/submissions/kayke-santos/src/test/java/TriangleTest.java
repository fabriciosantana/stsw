import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TriangleTest {

    @Test
    @DisplayName("Caso de Uso 1: Triângulo Equilátero")
    public void testEquilateralTriangle() {
        assertEquals("Equilátero", Triangle.classify(5, 5, 5));
        assertEquals("Equilátero", Triangle.classify(1, 1, 1));
        assertEquals("Equilátero", Triangle.classify(200, 200, 200));
    }

    @Test
    @DisplayName("Caso de Uso 2: Triângulo Isósceles")
    public void testIsoscelesTriangle() {
        assertEquals("Isósceles", Triangle.classify(5, 5, 3));
        assertEquals("Isósceles", Triangle.classify(5, 3, 5));
        assertEquals("Isósceles", Triangle.classify(3, 5, 5));
    }

    @Test
    @DisplayName("Caso de Uso 3: Triângulo Escaleno")
    public void testScaleneTriangle() {
        assertEquals("Escaleno", Triangle.classify(5, 4, 3));
        assertEquals("Escaleno", Triangle.classify(3, 4, 5));
        assertEquals("Escaleno", Triangle.classify(10, 15, 20));
    }

    @Test
    @DisplayName("Caso de Uso 4: Lados que não formam triângulo")
    public void testNotATriangle() {
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 3));
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 4));
        assertEquals("Não é um triângulo", Triangle.classify(5, 1, 2));
        assertEquals("Não é um triângulo", Triangle.classify(1, 5, 2));
    }

    @Test
    @DisplayName("Caso de Uso 5: Lados Negativos ou Zero")
    public void testInvalidSidesNonPositive() {
        assertEquals("Lados inválidos", Triangle.classify(-5, 0, 5));
        assertEquals("Lados inválidos", Triangle.classify(0, 5, 5));
        assertEquals("Lados inválidos", Triangle.classify(5, 0, 5));
        assertEquals("Lados inválidos", Triangle.classify(5, 5, 0));
        assertEquals("Lados inválidos", Triangle.classify(-1, -1, -1));
    }

    @Test
    @DisplayName("Valores acima do limite (maior que 200)")
    public void testInvalidSidesGreaterThan200() {
        assertEquals("Lados inválidos", Triangle.classify(201, 100, 100));
        assertEquals("Lados inválidos", Triangle.classify(100, 201, 100));
        assertEquals("Lados inválidos", Triangle.classify(100, 100, 201));
    }

    @Test
    @DisplayName("Teste do construtor da classe Triangle")
    public void testConstructor() {
        Triangle triangle = new Triangle();
        assertNotNull(triangle);
    }
}
