package br.edu.idp.es.stsw.trianglewhite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DecisionCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Decisão de domínio assume verdadeiro e falso")
    void rangeDecisionTakesBothOutcomes() {
        assertEquals("Lados inválidos", triangle.classify(0, 100, 100));
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
    }

    @Test
    @DisplayName("Decisão da desigualdade triangular assume verdadeiro e falso")
    void triangleValidityDecisionTakesBothOutcomes() {
        assertEquals("Não é um triângulo", triangle.classify(50, 50, 100));
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }

    @Test
    @DisplayName("Decisão de equilátero assume verdadeiro e falso")
    void equilateralDecisionTakesBothOutcomes() {
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
    }

    @Test
    @DisplayName("Decisão de isósceles assume verdadeiro e falso")
    void isoscelesDecisionTakesBothOutcomes() {
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
