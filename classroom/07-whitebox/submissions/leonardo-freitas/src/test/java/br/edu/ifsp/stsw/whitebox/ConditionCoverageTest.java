package br.edu.ifsp.stsw.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldCoverCouponConditionAsTrueAndHighPurchaseConditionAsFalse() {
        int discount = calculator.calculateDiscount(false, 150, true, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldCoverCouponConditionAsFalseAndHighPurchaseConditionAsTrue() {
        int discount = calculator.calculateDiscount(false, 250, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldCoverBlackFridayConditionAsTrue() {
        int discount = calculator.calculateDiscount(false, 80, false, true);

        assertEquals(20, discount);
    }

    @Test
    void shouldCoverPremiumHighValueConditionAsTrueWhenBlackFridayIsFalse() {
        int discount = calculator.calculateDiscount(true, 300, false, false);

        assertEquals(35, discount);
    }

    @Test
    void shouldCoverPremiumConditionAsFalseInCompositeDecision() {
        int discount = calculator.calculateDiscount(false, 300, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldCoverPurchaseAmountAtLeastThreeHundredConditionAsFalse() {
        int discount = calculator.calculateDiscount(true, 250, false, false);

        assertEquals(15, discount);
    }
}
