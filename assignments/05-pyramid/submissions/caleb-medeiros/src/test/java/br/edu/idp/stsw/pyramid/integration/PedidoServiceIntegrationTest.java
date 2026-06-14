package br.edu.idp.stsw.pyramid.integration;

import br.edu.idp.stsw.pyramid.domain.Pedido;
import br.edu.idp.stsw.pyramid.domain.Produto;
import br.edu.idp.stsw.pyramid.repository.InMemoryProdutoRepository;
import br.edu.idp.stsw.pyramid.service.PedidoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ════════════════════════════════════════════════════════
 *  MEIO DA PIRÂMIDE — Testes de Integração
 *
 *  • Testam a interação entre o serviço e o repositório.
 *  • Usam o repositório real (InMemory) sem mocks.
 *  • Validam que os componentes funcionam juntos corretamente.
 *  • Médio custo e velocidade.
 * ════════════════════════════════════════════════════════
 */
@DisplayName("[Integração] PedidoService + InMemoryProdutoRepository")
class PedidoServiceIntegrationTest {

    private PedidoService pedidoService;
    private InMemoryProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        produtoRepository = new InMemoryProdutoRepository();
        pedidoService = new PedidoService(produtoRepository);
    }

    @Test
    @DisplayName("Deve cadastrar produto e processá-lo em um pedido com repositório real")
    void deveCadastrarEProcessarPedido() {
        // 1. Cadastra produto no repositório
        Produto mouse = new Produto(10L, "Mouse Gamer", 250.00, 5);
        pedidoService.cadastrarProduto(mouse);

        // 2. Processa pedido usando o mesmo produto salvo
        Pedido pedido = new Pedido(1L, "Kelwin", mouse, 3);
        double total = pedidoService.processarPedido(pedido);

        // 3. Verifica total calculado
        assertEquals(750.00, total, 0.01);

        // 4. Verifica estoque atualizado no repositório
        Produto atualizado = produtoRepository.findById(10L).orElseThrow();
        assertEquals(2, atualizado.getEstoque()); // 5 - 3
    }

    @Test
    @DisplayName("Deve persistir dois produtos independentes no repositório")
    void devePersistirDoisProdutos() {
        Produto p1 = new Produto(1L, "Teclado", 180.00, 20);
        Produto p2 = new Produto(2L, "Monitor", 1200.00, 8);

        pedidoService.cadastrarProduto(p1);
        pedidoService.cadastrarProduto(p2);

        assertTrue(produtoRepository.findById(1L).isPresent());
        assertTrue(produtoRepository.findById(2L).isPresent());
    }

    @Test
    @DisplayName("Deve rejeitar pedido quando estoque esgota entre duas operações")
    void deveRejeitarQuandoEstoqueEsgota() {
        Produto fone = new Produto(3L, "Fone de Ouvido", 350.00, 2);
        pedidoService.cadastrarProduto(fone);

        // Primeiro pedido consome todo o estoque
        Pedido pedido1 = new Pedido(1L, "Ana", fone, 2);
        pedidoService.processarPedido(pedido1);

        // Segundo pedido deve falhar
        Pedido pedido2 = new Pedido(2L, "Bruno", fone, 1);
        assertThrows(IllegalStateException.class,
                () -> pedidoService.processarPedido(pedido2));
    }

    @Test
    @DisplayName("Deve falhar ao processar pedido de produto não cadastrado")
    void deveFalharParaProdutoNaoCadastrado() {
        Produto fantasma = new Produto(99L, "Produto Inexistente", 100.00, 5);
        Pedido pedido = new Pedido(1L, "Carlos", fantasma, 1);

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.processarPedido(pedido));
    }
}
