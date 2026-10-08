package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<Long, Product> products = new HashMap<>();

    @Override
    public Optional<Product> findById(long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public Product save(Product product) {
        products.put(product.getId(), product);
        return product;
    }
}
