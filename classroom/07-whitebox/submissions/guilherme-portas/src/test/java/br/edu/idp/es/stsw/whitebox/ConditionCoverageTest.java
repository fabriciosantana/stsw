package br.edu.idp.es.stsw.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Condition Coverage (Condition Coverage.md): cada condicao atomica
 * assume TRUE e FALSE ao menos uma vez. Geralmente superior ao
 * Decision Coverage, mas nem sempre satisfaz a decisao, pois
 * condicoes em "and/or" podem mascarar umas as outras.
 */
class ConditionCoverageTest {

    @Test
    @DisplayName("coupon-T e amount>=200-T: decisao do cupom verdadeira")
    void cupomVerdadeiroEValorAlto() {
        assertEquals(25, DiscountCalculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    @DisplayName("coupon-F mascara amount>=200-T: decisao do cupom falsa")
    void cupomFalsoMascaraValorAlto() {
        assertEquals(10, DiscountCalculator.calculateDiscount(false, 200, false, false));
    }

    @Test
    @DisplayName("coupon-T e amount>=200-F: decisao do cupom falsa")
    void cupomVerdadeiroMasValorBaixo() {
        assertEquals(10, DiscountCalculator.calculateDiscount(false, 150, true, false));
    }

    @Test
    @DisplayName("blackFriday-T: condicao da Black Friday verdadeira")
    void blackFridayVerdadeira() {
        assertEquals(20, DiscountCalculator.calculateDiscount(false, 50, false, true));
    }

    @Test
    @DisplayName("blackFriday-F, premium-T, amount>=300-T: decisao via premium+valor")
    void premiumEValorAltoSemBlackFriday() {
        assertEquals(35, DiscountCalculator.calculateDiscount(true, 300, false, false));
    }

    @Test
    @DisplayName("blackFriday-F, premium-T, amount>=300-F: decisao da Black Friday falsa")
    void premiumVerdadeiroMasValorBaixo() {
        assertEquals(15, DiscountCalculator.calculateDiscount(true, 250, false, false));
    }

    @Test
    @DisplayName("blackFriday-F, premium-F, amount>=300-T: && mascara valor alto")
    void semPremiumMascaraValorAlto() {
        assertEquals(10, DiscountCalculator.calculateDiscount(false, 300, false, false));
    }
}
