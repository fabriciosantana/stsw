package com.classroom.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ConditionDecisionCoverageTest {

    @Test
    void testConditionDecision() {
        DiscountCalculator calc = new DiscountCalculator();

        // Regra 3: (couponValid && purchaseAmount >= 200)
        // 1. Ambos T -> Decisão T
        assertEquals(30, calc.calculateDiscount(true, 200, true, false)); 
        // 2. coupon F, amount T -> Decisão F
        assertEquals(10, calc.calculateDiscount(false, 200, false, false));
        // 3. coupon T, amount F -> Decisão F
        assertEquals(10, calc.calculateDiscount(false, 150, true, false));

        // Regra 4: (blackFriday || (premiumCustomer && purchaseAmount >= 300))
        // 1. BlackFriday T -> Decisão T
        assertEquals(30, calc.calculateDiscount(false, 100, false, true));
        // 2. BlackFriday F, Premium T, Amount 300 -> Decisão T
        assertEquals(35, calc.calculateDiscount(true, 300, false, false));
    }
}