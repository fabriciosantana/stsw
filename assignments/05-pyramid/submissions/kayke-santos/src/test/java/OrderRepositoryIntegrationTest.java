import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Pirâmide de Testes - Camada 2: Testes de Integração de Repositório (OrderRepository)")
public class OrderRepositoryIntegrationTest {

    private OrderRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new OrderRepository();
    }

    @Test
    @DisplayName("Integration: Salvar e consultar pedido por ID no repositório")
    public void testSaveAndFindOrder() {
        Order order = new Order("ORD-INT-1", "Ana Clara");
        order.addItem(new OrderItem("Webcam", 300.0, 1));
        repository.save(order);

        Optional<Order> retrieved = repository.findById("ORD-INT-1");
        assertTrue(retrieved.isPresent());
        assertEquals("Ana Clara", retrieved.get().getCustomerName());
        assertEquals(300.0, retrieved.get().getTotalAmount());
    }

    @Test
    @DisplayName("Integration: Atualizar status do pedido persistido")
    public void testUpdatePersistedOrder() {
        Order order = new Order("ORD-INT-2", "Lucas Silva");
        order.addItem(new OrderItem("Headset", 150.0, 1));
        repository.save(order);

        order.markAsPaid();
        repository.save(order);

        Optional<Order> retrieved = repository.findById("ORD-INT-2");
        assertTrue(retrieved.isPresent());
        assertEquals(Order.Status.PAID, retrieved.get().getStatus());
    }
}
