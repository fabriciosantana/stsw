package br.edu.idp.es.stsw.pyramid.service;

import br.edu.idp.es.stsw.pyramid.domain.Order;
import br.edu.idp.es.stsw.pyramid.domain.Product;
import br.edu.idp.es.stsw.pyramid.repository.ProductRepository;

public class OrderService {
    private final ProductRepository productRepository;

    public OrderService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product registerProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Produto obrigatório");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Nome do produto obrigatório");
        }

        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }

        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }

        return productRepository.save(product);
    }

    public double placeOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Pedido obrigatório");
        }

        if (order.quantity() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Product product = productRepository.findById(order.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (product.getStock() < order.quantity()) {
            throw new IllegalStateException("Estoque insuficiente");
        }

        double total = product.getPrice() * order.quantity();

        product.decreaseStock(order.quantity());
        productRepository.save(product);

        return total;
    }

    public int stockOf(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"))
                .getStock();
    }
}
