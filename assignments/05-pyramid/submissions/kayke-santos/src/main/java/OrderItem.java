public class OrderItem {
    private final String productName;
    private final double unitPrice;
    private final int quantity;

    public OrderItem(String productName, double unitPrice, int quantity) {
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto não pode ser vazio");
        }
        if (unitPrice <= 0) {
            throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return unitPrice * quantity;
    }
}
