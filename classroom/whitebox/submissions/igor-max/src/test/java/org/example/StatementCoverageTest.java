package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatementCoverageTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldExecuteMostStatements() {
        int discount = calculator.calculateDiscount(
                true,
                350,
                true,
                true
        );

        assertEquals(40, discount);
    }
}