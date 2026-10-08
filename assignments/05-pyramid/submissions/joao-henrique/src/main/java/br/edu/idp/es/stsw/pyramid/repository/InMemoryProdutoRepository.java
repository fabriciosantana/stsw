package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.Produto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryProdutoRepository implements ProdutoRepository {
    private final Map<Long, Produto> dados = new HashMap<>();

    @Override
    public Optional<Produto> findById(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public Produto save(Produto produto) {
        dados.put(produto.getId(), produto);
        return produto;
    }
}
