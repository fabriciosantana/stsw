import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pirâmide de Testes - Camada 1: Testes Unitários de Domínio (Order & OrderItem)")
public class OrderUnitTest {

    @Test
    @DisplayName("Unit: Criar item válido e calcular subtotal")
    public void testOrderItemSubtotal() {
        OrderItem item = new OrderItem("Notebook", 2500.0, 2);
        assertEquals("Notebook", item.getProductName());
        assertEquals(2500.0, item.getUnitPrice());
        assertEquals(2, item.getQuantity());
        assertEquals(5000.0, item.getSubtotal());
    }

    @Test
    @DisplayName("Unit: Exceções em OrderItem com entradas inválidas")
    public void testOrderItemValidations() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem("", 100.0, 1));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem("Mouse", 0.0, 1));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem("Mouse", 100.0, 0));
    }

    @Test
    @DisplayName("Unit: Criar pedido, adicionar itens e calcular total sem desconto")
    public void testOrderTotalWithoutDiscount() {
        Order order = new Order("ORD-001", "Kayke Santos");
        order.addItem(new OrderItem("Teclado", 150.0, 2));
        order.addItem(new OrderItem("Mouse", 50.0, 1));

        assertEquals(350.0, order.getSubtotal());
        assertEquals(350.0, order.getTotalAmount());
        assertEquals(Order.Status.PENDING, order.getStatus());
    }

    @Test
    @DisplayName("Unit: Aplicar desconto percentual válido no pedido")
    public void testOrderApplyDiscount() {
        Order order = new Order("ORD-002", "Kayke Santos");
        order.addItem(new OrderItem("Monitor", 1000.0, 1));

        order.applyDiscount(20.0);
        assertEquals(1000.0, order.getSubtotal());
        assertEquals(800.0, order.getTotalAmount());
    }

    @Test
    @DisplayName("Unit: Tentar pagar pedido sem itens lança exceção")
    public void testPayEmptyOrderThrowsException() {
        Order order = new Order("ORD-003", "Kayke Santos");
        assertThrows(IllegalStateException.class, order::markAsPaid);
    }
}
