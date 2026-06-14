package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PathCoverageTest {

    DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void noDiscount() {
        assertEquals(0,
                calculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    void valueDiscountOnly() {
        assertEquals(10,
                calculator.calculateDiscount(false, 150, false, false));
    }

    @Test
    void premiumOnly() {
        assertEquals(5,
                calculator.calculateDiscount(true, 50, false, false));
    }

    @Test
    void couponDiscount() {
        assertEquals(25,
                calculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    void blackFridayDiscount() {
        assertEquals(20,
                calculator.calculateDiscount(false, 50, false, true));
    }

    @Test
    void maxDiscountReached() {
        assertEquals(40,
                calculator.calculateDiscount(true, 500, true, true));
    }
}