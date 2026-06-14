package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConditionCoverageTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void couponTrueAmountTrue() {
        assertEquals(25,
                calculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    void couponFalseAmountTrue() {
        assertEquals(10,
                calculator.calculateDiscount(false, 200, false, false));
    }

    @Test
    void couponTrueAmountFalse() {
        assertEquals(10,
                calculator.calculateDiscount(false, 150, true, false));
    }

    @Test
    void blackFridayTrue() {
        assertEquals(20,
                calculator.calculateDiscount(false, 50, false, true));
    }

    @Test
    void premiumAndHighAmount() {
        assertEquals(35,
                calculator.calculateDiscount(true, 300, false, false));
    }
}
