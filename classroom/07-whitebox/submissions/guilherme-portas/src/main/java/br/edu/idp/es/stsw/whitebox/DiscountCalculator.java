package br.edu.idp.es.stsw.whitebox;

public class DiscountCalculator {
    public static int calculateDiscount(boolean premiumCustomer, int purchaseAmount, boolean couponValid, boolean blackFriday) {
        int discountAmount = 0;

        if (purchaseAmount >= 100) discountAmount += 10;
        if (premiumCustomer) discountAmount += 5;
        if (couponValid && purchaseAmount >= 200) discountAmount += 15;
        if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) discountAmount += 20;

        discountAmount = Math.min(discountAmount, 40);
        return discountAmount;
    }
}
