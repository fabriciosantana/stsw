package br.edu.idp.es.stsw.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Condition/Decision Coverage (Decision_Condition Coverage.md):
 * cada decisao assume TRUE/FALSE e cada condicao atomica assume
 * TRUE/FALSE. Mesmo assim, erros em expressoes "and/or" podem
 * ficar mascarados e nao serem revelados.
 */
class ConditionDecisionCoverageTest {

    @Test
    @DisplayName("Base falsa: D1-F, D2-F, D3-F, D4-F e todas as condicoes F")
    void baseSemDesconto() {
        assertEquals(0, DiscountCalculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    @DisplayName("D1-T e D2-T: amount>=100 e premium verdadeiros")
    void valorMinimoEPremium() {
        assertEquals(15, DiscountCalculator.calculateDiscount(true, 150, false, false));
    }

    @Test
    @DisplayName("D3-T: coupon-T e amount>=200-T")
    void cupomVerdadeiro() {
        assertEquals(25, DiscountCalculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    @DisplayName("D3-F por mascaramento: coupon-T mas amount>=200-F")
    void cupomMascaradoPorValorBaixo() {
        assertEquals(10, DiscountCalculator.calculateDiscount(false, 150, true, false));
    }

    @Test
    @DisplayName("D4-T via segunda via: BF-F, premium-T, amount>=300-T")
    void blackFridayViaPremiumEValorAlto() {
        assertEquals(35, DiscountCalculator.calculateDiscount(true, 300, false, false));
    }

    @Test
    @DisplayName("D4-T e D5-T: tudo verdadeiro com teto de 40")
    void tetoMaximo() {
        assertEquals(40, DiscountCalculator.calculateDiscount(true, 500, true, true));
    }
}
