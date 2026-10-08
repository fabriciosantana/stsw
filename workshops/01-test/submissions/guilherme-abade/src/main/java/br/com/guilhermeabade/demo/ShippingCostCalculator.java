package br.com.guilhermeabade.demo;

import java.math.BigDecimal;
import java.util.Objects;

public class ShippingCostCalculator {
    private static final BigDecimal FREE_SHIPPING_MINIMUM = new BigDecimal("100.00");
    private static final BigDecimal STANDARD_SHIPPING = new BigDecimal("10.00");
    private static final BigDecimal EXPRESS_SURCHARGE = new BigDecimal("15.00");

    public BigDecimal calculate(BigDecimal orderTotal, boolean express) {
        Objects.requireNonNull(orderTotal, "orderTotal");
        if (orderTotal.signum() < 0) {
            throw new IllegalArgumentException("Order total cannot be negative");
        }

        BigDecimal shipping = orderTotal.compareTo(FREE_SHIPPING_MINIMUM) >= 0
                ? BigDecimal.ZERO
                : STANDARD_SHIPPING;

        if (express) {
            shipping = shipping.add(EXPRESS_SURCHARGE);
        }

        return shipping;
    }
}
