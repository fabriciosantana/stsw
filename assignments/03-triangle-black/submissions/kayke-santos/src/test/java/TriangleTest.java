import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TriangleTest {

    @Test
    @DisplayName("ECP 1: Classes Validadas de Triângulos (Equilátero, Isósceles, Escaleno)")
    public void testEquivalenceClassesValid() {
        assertEquals("Equilátero", Triangle.classify(100, 100, 100));
        assertEquals("Isósceles", Triangle.classify(100, 100, 150));
        assertEquals("Isósceles", Triangle.classify(100, 150, 100));
        assertEquals("Isósceles", Triangle.classify(150, 100, 100));
        assertEquals("Escaleno", Triangle.classify(3, 4, 5));
    }

    @Test
    @DisplayName("ECP 2: Classe Inválida - Desigualdade Triangular (a + b <= c)")
    public void testEquivalenceClassInvalidTriangleInequality() {
        assertEquals("Não é um triângulo", Triangle.classify(1, 1, 2));
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 4));
        assertEquals("Não é um triângulo", Triangle.classify(5, 1, 2));
    }

    @Test
    @DisplayName("ECP 3: Classe Inválida - Lados Fora do Intervalo Permitido (1-200)")
    public void testEquivalenceClassOutofRange() {
        assertEquals("Lados inválidos", Triangle.classify(0, 100, 100));
        assertEquals("Lados inválidos", Triangle.classify(-5, 100, 100));
        assertEquals("Lados inválidos", Triangle.classify(201, 100, 100));
    }

    @Test
    @DisplayName("BVA: Análise de Valores Limite (Min-1, Min, Min+1, Max-1, Max, Max+1)")
    public void testBoundaryValueAnalysis() {
        assertEquals("Equilátero", Triangle.classify(1, 1, 1));
        assertEquals("Equilátero", Triangle.classify(2, 2, 2));
        assertEquals("Equilátero", Triangle.classify(199, 199, 199));
        assertEquals("Equilátero", Triangle.classify(200, 200, 200));
        assertEquals("Lados inválidos", Triangle.classify(201, 200, 200));
        assertEquals("Lados inválidos", Triangle.classify(0, 1, 1));
    }

    @Test
    @DisplayName("Instanciação da classe Triangle")
    public void testConstructor() {
        Triangle triangle = new Triangle();
        assertNotNull(triangle);
    }
}
