public class PaymentService {

    public boolean processPayment(Order order, String creditCardNumber) {
        if (order == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        if (creditCardNumber == null || creditCardNumber.replaceAll("\\s+", "").length() < 13) {
            return false;
        }

        // Regra de negócio: compras acima de R$ 500 recebem 10% de desconto automático
        if (order.getSubtotal() >= 500.0) {
            order.applyDiscount(10.0);
        }

        order.markAsPaid();
        return true;
    }
}
