package com.example.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionDecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldCoverCouponDecisionAsTrue() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount);
    }

    @Test
    void shouldCoverCouponDecisionAsFalseBecauseCouponIsFalse() {
        int discount = calculator.calculateDiscount(false, 200, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldCoverCouponDecisionAsFalseBecauseAmountIsLessThan200() {
        int discount = calculator.calculateDiscount(false, 150, true, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldCoverLastDecisionAsTrueBecauseBlackFridayIsTrue() {
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount);
    }

    @Test
    void shouldCoverLastDecisionAsTrueBecausePremiumAndHighAmountAreTrue() {
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount);
    }

    @Test
    void shouldCoverLastDecisionAsFalse() {
        int discount = calculator.calculateDiscount(false, 250, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldCoverBasicDecisionsForTrueAndFalse() {
        assertEquals(10, calculator.calculateDiscount(false, 100, false, false));
        assertEquals(0, calculator.calculateDiscount(false, 99, false, false));
        assertEquals(5, calculator.calculateDiscount(true, 99, false, false));
        assertEquals(0, calculator.calculateDiscount(false, 99, false, false));
    }

    @Test
    void shouldCoverMaxDiscountLimit() {
        int discount = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount);
    }
}