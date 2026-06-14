package br.edu.idp.stsw.pyramid.unit;

import br.edu.idp.stsw.pyramid.domain.Pedido;
import br.edu.idp.stsw.pyramid.domain.Produto;
import br.edu.idp.stsw.pyramid.repository.ProdutoRepository;
import br.edu.idp.stsw.pyramid.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ════════════════════════════════════════════════════════
 *  BASE DA PIRÂMIDE — Testes de Unidade
 *
 *  • Testam a lógica de negócio isoladamente.
 *  • Usam Mockito para substituir dependências externas.
 *  • Rápidos, baratos e numerosos.
 * ════════════════════════════════════════════════════════
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[Unidade] PedidoService")
class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Produto notebook;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        notebook = new Produto(1L, "Notebook", 3500.00, 10);
        pedido   = new Pedido(1L, "Caleb", notebook, 2);
    }

    // ── Cenários de sucesso ───────────────────────────────

    @Test
    @DisplayName("Deve processar pedido válido e retornar total correto")
    void deveProcessarPedidoValido() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(notebook));
        when(produtoRepository.save(any(Produto.class))).thenReturn(notebook);

        double total = pedidoService.processarPedido(pedido);

        assertEquals(7000.00, total, 0.01);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve decrementar estoque ao processar pedido")
    void deveDecrementarEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(notebook));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.processarPedido(pedido);

        assertEquals(8, notebook.getEstoque()); // 10 - 2
    }

    @Test
    @DisplayName("Deve cadastrar produto com preço e estoque válidos")
    void deveCadastrarProdutoValido() {
        when(produtoRepository.save(notebook)).thenReturn(notebook);

        Produto salvo = pedidoService.cadastrarProduto(notebook);

        assertNotNull(salvo);
        assertEquals("Notebook", salvo.getNome());
    }

    // ── Cenários de erro ─────────────────────────────────

    @Test
    @DisplayName("Deve lançar exceção quando quantidade for zero")
    void deveLancarExcecaoParaQuantidadeZero() {
        pedido.setQuantidade(0);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.processarPedido(pedido));

        verify(produtoRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção quando quantidade for negativa")
    void deveLancarExcecaoParaQuantidadeNegativa() {
        pedido.setQuantidade(-1);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.processarPedido(pedido));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não for encontrado")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.processarPedido(pedido));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque for insuficiente")
    void deveLancarExcecaoParaEstoqueInsuficiente() {
        notebook.setEstoque(1); // menos que os 2 solicitados
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(notebook));

        assertThrows(IllegalStateException.class,
                () -> pedidoService.processarPedido(pedido));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar produto com preço negativo")
    void deveLancarExcecaoPrecoNegativo() {
        notebook.setPreco(-1.0);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.cadastrarProduto(notebook));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar produto com estoque negativo")
    void deveLancarExcecaoEstoqueNegativo() {
        notebook.setEstoque(-5);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.cadastrarProduto(notebook));
    }
}
