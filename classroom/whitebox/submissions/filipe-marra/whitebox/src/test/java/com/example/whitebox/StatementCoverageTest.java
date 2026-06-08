package com.example.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldExecuteMainStatements() {
        int discount = calculator.calculateDiscount(true, 300, true, false);

        assertEquals(40, discount);
    }
}
