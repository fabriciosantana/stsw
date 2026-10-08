import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TriangleTest {

    @Test
    @DisplayName("BVA 1: Limites Inferiores (Min-1, Min, Min+1)")
    public void testLowerBoundaries() {
        assertEquals("Lados inválidos", Triangle.classify(0, 100, 100));
        assertEquals("Lados inválidos", Triangle.classify(-1, 100, 100));
        assertEquals("Equilátero", Triangle.classify(1, 1, 1));
        assertEquals("Equilátero", Triangle.classify(2, 2, 2));
    }

    @Test
    @DisplayName("BVA 2: Limites Superiores (Max-1, Max, Max+1)")
    public void testUpperBoundaries() {
        assertEquals("Equilátero", Triangle.classify(199, 199, 199));
        assertEquals("Equilátero", Triangle.classify(200, 200, 200));
        assertEquals("Lados inválidos", Triangle.classify(201, 200, 200));
        assertEquals("Lados inválidos", Triangle.classify(200, 201, 200));
        assertEquals("Lados inválidos", Triangle.classify(200, 200, 201));
    }

    @Test
    @DisplayName("BVA 3: Desigualdade Triangular nas Fronteiras")
    public void testTriangleInequalityBoundaries() {
        assertEquals("Não é um triângulo", Triangle.classify(1, 1, 2));
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 3));
        assertEquals("Não é um triângulo", Triangle.classify(100, 100, 200));
        assertEquals("Não é um triângulo", Triangle.classify(50, 50, 101));
    }

    @Test
    @DisplayName("BVA 4: Tipos de Triângulos (Equilátero, Isósceles, Escaleno)")
    public void testTriangleTypes() {
        assertEquals("Equilátero", Triangle.classify(100, 100, 100));
        assertEquals("Isósceles", Triangle.classify(100, 100, 150));
        assertEquals("Escaleno", Triangle.classify(3, 4, 5));
        assertEquals("Escaleno", Triangle.classify(198, 199, 200));
    }

    @Test
    @DisplayName("BVA 5: Instanciação da classe Triangle")
    public void testConstructor() {
        Triangle triangle = new Triangle();
        assertNotNull(triangle);
    }
}
