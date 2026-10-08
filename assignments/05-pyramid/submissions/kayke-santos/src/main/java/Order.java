import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    public enum Status {
        PENDING,
        PAID,
        CANCELLED
    }

    private final String orderId;
    private final String customerName;
    private final List<OrderItem> items = new ArrayList<>();
    private Status status = Status.PENDING;
    private double discountPercentage = 0.0;

    public Order(String orderId, String customerName) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("ID do pedido não pode ser nulo ou vazio");
        }
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser nulo ou vazio");
        }
        this.orderId = orderId;
        this.customerName = customerName;
    }

    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }
        if (status != Status.PENDING) {
            throw new IllegalStateException("Não é possível adicionar itens a um pedido que não está pendente");
        }
        items.add(item);
    }

    public void applyDiscount(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 50) {
            throw new IllegalArgumentException("Desconto deve estar entre 0% e 50%");
        }
        this.discountPercentage = discountPercentage;
    }

    public double getSubtotal() {
        return items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public double getTotalAmount() {
        double subtotal = getSubtotal();
        return subtotal * (1 - (discountPercentage / 100.0));
    }

    public void markAsPaid() {
        if (status != Status.PENDING) {
            throw new IllegalStateException("Pedido só pode ser pago se estiver PENDING");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Pedido não pode ser pago sem itens");
        }
        this.status = Status.PAID;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Status getStatus() {
        return status;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
