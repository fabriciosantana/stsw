package triangulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TriangleClassifierTest {

    @LoginTest
    void testEquilatero() {
        assertEquals("Equilátero", TriangleClassifier.classificar(5, 5, 5));
    }

    @LoginTest
    void testIsosceles() {
        assertEquals("Isósceles", TriangleClassifier.classificar(5, 5, 3));
        assertEquals("Isósceles", TriangleClassifier.classificar(3, 5, 5));
        assertEquals("Isósceles", TriangleClassifier.classificar(5, 3, 5));
    }

    @LoginTest
    void testEscaleno() {
        assertEquals("Escaleno", TriangleClassifier.classificar(3, 4, 5));
    }

    @LoginTest
    void testNaoEhTriangulo() {
        assertEquals("Não é um triângulo", TriangleClassifier.classificar(1, 2, 3));
        assertEquals("Não é um triângulo", TriangleClassifier.classificar(10, 1, 1));
    }

    @LoginTest
    void testLadosInvalidos() {
        assertEquals("Lados inválidos", TriangleClassifier.classificar(0, 5, 5));
        assertEquals("Lados inválidos", TriangleClassifier.classificar(201, 100, 100));
    }

    @ParameterizedTest
    @CsvSource({
        "2,2,2,Equilátero",
        "3,3,5,Isósceles",
        "7,8,9,Escaleno",
        "1,2,3,Não é um triângulo",
        "0,5,5,Lados inválidos",
        "201,10,10,Lados inválidos"
    })
    void testComVariosValores(int a, int b, int c, String esperado) {
        assertEquals(esperado, TriangleClassifier.classificar(a, b, c));
    }
}
