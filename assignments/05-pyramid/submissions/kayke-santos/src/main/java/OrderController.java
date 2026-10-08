import java.util.Optional;

public class OrderController {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public OrderController(OrderRepository orderRepository, PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }

    public Order createOrder(String orderId, String customerName) {
        Order order = new Order(orderId, customerName);
        orderRepository.save(order);
        return order;
    }

    public boolean addItemToOrder(String orderId, String productName, double price, int quantity) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }
        Order order = optionalOrder.get();
        order.addItem(new OrderItem(productName, price, quantity));
        orderRepository.save(order);
        return true;
    }

    public boolean payOrder(String orderId, String creditCardNumber) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return false;
        }
        Order order = optionalOrder.get();
        boolean success = paymentService.processPayment(order, creditCardNumber);
        if (success) {
            orderRepository.save(order);
        }
        return success;
    }

    public Optional<Order> getOrderDetails(String orderId) {
        return orderRepository.findById(orderId);
    }
}
