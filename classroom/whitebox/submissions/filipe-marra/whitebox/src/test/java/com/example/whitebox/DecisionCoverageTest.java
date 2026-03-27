package com.example.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldMakePurchaseAmountGreaterOrEqual100True() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldMakePurchaseAmountGreaterOrEqual100False() {
        int discount = calculator.calculateDiscount(false, 90, false, false);
        assertEquals(0, discount);
    }

    @Test
    void shouldMakePremiumCustomerTrue() {
        int discount = calculator.calculateDiscount(true, 90, false, false);
        assertEquals(5, discount);
    }

    @Test
    void shouldMakePremiumCustomerFalse() {
        int discount = calculator.calculateDiscount(false, 90, false, false);
        assertEquals(0, discount);
    }

    @Test
    void shouldMakeCouponDecisionTrue() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount);
    }

    @Test
    void shouldMakeCouponDecisionFalse() {
        int discount = calculator.calculateDiscount(false, 200, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldMakeBlackFridayDecisionTrueByBlackFriday() {
        int discount = calculator.calculateDiscount(false, 90, false, true);
        assertEquals(20, discount);
    }

    @Test
    void shouldMakeBlackFridayDecisionFalse() {
        int discount = calculator.calculateDiscount(false, 250, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldMakeMaxDiscountDecisionTrue() {
        int discount = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount);
    }

    @Test
    void shouldMakeMaxDiscountDecisionFalse() {
        int discount = calculator.calculateDiscount(true, 100, false, false);
        assertEquals(15, discount);
    }
}