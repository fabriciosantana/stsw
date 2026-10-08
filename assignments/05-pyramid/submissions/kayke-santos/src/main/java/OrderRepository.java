import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OrderRepository {
    private final Map<String, Order> database = new HashMap<>();

    public void save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        database.put(order.getOrderId(), order);
    }

    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(database.get(orderId));
    }

    public int count() {
        return database.size();
    }

    public void clear() {
        database.clear();
    }
}
