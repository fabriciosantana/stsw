package br.edu.ifsp.stsw.whitebox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    void shouldRepresentPathWithoutDiscount() {
        int discount = calculator.calculateDiscount(false, 50, false, false);

        assertEquals(0, discount);
    }

    @Test
    void shouldRepresentPathWithOnlyMinimumPurchaseDiscount() {
        int discount = calculator.calculateDiscount(false, 150, false, false);

        assertEquals(10, discount);
    }

    @Test
    void shouldRepresentPathWithOnlyPremiumDiscount() {
        int discount = calculator.calculateDiscount(true, 50, false, false);

        assertEquals(5, discount);
    }

    @Test
    void shouldRepresentPathWithValidCoupon() {
        int discount = calculator.calculateDiscount(false, 200, true, false);

        assertEquals(25, discount);
    }

    @Test
    void shouldRepresentPathWithBlackFriday() {
        int discount = calculator.calculateDiscount(false, 80, false, true);

        assertEquals(20, discount);
    }

    @Test
    void shouldRepresentPathWithPremiumAndHighPurchaseAmount() {
        int discount = calculator.calculateDiscount(true, 300, false, false);

        assertEquals(35, discount);
    }

    @Test
    void shouldRepresentPathThatReachesMaximumDiscount() {
        int discount = calculator.calculateDiscount(true, 300, true, true);

        assertEquals(40, discount);
    }
}
