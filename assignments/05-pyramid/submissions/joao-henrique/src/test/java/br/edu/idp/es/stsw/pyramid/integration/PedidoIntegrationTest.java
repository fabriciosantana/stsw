package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.domain.Pedido;
import br.edu.idp.es.stsw.pyramid.domain.Produto;
import br.edu.idp.es.stsw.pyramid.repository.InMemoryProdutoRepository;
import br.edu.idp.es.stsw.pyramid.service.PedidoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoIntegrationTest {

    @Test
    void testIntegracaoComRepositorioReal() {
        InMemoryProdutoRepository repo = new InMemoryProdutoRepository();
        PedidoService service = new PedidoService(repo);

        Produto p = new Produto(10L, "Notebook", 3500.0, 5);
        service.cadastrarProduto(p);

        Pedido pedido = new Pedido(1L, p, 2);
        double total = service.processarPedido(pedido);

        assertEquals(7000.0, total);
        assertEquals(3, repo.findById(10L).get().getEstoque());
    }
}
