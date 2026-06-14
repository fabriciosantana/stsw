/**
 * Calcula o desconto de uma compra em uma loja virtual.
 * 
 * Regras de negócio:
 * 1. Se compra >= 100, adicionar 10 pontos de desconto
 * 2. Se cliente premium, adicionar 5 pontos de desconto
 * 3. Se cupom válido E compra >= 200, adicionar 15 pontos de desconto
 * 4. Se Black Friday OU (premium E compra >= 300), adicionar 20 pontos de desconto
 * 5. Desconto máximo permitido é 40 pontos
 */
public class DiscountCalculator {
    /**
     * Calcula o desconto total da compra.
     *
     * @param premiumCustomer se o cliente é premium
     * @param purchaseAmount valor da compra em reais
     * @param couponValid se o cupom informado é válido
     * @param blackFriday se a compra ocorreu na Black Friday
     * @return valor do desconto em pontos (0-40)
     */
    public int calculateDiscount(boolean premiumCustomer,
                                  int purchaseAmount,
                                  boolean couponValid,
                                  boolean blackFriday) {
        
        int discount = 0;

        // Regra 1 -> Se o valor da compra for maior ou igual a `100`, adicionar `10` pontos de desconto.
        if (purchaseAmount >= 100) {
            discount += 10;
        }

        // Regra 2 -> Se o cliente for premium, adicionar `5` pontos de desconto.
        if (premiumCustomer) {
            discount += 5;
        }

        // Regra 3 -> Se o cupom for valido **e** o valor da compra for maior ou igual a `200`, adicionar `15` pontos de desconto.
        if (couponValid && purchaseAmount >= 200) {
            discount += 15;
        }

        // Regra 4 -> Se for Black Friday **ou** se o cliente for premium e o valor da compra for maior ou igual a `300`, adicionar `20` pontos de desconto.
        if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) {
            discount += 20;
        }

        // Regra 5 -> O desconto máximo permitido é `40`.
        // Se o desconto ultrapassar 40, limita a 40
        if (discount > 40) {
            discount = 40;
        }

        return discount;
    }
}