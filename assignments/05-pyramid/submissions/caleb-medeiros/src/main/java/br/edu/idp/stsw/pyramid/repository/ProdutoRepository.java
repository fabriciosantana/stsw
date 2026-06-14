package br.edu.idp.stsw.pyramid.repository;

import br.edu.idp.stsw.pyramid.domain.Produto;
import java.util.Optional;

/**
 * Interface do repositório de produtos.
 * Permite substituição por implementações reais ou mocks nos testes.
 */
public interface ProdutoRepository {
    Optional<Produto> findById(Long id);
    Produto save(Produto produto);
    void deleteById(Long id);
}
