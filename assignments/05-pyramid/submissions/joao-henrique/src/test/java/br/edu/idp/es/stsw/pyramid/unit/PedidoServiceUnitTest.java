package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.domain.Pedido;
import br.edu.idp.es.stsw.pyramid.domain.Produto;
import br.edu.idp.es.stsw.pyramid.repository.ProdutoRepository;
import br.edu.idp.es.stsw.pyramid.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceUnitTest {

    private ProdutoRepository repository;
    private PedidoService service;

    @BeforeEach
    void setUp() {
        repository = mock(ProdutoRepository.class);
        service = new PedidoService(repository);
    }

    @Test
    void testProcessarPedidoComSucesso() {
        Produto p = new Produto(1L, "Teclado", 100.0, 10);
        Pedido pedido = new Pedido(1L, p, 2);

        when(repository.findById(1L)).thenReturn(Optional.of(p));

        double total = service.processarPedido(pedido);

        assertEquals(200.0, total);
        assertEquals(8, p.getEstoque());
        verify(repository, times(1)).save(p);
    }

    @Test
    void testQuantidadeInvalida() {
        Produto p = new Produto(1L, "Mouse", 50.0, 5);
        Pedido pedido = new Pedido(1L, p, 0);

        assertThrows(IllegalArgumentException.class, () -> service.processarPedido(pedido));
    }

    @Test
    void testEstoqueInsuficiente() {
        Produto p = new Produto(1L, "Monitor", 800.0, 1);
        Pedido pedido = new Pedido(1L, p, 5);

        when(repository.findById(1L)).thenReturn(Optional.of(p));

        assertThrows(IllegalStateException.class, () -> service.processarPedido(pedido));
    }

    @Test
    void testCadastrarProdutoValido() {
        Produto p = new Produto(2L, "Cadeira", 500.0, 3);
        when(repository.save(p)).thenReturn(p);

        Produto cadastrado = service.cadastrarProduto(p);

        assertNotNull(cadastrado);
        assertEquals("Cadeira", cadastrado.getNome());
    }
}
