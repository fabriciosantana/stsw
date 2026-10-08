package triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TriangleTest {
    @ParameterizedTest
    @CsvSource({
            "0, 5, 5",
            "201, 5, 5",
            "5, 0, 5",
            "5, 201, 5",
            "5, 5, 0",
            "5, 5, 201"
    })
    void rejectsEachOutOfRangeSide(int a, int b, int c) {
        assertEquals("Lados inválidos", Triangle.classify(a, b, c));
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1, 2",
            "1, 2, 1",
            "2, 1, 1"
    })
    void rejectsEachFailedTriangleInequality(int a, int b, int c) {
        assertEquals("Não é um triângulo", Triangle.classify(a, b, c));
    }

    @Test
    void classifiesEquilateral() {
        assertEquals("Equilátero", Triangle.classify(5, 5, 5));
    }

    @ParameterizedTest
    @CsvSource({
            "5, 5, 3",
            "5, 3, 5",
            "3, 5, 5"
    })
    void classifiesEachIsoscelesBranch(int a, int b, int c) {
        assertEquals("Isósceles", Triangle.classify(a, b, c));
    }

    @Test
    void classifiesScalene() {
        assertEquals("Escaleno", Triangle.classify(3, 4, 5));
    }
}
