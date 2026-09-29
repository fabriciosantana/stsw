package triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TriangleTest {
    @Test
    void rejectsSidesOutsideRange() {
        assertEquals("Lados inválidos", Triangle.classify(0, 5, 5));
        assertEquals("Lados inválidos", Triangle.classify(5, -1, 5));
        assertEquals("Lados inválidos", Triangle.classify(5, 5, 201));
    }

    @Test
    void rejectsSidesThatDoNotFormTriangle() {
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 3));
        assertEquals("Não é um triângulo", Triangle.classify(3, 1, 1));
        assertEquals("Não é um triângulo", Triangle.classify(1, 3, 1));
    }

    @Test
    void classifiesEquilateralAtRangeLimits() {
        assertEquals("Equilátero", Triangle.classify(1, 1, 1));
        assertEquals("Equilátero", Triangle.classify(200, 200, 200));
    }

    @Test
    void classifiesEveryIsoscelesPosition() {
        assertEquals("Isósceles", Triangle.classify(5, 5, 3));
        assertEquals("Isósceles", Triangle.classify(5, 3, 5));
        assertEquals("Isósceles", Triangle.classify(3, 5, 5));
    }

    @Test
    void classifiesScalene() {
        assertEquals("Escaleno", Triangle.classify(3, 4, 5));
    }
}
