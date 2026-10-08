package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionCoverageTest {

    @Test
    void shouldCoverConditionsTrue() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre as condicoes como verdadeiras
        // premiumCustomer = true
        // purchaseAmount >= 100 -> true (300)
        // couponValid = true
        // purchaseAmount >= 200 -> true (300)
        // blackFriday = true
        // purchaseAmount >= 300 -> true (300)
        int discount = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount);
    }

    @Test
    void shouldCoverConditionsFalse() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre as condicoes como falsas
        // premiumCustomer = false
        // purchaseAmount >= 100 -> false (50)
        // couponValid = false
        // purchaseAmount >= 200 -> false (50)
        // blackFriday = false
        // purchaseAmount >= 300 -> false (50)
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }
}
