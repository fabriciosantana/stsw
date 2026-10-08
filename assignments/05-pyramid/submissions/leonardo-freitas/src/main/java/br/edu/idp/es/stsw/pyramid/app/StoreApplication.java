package br.edu.idp.es.stsw.pyramid.app;

import br.edu.idp.es.stsw.pyramid.domain.Order;
import br.edu.idp.es.stsw.pyramid.domain.Product;
import br.edu.idp.es.stsw.pyramid.repository.InMemoryProductRepository;
import br.edu.idp.es.stsw.pyramid.service.OrderService;

public class StoreApplication {
    private final OrderService orderService;

    public StoreApplication() {
        this.orderService = new OrderService(new InMemoryProductRepository());
    }

    public void registerProduct(long id, String name, double price, int stock) {
        orderService.registerProduct(new Product(id, name, price, stock));
    }

    public double checkout(long productId, int quantity) {
        return orderService.placeOrder(new Order(productId, quantity));
    }

    public int stockOf(long productId) {
        return orderService.stockOf(productId);
    }
}
