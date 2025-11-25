package stsw.jacoco;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import stsw.jacoco.TriangleClassifier.Tipo;

public class TriangleClassifierTest {

    @Test
    void foraDoIntervalo() {
        assertEquals(Tipo.FORA_DO_INTERVALO, TriangleClassifier.classificar(0, 10, 10));
        assertEquals(Tipo.FORA_DO_INTERVALO, TriangleClassifier.classificar(10, 201, 10));
    }

    @Test
    void invalidoDesigualdadeTriangular() {
        assertEquals(Tipo.INVALIDO, TriangleClassifier.classificar(1, 2, 3)); // 1 + 2 == 3
        assertEquals(Tipo.INVALIDO, TriangleClassifier.classificar(1, 10, 20));
    }

    @Test
    void equilatero() {
        assertEquals(Tipo.EQUILATERO, TriangleClassifier.classificar(5, 5, 5));
    }

     @Test
    void isosceles() {
        assertEquals(Tipo.ISOSCELES, TriangleClassifier.classificar(5, 5, 6));
        assertEquals(Tipo.ISOSCELES, TriangleClassifier.classificar(6, 5, 5));
        assertEquals(Tipo.ISOSCELES, TriangleClassifier.classificar(5, 6, 5));
    } 

    @Test
    void escaleno() {
        assertEquals(Tipo.ESCALENO, TriangleClassifier.classificar(4, 5, 6));
    }
}
