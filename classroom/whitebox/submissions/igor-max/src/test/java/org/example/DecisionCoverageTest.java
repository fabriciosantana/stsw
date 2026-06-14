package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecisionCoverageTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void purchaseBelow100() {
        assertEquals(0,
                calculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    void purchaseAbove100() {
        assertEquals(10,
                calculator.calculateDiscount(false, 100, false, false));
    }

    @Test
    void premiumCustomer() {
        assertEquals(15,
                calculator.calculateDiscount(true, 100, false, false));
    }

    @Test
    void couponDecisionTrue() {
        assertEquals(25,
                calculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    void blackFridayDecisionTrue() {
        assertEquals(20,
                calculator.calculateDiscount(false, 50, false, true));
    }
}