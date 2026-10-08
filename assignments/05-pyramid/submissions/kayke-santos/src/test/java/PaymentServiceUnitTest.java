import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pirâmide de Testes - Camada 1: Testes Unitários de Serviço (PaymentService)")
public class PaymentServiceUnitTest {

    @Test
    @DisplayName("Unit: Processar pagamento com cartão válido (subtotal < 500)")
    public void testProcessPaymentStandard() {
        PaymentService paymentService = new PaymentService();
        Order order = new Order("ORD-101", "Maria Silva");
        order.addItem(new OrderItem("Fone", 200.0, 1));

        boolean success = paymentService.processPayment(order, "1234567890123456");
        assertTrue(success);
        assertEquals(Order.Status.PAID, order.getStatus());
        assertEquals(0.0, order.getDiscountPercentage());
        assertEquals(200.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Unit: Processar pagamento acima de R$ 500 com 10% de desconto automático")
    public void testProcessPaymentHighAmountAutomaticDiscount() {
        PaymentService paymentService = new PaymentService();
        Order order = new Order("ORD-102", "João Pedro");
        order.addItem(new OrderItem("Cadeira Gamer", 600.0, 1));

        boolean success = paymentService.processPayment(order, "1234567890123456");
        assertTrue(success);
        assertEquals(Order.Status.PAID, order.getStatus());
        assertEquals(10.0, order.getDiscountPercentage());
        assertEquals(540.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Unit: Recusar pagamento com número de cartão inválido")
    public void testProcessPaymentInvalidCardNumber() {
        PaymentService paymentService = new PaymentService();
        Order order = new Order("ORD-103", "Carlos Ramos");
        order.addItem(new OrderItem("Livro", 50.0, 1));

        boolean success = paymentService.processPayment(order, "123");
        assertFalse(success);
        assertEquals(Order.Status.PENDING, order.getStatus());
    }
}
