package br.edu.ifsp.stsw.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldCoverTrueOutcomeForAllDecisions() {
        int discount = calculator.calculateDiscount(true, 300, true, true);

        assertEquals(40, discount);
    }

    @Test
    void shouldCoverFalseOutcomeForAllDecisions() {
        int discount = calculator.calculateDiscount(false, 50, false, false);

        assertEquals(0, discount);
    }
}
