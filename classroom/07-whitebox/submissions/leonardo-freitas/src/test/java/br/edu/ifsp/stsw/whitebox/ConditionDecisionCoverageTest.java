package br.edu.ifsp.stsw.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionDecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldCoverAllDecisionsAsTrueAndConditionsAsTrue() {
        int discount = calculator.calculateDiscount(true, 300, true, true);

        assertEquals(40, discount);
    }

    @Test
    void shouldCoverAllDecisionsAsFalseAndMostConditionsAsFalse() {
        int discount = calculator.calculateDiscount(false, 50, false, false);

        assertEquals(0, discount);
    }

    @Test
    void shouldCoverCouponValidTrueWithoutCouponDecisionBecomingTrue() {
        int discount = calculator.calculateDiscount(false, 150, true, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldCoverHighPurchaseTrueWithInvalidCoupon() {
        int discount = calculator.calculateDiscount(false, 250, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldCoverPremiumHighValuePathWhenBlackFridayIsFalse() {
        int discount = calculator.calculateDiscount(true, 300, false, false);

        assertEquals(35, discount);
    }
}
