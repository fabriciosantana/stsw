package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConditionDecisionCoverageTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void allFalse() {
        assertEquals(0,
                calculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    void allTrue() {
        assertEquals(40,
                calculator.calculateDiscount(true, 400, true, true));
    }

    @Test
    void mixedConditions() {
        assertEquals(30,
                calculator.calculateDiscount(true, 200, true, false));
    }
}