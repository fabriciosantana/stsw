package br.edu.idp.es.stsw.trianglewhite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatementCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Executa retorno de lados inválidos")
    void coversInvalidSidesReturn() {
        assertEquals("Lados inválidos", triangle.classify(0, 100, 100));
    }

    @Test
    @DisplayName("Executa retorno de não triângulo")
    void coversNotTriangleReturn() {
        assertEquals("Não é um triângulo", triangle.classify(50, 50, 100));
    }

    @Test
    @DisplayName("Executa retorno de equilátero")
    void coversEquilateralReturn() {
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
    }

    @Test
    @DisplayName("Executa retorno de isósceles")
    void coversIsoscelesReturn() {
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
    }

    @Test
    @DisplayName("Executa retorno final de escaleno")
    void coversScaleneReturn() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
