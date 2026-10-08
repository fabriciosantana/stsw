package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(long id);

    Product save(Product product);
}
