package br.edu.idp.es.stsw.trianglewhite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConditionCoverageTest {

    private final Triangle triangle = new Triangle();

    @Test
    @DisplayName("Exercita limite inferior, valor válido e limite superior do domínio")
    void rangeAtomicConditionsTakeTrueAndFalse() {
        assertEquals("Lados inválidos", triangle.classify(0, 100, 100));
        assertEquals("Lados inválidos", triangle.classify(201, 100, 100));
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
    }

    @Test
    @DisplayName("Exercita cada operando da decisão de lados inválidos")
    void eachSideCanIndependentlyBeInvalid() {
        assertEquals("Lados inválidos", triangle.classify(0, 100, 100));
        assertEquals("Lados inválidos", triangle.classify(100, 0, 100));
        assertEquals("Lados inválidos", triangle.classify(100, 100, 0));
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
    }

    @Test
    @DisplayName("Cada desigualdade triangular pode invalidar a entrada")
    void eachTriangleInequalityConditionCanBeTrue() {
        assertEquals("Não é um triângulo", triangle.classify(50, 50, 100));
        assertEquals("Não é um triângulo", triangle.classify(50, 100, 50));
        assertEquals("Não é um triângulo", triangle.classify(100, 50, 50));
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }

    @Test
    @DisplayName("Condições atômicas da decisão de equilátero assumem verdadeiro e falso")
    void equilateralAtomicConditionsTakeTrueAndFalse() {
        assertEquals("Equilátero", triangle.classify(100, 100, 100));
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
        assertEquals("Isósceles", triangle.classify(100, 120, 100));
    }

    @Test
    @DisplayName("Cada igualdade da decisão de isósceles pode ser verdadeira")
    void eachIsoscelesAtomicConditionCanBeTrueAndAllCanBeFalse() {
        assertEquals("Isósceles", triangle.classify(100, 100, 120));
        assertEquals("Isósceles", triangle.classify(100, 120, 100));
        assertEquals("Isósceles", triangle.classify(120, 100, 100));
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }
}
