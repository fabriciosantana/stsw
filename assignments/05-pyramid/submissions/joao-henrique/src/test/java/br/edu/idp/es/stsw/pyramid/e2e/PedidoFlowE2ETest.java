package br.edu.idp.es.stsw.pyramid.e2e;

import br.edu.idp.es.stsw.pyramid.domain.Pedido;
import br.edu.idp.es.stsw.pyramid.domain.Produto;
import br.edu.idp.es.stsw.pyramid.repository.InMemoryProdutoRepository;
import br.edu.idp.es.stsw.pyramid.service.PedidoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoFlowE2ETest {

    @Test
    void testFluxoCompletoDoPedido() {
        InMemoryProdutoRepository repo = new InMemoryProdutoRepository();
        PedidoService service = new PedidoService(repo);

        Produto p1 = service.cadastrarProduto(new Produto(1L, "Fone", 150.0, 10));
        Produto p2 = service.cadastrarProduto(new Produto(2L, "Mousepad", 50.0, 20));

        Pedido pedido1 = new Pedido(100L, p1, 2);
        Pedido pedido2 = new Pedido(101L, p2, 4);

        double total1 = service.processarPedido(pedido1);
        double total2 = service.processarPedido(pedido2);

        assertEquals(300.0, total1);
        assertEquals(200.0, total2);
        assertEquals(8, repo.findById(1L).get().getEstoque());
        assertEquals(16, repo.findById(2L).get().getEstoque());
    }
}
