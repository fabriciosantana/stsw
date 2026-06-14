package br.edu.idp.stsw.pyramid.domain;

/**
 * Entidade Pedido — agrega produtos e calcula o total.
 */
public class Pedido {

    private Long id;
    private String cliente;
    private Produto produto;
    private int quantidade;

    public Pedido() {}

    public Pedido(Long id, String cliente, Produto produto, int quantidade) {
        this.id = id;
        this.cliente = cliente;
        this.produto = produto;
        this.quantidade = quantidade;
    }

    /** Calcula o valor total do pedido. */
    public double calcularTotal() {
        if (produto == null || quantidade <= 0) return 0.0;
        return produto.getPreco() * quantidade;
    }

    public Long getId()               { return id; }
    public void setId(Long id)        { this.id = id; }

    public String getCliente()              { return cliente; }
    public void setCliente(String c)        { this.cliente = c; }

    public Produto getProduto()             { return produto; }
    public void setProduto(Produto p)       { this.produto = p; }

    public int getQuantidade()              { return quantidade; }
    public void setQuantidade(int q)        { this.quantidade = q; }
}
