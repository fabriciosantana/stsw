package com.example.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldHandlePathWithoutAnyDiscount() {
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

    @Test
    void shouldHandlePathWithOnlyMinimumAmountDiscount() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    @Test
    void shouldHandlePathWithOnlyPremiumDiscount() {
        int discount = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, discount);
    }

    @Test
    void shouldHandlePathWithCouponDiscount() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount);
    }

    @Test
    void shouldHandlePathWithBlackFridayDiscount() {
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount);
    }

    @Test
    void shouldHandlePathWithPremiumAndHighAmountRule() {
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount);
    }

    @Test
    void shouldHandlePathThatHitsMaximumDiscountCap() {
        int discount = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount);
    }
}