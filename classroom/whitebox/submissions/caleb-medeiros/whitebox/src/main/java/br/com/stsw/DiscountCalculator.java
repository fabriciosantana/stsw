package br.com.stsw.whitebox;

public class DiscountCalculator {

    public int calculateDiscount(boolean premiumCustomer,
                                 int purchaseAmount,
                                 boolean couponValid,
                                 boolean blackFriday) {
        int discount = 0;

        // 1. Se o valor da compra for maior ou igual a 100, adicionar 10 pontos de desconto.
        if (purchaseAmount >= 100) {
            discount += 10;
        }

        // 2. Se o cliente for premium, adicionar 5 pontos de desconto.
        if (premiumCustomer) {
            discount += 5;
        }

        // 3. Se o cupom for valido e o valor da compra for maior ou igual a 200, adicionar 15 pontos de desconto.
        if (couponValid && purchaseAmount >= 200) {
            discount += 15;
        }

        // 4. Se for Black Friday ou se o cliente for premium e o valor da compra for maior ou igual a 300, adicionar 20 pontos de desconto.
        if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) {
            discount += 20;
        }

        // 5. O desconto maximo permitido e 40.
        if (discount > 40) {
            discount = 40;
        }

        return discount;
    }
}
