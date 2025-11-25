package br.edu.idp.stsw.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TrianguloTest {

    @LoginTest
    void simpleClassificationTest() {
        assertEquals("Isósceles", Triangulo.classificar(5, 5, 3));
    }

    @ParameterizedTest
    @CsvSource({

            "3,3,3,Equilátero",
            "5,5,3,Isósceles",
            "3,4,5,Escaleno",

            "1,2,3,Não é triangulo",
            "0,10,10,Lados inválidos",
            "201,10,10,Lados inválidos"
    })
    void parametrizedClassificationCsv(int a, int b, int c, String esperado) {
        assertEquals(esperado, Triangulo.classificar(a, b, c));
    }

    @ParameterizedTest
    @ValueSource(ints = { 1, 2, 50, 200 })
    void parametrizedEquilateralByValue(int a) {
        assertEquals("Equilátero", Triangulo.classificar(a, a, a));
    }
}
