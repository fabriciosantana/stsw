package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.Produto;
import java.util.Optional;

public interface ProdutoRepository {
    Optional<Produto> findById(Long id);
    Produto save(Produto produto);
}
