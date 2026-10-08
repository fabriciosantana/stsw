package br.edu.ifsp.stsw.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldExecuteMainStatementsAndApplyMaximumDiscount() {
        int discount = calculator.calculateDiscount(true, 300, true, true);

        assertEquals(40, discount);
    }
}
