package br.edu.idp.es.stsw.pyramid.service;

import br.edu.idp.es.stsw.pyramid.domain.Pedido;
import br.edu.idp.es.stsw.pyramid.domain.Produto;
import br.edu.idp.es.stsw.pyramid.repository.ProdutoRepository;

public class PedidoService {

    private final ProdutoRepository produtoRepository;

    public PedidoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public double processarPedido(Pedido pedido) {
        if (pedido.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        Produto produto = produtoRepository
                .findById(pedido.getProduto().getId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

        if (produto.getEstoque() < pedido.getQuantidade()) {
            throw new IllegalStateException("Estoque insuficiente");
        }

        produto.setEstoque(produto.getEstoque() - pedido.getQuantidade());
        produtoRepository.save(produto);

        return pedido.calcularTotal();
    }

    public Produto cadastrarProduto(Produto produto) {
        if (produto.getPreco() < 0 || produto.getEstoque() < 0) {
            throw new IllegalArgumentException("Preço e estoque não podem ser negativos");
        }
        return produtoRepository.save(produto);
    }
}
