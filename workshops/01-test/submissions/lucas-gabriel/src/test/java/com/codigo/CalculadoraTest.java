package com.codigo;

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculadoraTest {

    private Calculadora calculadora = new Calculadora();

    @Test
    public void testSoma() {
        assertEquals(5, calculadora.soma(2, 3));
    }

   @Test
    public void testSubtrai() {
        assertEquals(1, calculadora.subtrai(3, 2));
    }


    @Test
    public void testMultiplica() {
        assertEquals(6, calculadora.multiplica(2, 3));
    }

    @Test
    public void testDivide() {
        assertEquals(2, calculadora.divide(6, 3));
    }

    @Test(expected = ArithmeticException.class)
    public void testDividePorZero() {
        calculadora.divide(1, 0);
    }

    @Test
    public void testPotencia() {
        assertEquals(9, calculadora.potencia(3, 2));
    }
}
