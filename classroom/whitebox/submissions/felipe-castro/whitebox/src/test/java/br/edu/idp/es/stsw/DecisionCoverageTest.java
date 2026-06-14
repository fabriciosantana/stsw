package br.edu.idp.es.stsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DecisionCoverageTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    // D1 = true
    @Test
    void d1Verdadeiro_compraAcimaDe100() {
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));
    }

    // D1 = false
    @Test
    void d1Falso_compraAbaixoDe100() {
        assertEquals(0, calc.calculateDiscount(false, 99, false, false));
    }

    // D2 = true
    @Test
    void d2Verdadeiro_clientePremium() {
        assertEquals(5, calc.calculateDiscount(true, 50, false, false));
    }

    // D2 = false
    @Test
    void d2Falso_clienteComum() {
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));
    }

    // D3 = true
    @Test
    void d3Verdadeiro_cupomValidoECompraAcimaDe200() {
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));
        // +10 (>=100) +15 (cupom e >=200)
    }

    // D3 = false
    @Test
    void d3Falso_semCupom() {
        assertEquals(10, calc.calculateDiscount(false, 200, false, false));
    }

    // D4
    @Test
    void d4Verdadeiro_blackFriday() {
        assertEquals(30, calc.calculateDiscount(false, 100, false, true));
        // +10 (>=100) +20 (blackFriday)
    }

    // D4
    @Test
    void d4Falso_semBlackFridayEClienteComum() {
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));
    }

    // D5
    @Test
    void d5Verdadeiro_descontoUltrapassaTeto() {
        assertEquals(40, calc.calculateDiscount(true, 350, true, false));
    }

    // D5
    @Test
    void d5Falso_descontoAbaixoDoTeto() {
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));
    }
}
