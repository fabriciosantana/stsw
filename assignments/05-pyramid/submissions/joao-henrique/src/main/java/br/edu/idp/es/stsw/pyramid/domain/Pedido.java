package br.edu.idp.es.stsw.pyramid.domain;

public class Pedido {
    private Long id;
    private Produto produto;
    private int quantidade;

    public Pedido(Long id, Produto produto, int quantidade) {
        this.id = id;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public double calcularTotal() {
        return produto.getPreco() * quantidade;
    }

    public Long getId() { return id; }
    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
}
