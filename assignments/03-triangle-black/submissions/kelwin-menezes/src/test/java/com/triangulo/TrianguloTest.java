package com.triangulo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TrianguloTest {

    Triangulo triangulo = new Triangulo();

    @Test
    void testeBVA_LimiteMinimo() {
        assertEquals("Equilátero", triangulo.classificar(1, 1, 1));
    }

    @Test
    void testeBVA_AbaixoDoLimite() {
        assertEquals("Lados inválidos", triangulo.classificar(0, 100, 100));
    }
}