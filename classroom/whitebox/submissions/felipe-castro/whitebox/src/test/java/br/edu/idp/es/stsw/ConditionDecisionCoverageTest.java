package br.edu.idp.es.stsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionDecisionCoverageTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    // D3=true via >=200=true
    @Test
    void d3Verdadeiro_ambasCondicoesVerdadeiras() {
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));
        // +10 +15
    }

    // D3=false via C3=false, C4=true
    @Test
    void d3Falso_cupomInvalido() {
        assertEquals(10, calc.calculateDiscount(false, 200, false, false));
    }

    // D3=false via C3=true, C4=false
    @Test
    void d3Falso_compraAbaixoDe200() {
        assertEquals(10, calc.calculateDiscount(false, 150, true, false));
    }

    // --- D4: blackFriday || (premiumCustomer && purchaseAmount >= 300) ---

    // D4=true via C5=true
    @Test
    void d4Verdadeiro_blackFriday() {
        assertEquals(30, calc.calculateDiscount(false, 100, false, true));
        // +10 +20
    }

    // D4=true via C5=false, C2=true, C6=true
    @Test
    void d4Verdadeiro_premiumComCompraAlta() {
        assertEquals(35, calc.calculateDiscount(true, 300, false, false));
        // +10 +5 +20
    }

    // D4=false via blackFriday=false, premiumCustomer=false
    @Test
    void d4Falso_semBlackFridayEClienteComum() {
        assertEquals(10, calc.calculateDiscount(false, 300, false, false));
    }

    // D4=false via C6=false
    @Test
    void d4Falso_premiumMasCompraAbaixoDe300() {
        assertEquals(15, calc.calculateDiscount(true, 100, false, false));
        // +10 +5 — D4 false
    }
}
