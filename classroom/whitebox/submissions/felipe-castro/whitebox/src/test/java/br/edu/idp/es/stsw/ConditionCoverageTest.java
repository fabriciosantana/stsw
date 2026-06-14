package br.edu.idp.es.stsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionCoverageTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    // C1 = true
    @Test
    void c1Verdadeiro_valorIgualA100() {
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));
    }

    // C1 = false
    @Test
    void c1Falso_valorAbaixoDe100() {
        assertEquals(0, calc.calculateDiscount(false, 99, false, false));
    }

    // C2 = true
    @Test
    void c2Verdadeiro_clientePremium() {
        assertEquals(5, calc.calculateDiscount(true, 50, false, false));
    }

    // C2 = false
    @Test
    void c2Falso_clienteComum() {
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));
    }

    // C3 = true
    @Test
    void c3Verdadeiro_cupomValido() {
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));
    }

    // C3 = false
    @Test
    void c3Falso_cupomInvalido() {
        assertEquals(10, calc.calculateDiscount(false, 200, false, false));
    }

    // C4 = true
    @Test
    void c4Verdadeiro_compraAcimaDe200() {
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));
    }

    // C4 = false
    @Test
    void c4Falso_compraAbaixoDe200() {
        // cupom valido mas compra < 200: D3 = false
        assertEquals(10, calc.calculateDiscount(false, 150, true, false));
    }

    // C5 = true
    @Test
    void c5Verdadeiro_blackFriday() {
        assertEquals(20, calc.calculateDiscount(false, 50, false, true));
    }

    // C5 = false
    @Test
    void c5Falso_semBlackFriday() {
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));
    }

    // C6 = true
    @Test
    void c6Verdadeiro_compraAcimaDe300() {
        assertEquals(35, calc.calculateDiscount(true, 300, false, false));
        // +10 (>=100) +5 (premium) +20 (premium e >=300)
    }

    // C6 = false
    @Test
    void c6Falso_compraAbaixoDe300() {
        assertEquals(15, calc.calculateDiscount(true, 100, false, false));
        // +10 (>=100) +5 (premium) — D4 false pois nao e BF e nao e >=300
    }
}
