package br.edu.idp.es.stsw.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Decision Coverage (Decision Branch Coverage.md): cada decisao
 * assume TRUE e FALSE ao menos uma vez. Geralmente satisfaz
 * o Statement Coverage.
 *
 * Decisoes: D1 amount>=100, D2 premium, D3 coupon&&amount>=200,
 * D4 blackFriday||(premium&&amount>=300), D5 teto > 40.
 */
class DecisionCoverageTest {

    @Test
    @DisplayName("D1-F, D2-F, D3-F, D4-F, D5-F: sem desconto")
    void todasDecisoesFalsas() {
        assertEquals(0, DiscountCalculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    @DisplayName("D1-T, D2-T: valor minimo + premium")
    void decisaoValorMinimoEPremiumVerdadeiras() {
        assertEquals(15, DiscountCalculator.calculateDiscount(true, 150, false, false));
    }

    @Test
    @DisplayName("D3-T: cupom valido com valor >= 200")
    void decisaoCupomVerdadeira() {
        assertEquals(25, DiscountCalculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    @DisplayName("D4-T via Black Friday")
    void decisaoBlackFridayVerdadeira() {
        assertEquals(20, DiscountCalculator.calculateDiscount(false, 50, false, true));
    }

    @Test
    @DisplayName("D5-T: teto de 40 aplicado")
    void decisaoTetoVerdadeira() {
        assertEquals(40, DiscountCalculator.calculateDiscount(true, 500, true, true));
    }
}
