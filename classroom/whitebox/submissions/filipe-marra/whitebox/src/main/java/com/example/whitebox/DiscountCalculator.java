package com.example.whitebox;

public class DiscountCalculator {

    private static final int MAXIMUM_DISCOUNT = 40;

    public int calculateDiscount(
            boolean premiumCustomer,
            int purchaseAmount,
            boolean couponValid,
            boolean blackFriday
    ) {
        int discount = 0;

        if (purchaseAmount >= 100) {
            discount += 10;
        }

        if (premiumCustomer) {
            discount += 5;
        }

        if (couponValid && purchaseAmount >= 200) {
            discount += 15;
        }

        if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) {
            discount += 20;
        }

        return Math.min(discount, MAXIMUM_DISCOUNT);
    }
}
