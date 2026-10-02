package br.com.guilhermeabade.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ShippingCostCalculatorTest {
    private final ShippingCostCalculator calculator = new ShippingCostCalculator();

    @Test
    void chargesStandardShippingBelowMinimum() {
        assertEquals(new BigDecimal("10.00"),
                calculator.calculate(new BigDecimal("99.99"), false));
    }

    @Test
    void shippingIsFreeAtMinimum() {
        assertEquals(BigDecimal.ZERO,
                calculator.calculate(new BigDecimal("100.00"), false));
    }

    @Test
    void shippingIsFreeAboveMinimum() {
        assertEquals(BigDecimal.ZERO,
                calculator.calculate(new BigDecimal("150.00"), false));
    }

    @Test
    void rejectsNegativeOrderTotal() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(new BigDecimal("-1.00"), false));
    }
}
