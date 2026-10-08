package br.edu.idp.es.stsw.trianglewhite;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ConditionDecisionCoverageTest {

    private final Triangle triangle = new Triangle();

    @ParameterizedTest(name = "{0}, {1}, {2} => {3}")
    @CsvSource({
        "0,   100, 100, 'Lados inválidos'",
        "201, 100, 100, 'Lados inválidos'",
        "100, 0,   100, 'Lados inválidos'",
        "100, 100, 0,   'Lados inválidos'",
        "50,  50,  100, 'Não é um triângulo'",
        "50,  100, 50,  'Não é um triângulo'",
        "100, 50,  50,  'Não é um triângulo'",
        "100, 100, 100, 'Equilátero'",
        "100, 100, 120, 'Isósceles'",
        "100, 120, 100, 'Isósceles'",
        "120, 100, 100, 'Isósceles'",
        "3,   4,   5,   'Escaleno'"
    })
    @DisplayName("Combina cobertura de decisões e condições atômicas")
    void coversConditionsAndDecisionOutcomes(int a, int b, int c, String expected) {
        assertEquals(expected, triangle.classify(a, b, c));
    }
}
