# I1-05 — Estudo de Caso: Pirâmide de Testes

**Aluno:** Caleb Medeiros
**Data:** 24/04/2026
**Artigo de referência:** [The Practical Test Pyramid — Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html)

---

## Visão Geral

Este projeto implementa a **pirâmide de testes** aplicada a um sistema simples de **gerenciamento de pedidos**, demonstrando os três níveis descritos por Martin Fowler:

```
        ┌─────────────────┐
        │   E2E / UI      │  ← Poucos testes — lentos, caros
        │ (manual / CLI)  │     alto valor de negócio
        └────────┬────────┘
                 │
        ┌────────┴────────┐
        │   Integração    │  ← Testes moderados — médio custo
        │  (repositório   │     valida componentes juntos
        │   em memória)   │
        └────────┬────────┘
                 │
        ┌────────┴────────┐
        │     Unidade     │  ← Muitos testes — rápidos, baratos
        │ (JUnit+Mockito) │     foco na lógica de negócio
        └─────────────────┘
```

---

## Domínio

O sistema simula um fluxo de e-commerce básico:

| Classe | Responsabilidade |
|--------|-----------------|
| `Produto` | Entidade com nome, preço e estoque |
| `Pedido`  | Agrega produto e quantidade; calcula total |
| `PedidoService` | Valida estoque, processa pedido, cadastra produto |
| `ProdutoRepository` | Interface de persistência (permite mock e implementação real) |
| `InMemoryProdutoRepository` | Repositório em memória para testes de integração |

---

## Comparação dos Níveis

| Aspecto | Unidade | Integração | E2E |
|---------|---------|------------|-----|
| **Quantidade** | Muitos (~70%) | Moderados (~20%) | Poucos (~10%) |
| **Velocidade** | < 1s | 1–5s | 10s+ |
| **Custo de manutenção** | Baixo | Médio | Alto |
| **Usa mocks?** | Sim | Não | Não |
| **Feedback** | Imediato | Rápido | Lento |
| **Framework** | JUnit 5 + Mockito | JUnit 5 + InMemory | Manual / CLI |

---

## Testes de Unidade — Base da Pirâmide

**Arquivo:** `src/test/java/.../unit/PedidoServiceTest.java`
**Total:** 8 testes
**Framework:** JUnit 5 + Mockito

Testam a lógica de negócio do `PedidoService` em **isolamento total**: o repositório é substituído por um mock via Mockito, garantindo que qualquer falha seja da lógica, nunca da infraestrutura.

**Cenários cobertos:**
- Pedido válido → total correto
- Pedido válido → estoque decrementado
- Cadastro de produto válido
- Quantidade zero ou negativa → `IllegalArgumentException`
- Produto não encontrado → `IllegalArgumentException`
- Estoque insuficiente → `IllegalStateException`
- Preço/estoque negativos ao cadastrar → `IllegalArgumentException`

```java
@Test
@DisplayName("Deve processar pedido válido e retornar total correto")
void deveProcessarPedidoValido() {
    when(produtoRepository.findById(1L)).thenReturn(Optional.of(notebook));
    when(produtoRepository.save(any(Produto.class))).thenReturn(notebook);

    double total = pedidoService.processarPedido(pedido);

    assertEquals(7000.00, total, 0.01);
    verify(produtoRepository, times(1)).save(any(Produto.class));
}
```

---

## Testes de Integração — Meio da Pirâmide

**Arquivo:** `src/test/java/.../integration/PedidoServiceIntegrationTest.java`
**Total:** 4 testes
**Framework:** JUnit 5 + `InMemoryProdutoRepository` (repositório real sem mock)

Validam que `PedidoService` e `ProdutoRepository` funcionam **corretamente juntos**, incluindo persistência de estado entre operações consecutivas.

**Cenários cobertos:**
- Cadastrar produto + processar pedido com repositório real
- Persistir dois produtos independentes
- Estoque esgota entre dois pedidos consecutivos
- Produto não cadastrado no repositório

```java
@Test
@DisplayName("Deve cadastrar produto e processá-lo em um pedido com repositório real")
void deveCadastrarEProcessarPedido() {
    Produto mouse = new Produto(10L, "Mouse Gamer", 250.00, 5);
    pedidoService.cadastrarProduto(mouse);

    Pedido pedido = new Pedido(1L, "Kelwin", mouse, 3);
    double total = pedidoService.processarPedido(pedido);

    assertEquals(750.00, total, 0.01);
    assertEquals(2, produtoRepository.findById(10L).orElseThrow().getEstoque());
}
```

---

## Topo da Pirâmide — E2E

Para este estudo de caso, o nível E2E é representado pelo teste manual via linha de comando, executando o jar gerado:

```bash
mvn package
# Observar o output dos testes de integração como proxy de E2E
```

Em um contexto de sistema completo com interface web, o E2E seria coberto com **Playwright** (utilizado no projeto Modelo SaaS — ver seminário W1-01) ou Selenium.

---

## Como executar

### Pré-requisitos

- Java 21+
- Maven 3.8+

### Executar todos os testes

```bash
cd assignments/05-pyramid/submissions/caleb-medeiros
mvn test
```

### Saída esperada

```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0 (unit)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 (integration)
[INFO] BUILD SUCCESS
```

### Executar apenas testes de unidade

```bash
mvn test -Dtest="**/unit/**"
```

### Executar apenas testes de integração

```bash
mvn test -Dtest="**/integration/**"
```

---

## Conclusão

A pirâmide de testes orienta a **distribuição ideal** de testes em um projeto: base larga de testes de unidade (baratos e rápidos), camada média de integração e poucos testes E2E (caros e lentos). Inverter essa pirâmide — ter poucos testes unitários e muitos E2E — gera suítes lentas, frágeis e difíceis de manter. A chave é encontrar o equilíbrio: testar a lógica de negócio unitariamente, validar a integração dos componentes e ter apenas os E2E mais críticos.
