package com.classroom.whitebox;

public class DiscountCalculator {

    public int calculateDiscount(boolean premiumCustomer, int purchaseAmount, 
                                 boolean couponValid, boolean blackFriday) {
        int discount = 0;

        // Regra 1: Valor >= 100 -> +10 pontos
        if (purchaseAmount >= 100) {
            discount += 10;
        }

        // Regra 2: Cliente Premium -> +5 pontos
        if (premiumCustomer) {
            discount += 5;
        }

        // Regra 3: Cupom válido E Valor >= 200 -> +15 pontos
        if (couponValid && purchaseAmount >= 200) {
            discount += 15;
        }

        // Regra 4: Black Friday OU (Premium E Valor >= 300) -> +20 pontos
        if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) {
            discount += 20;
        }

        // Regra 5: Teto máximo permitido de 40
        if (discount > 40) {
            discount = 40;
        }

        return discount;
    }
}