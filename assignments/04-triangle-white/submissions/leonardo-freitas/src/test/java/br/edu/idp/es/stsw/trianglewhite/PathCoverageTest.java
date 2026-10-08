package br.edu.idp.es.stsw.trianglewhite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PathCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Caminho P1: encerra na validação de domínio")
    void pathInvalidDomain() {
        assertEquals("Lados inválidos", triangle.classify(0, 100, 100));
    }

    @Test
    @DisplayName("Caminho P2: passa domínio e encerra na desigualdade triangular")
    void pathNotATriangle() {
        assertEquals("Não é um triângulo", triangle.classify(50, 50, 100));
    }

    @Test
    @DisplayName("Caminho P3: passa validações e encerra como equilátero")
    void pathEquilateral() {
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
    }

    @Test
    @DisplayName("Caminho P4: falha em equilátero e encerra como isósceles")
    void pathIsosceles() {
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
    }

    @Test
    @DisplayName("Caminho P5: percorre todas as decisões e encerra como escaleno")
    void pathScalene() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
