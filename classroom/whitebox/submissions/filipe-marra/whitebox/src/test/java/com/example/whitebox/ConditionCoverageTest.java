package com.example.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldMakeCouponConditionsTrue() {
        int discount = calculator.calculateDiscount(false, 200, true, false);

        assertEquals(25, discount);
    }

    @Test
    void shouldMakeCouponValidConditionFalse() {
        int discount = calculator.calculateDiscount(false, 200, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldMakePurchaseAmountCouponConditionFalse() {
        int discount = calculator.calculateDiscount(false, 150, true, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldMakeBlackFridayTrue() {
        int discount = calculator.calculateDiscount(false, 50, false, true);

        assertEquals(20, discount);
    }

    @Test
    void shouldMakePremiumAndHighAmountConditionsTrue() {
        int discount = calculator.calculateDiscount(true, 300, false, false);

        assertEquals(35, discount);
    }

    @Test
    void shouldMakePremiumFalseInsideLastDecision() {
        int discount = calculator.calculateDiscount(false, 300, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldMakeHighAmountConditionFalseInsideLastDecision() {
        int discount = calculator.calculateDiscount(true, 250, false, false);

        assertEquals(15, discount);
    }
}
