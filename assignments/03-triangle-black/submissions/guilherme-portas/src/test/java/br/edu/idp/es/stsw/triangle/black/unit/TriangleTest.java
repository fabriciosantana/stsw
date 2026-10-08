package br.edu.idp.es.stsw.triangle.black.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.edu.idp.es.stsw.triangle.black.Triangle;

public class TriangleTest {

    @Test
    @DisplayName("Caso de Uso 1: Triangulo Equilatero")
    void testEquilatero() {
        assertEquals("Equilátero", Triangle.classify(5, 5, 5));
    }

    @Test
    @DisplayName("Caso de Uso 2: Triangulo Isosceles")
    void testIsosceles() {
        assertEquals("Isósceles", Triangle.classify(5, 5, 3));
    }

    @Test
    @DisplayName("Caso de Uso 3: Triangulo Escaleno")
    void testEscaleno() {
        assertEquals("Escaleno", Triangle.classify(5, 4, 3));
    }

    @Test
    @DisplayName("Caso de Uso 4: Lados que nao formam triangulo")
    void testNaoTriangulo() {
        assertEquals("Não é um triângulo", Triangle.classify(1, 2, 3));
    }

    @Test
    @DisplayName("Caso de Uso 5: Lados negativos ou zero")
    void testLadosInvalidos() {
        assertEquals("Lados inválidos", Triangle.classify(-5, 0, 5));
    }

    @Test
    @DisplayName("Lados acima do limite de 200")
    void testLadosAcimaDoLimite() {
        assertEquals("Lados inválidos", Triangle.classify(201, 100, 100));
    }
}
