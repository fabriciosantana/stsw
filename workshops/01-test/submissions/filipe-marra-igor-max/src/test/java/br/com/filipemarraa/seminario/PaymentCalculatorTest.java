package br.com.filipemarraa.seminario;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentCalculatorTest {

    private final PaymentCalculator calculator = new PaymentCalculator();

    @Test
    void shouldApplyVipDiscount() {
        double result = calculator.calculateDiscount(100.0, "VIP");

        assertEquals(80.0, result);
    }

    @Test
    void shouldApplyPremiumDiscount() {
        double result = calculator.calculateDiscount(100.0, "PREMIUM");

        assertEquals(90.0, result);
    }

    @Test
    void shouldReturnSamePriceForNormalUser() {
        double result = calculator.calculateDiscount(100.0, "NORMAL");

        assertEquals(100.0, result);
    }

    @Test
    void shouldReturnSamePriceWhenUserTypeIsNull() {
        double result = calculator.calculateDiscount(100.0, null);

        assertEquals(100.0, result);
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateDiscount(0, "VIP")
        );
    }
}
