package br.edu.idp.stsw.pyramid.repository;

import br.edu.idp.stsw.pyramid.domain.Produto;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Repositório em memória — usado nos testes de integração e na execução local.
 */
public class InMemoryProdutoRepository implements ProdutoRepository {

    private final Map<Long, Produto> storage = new HashMap<>();

    @Override
    public Optional<Produto> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Produto save(Produto produto) {
        storage.put(produto.getId(), produto);
        return produto;
    }

    @Override
    public void deleteById(Long id) {
        storage.remove(id);
    }
}
