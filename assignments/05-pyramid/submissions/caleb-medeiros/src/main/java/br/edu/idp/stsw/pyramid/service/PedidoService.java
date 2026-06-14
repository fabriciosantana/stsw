package br.edu.idp.stsw.pyramid.service;

import br.edu.idp.stsw.pyramid.domain.Pedido;
import br.edu.idp.stsw.pyramid.domain.Produto;
import br.edu.idp.stsw.pyramid.repository.ProdutoRepository;

/**
 * Serviço de pedidos — contém as regras de negócio.
 * Esta é a camada alvo dos testes de unidade.
 */
public class PedidoService {

    private final ProdutoRepository produtoRepository;

    public PedidoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Processa um pedido: valida estoque e calcula total.
     *
     * @throws IllegalArgumentException se quantidade <= 0
     * @throws IllegalStateException    se estoque insuficiente
     */
    public double processarPedido(Pedido pedido) {
        if (pedido.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository
                .findById(pedido.getProduto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + pedido.getProduto().getId()));

        if (produto.getEstoque() < pedido.getQuantidade()) {
            throw new IllegalStateException(
                    "Estoque insuficiente. Disponível: " + produto.getEstoque()
                    + ", solicitado: " + pedido.getQuantidade());
        }

        // Debita estoque
        produto.setEstoque(produto.getEstoque() - pedido.getQuantidade());
        produtoRepository.save(produto);

        return pedido.calcularTotal();
    }

    /**
     * Cadastra um novo produto no repositório.
     */
    public Produto cadastrarProduto(Produto produto) {
        if (produto.getPreco() < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        if (produto.getEstoque() < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
        return produtoRepository.save(produto);
    }
}
