package com.example.whitebox;

public class DiscountCalculator {

    public int calculateDiscount(boolean premiumCustomer, int purchaseAmount, boolean couponValid, boolean blackFriday) {
        //- se o cliente e premium
        //- valor da compra
        //- se o cupom informado e valido
        //- se a compra ocorreu na Black Friday

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
        if (discount > 40) {
            discount = 40;
        }
        return discount;
    }
}
