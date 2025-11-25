package com.triangulo;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TrianguloTest {

    @LoginTest
    void testEquilatero() {
        assertEquals("Equilátero", Triangulo.classificar(5, 5, 5));
    }

    @LoginTest
    void testIsosceles() {
        assertEquals("Isósceles", Triangulo.classificar(5, 5, 3));
        assertEquals("Isósceles", Triangulo.classificar(3, 5, 5));
        assertEquals("Isósceles", Triangulo.classificar(5, 3, 5));
    }

    @LoginTest
    void testEscaleno() {
        assertEquals("Escaleno", Triangulo.classificar(5, 4, 3));
    }

    @LoginTest
    void testNaoTriangulo() {
        assertEquals("Não é um triângulo", Triangulo.classificar(1, 2, 3));
        assertEquals("Não é um triângulo", Triangulo.classificar(2, 2, 4));
    }

    @LoginTest
    void testLadosInvalidosZeroNegativo() {
        assertEquals("Lados inválidos", Triangulo.classificar(0, 5, 5));
        assertEquals("Lados inválidos", Triangulo.classificar(-1, 5, 5));
    }

    @LoginTest
    void testLadosInvalidosAcima200() {
        assertEquals("Lados inválidos", Triangulo.classificar(201, 5, 5));
    }

    @LoginTest
    void testLimitesValidos() {
        assertEquals("Equilátero", Triangulo.classificar(1, 1, 1));
        assertEquals("Equilátero", Triangulo.classificar(200, 200, 200));
    }
}
