package br.edu.idp.stsw.pyramid.domain;

/**
 * Entidade Produto — domínio simples para demonstrar a pirâmide de testes.
 */
public class Produto {

    private Long id;
    private String nome;
    private double preco;
    private int estoque;

    public Produto() {}

    public Produto(Long id, String nome, double preco, int estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    public Long getId()            { return id; }
    public void setId(Long id)     { this.id = id; }

    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }

    public double getPreco()             { return preco; }
    public void setPreco(double preco)   { this.preco = preco; }

    public int getEstoque()              { return estoque; }
    public void setEstoque(int estoque)  { this.estoque = estoque; }
}
