# I1-05 — Estudo de Caso: The Practical Test Pyramid

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software  
**Atividade:** I1-05 — Implementar estudo de caso do Test Pyramid

## Objetivo

Este projeto demonstra a aplicação prática da **Pirâmide de Testes** em um pequeno domínio de pedidos de uma loja.

A estratégia segue a ideia central de manter:

- muitos testes rápidos e isolados na base;
- uma quantidade menor de testes de integração;
- poucos testes end-to-end cobrindo os fluxos mais importantes.

## Cenário

O sistema permite:

1. cadastrar produtos;
2. consultar produtos em um repositório;
3. realizar pedidos;
4. validar quantidade e estoque;
5. calcular o total da compra;
6. atualizar o estoque após a venda.

## Estrutura da pirâmide

```text
              /\
             /  \
            / E2E\
           /------\
          /Integração\
         /------------\
        /   Unidade    \
       /________________\
```

### Base — testes unitários

Arquivo:

```text
src/test/java/br/edu/idp/es/stsw/pyramid/unit/OrderServiceUnitTest.java
```

Os testes unitários exercitam o `OrderService` isoladamente. O `ProductRepository` é substituído por mocks com Mockito.

São cobertos cenários como:

- pedido válido;
- quantidade zero ou negativa;
- produto inexistente;
- estoque insuficiente;
- cadastro de produto inválido.

Esses testes são rápidos, independentes e representam a maior quantidade de verificações do projeto.

### Meio — testes de integração

Arquivo:

```text
src/test/java/br/edu/idp/es/stsw/pyramid/integration/OrderServiceIntegrationTest.java
```

Os testes de integração utilizam a implementação real `InMemoryProductRepository` e verificam a interação entre serviço e persistência em memória.

São validados:

- cadastro e recuperação de produto;
- processamento de pedido;
- atualização real do estoque;
- comportamento após pedidos sucessivos.

### Topo — teste end-to-end

Arquivo:

```text
src/test/java/br/edu/idp/es/stsw/pyramid/e2e/OrderFlowE2ETest.java
```

O teste end-to-end utiliza a fachada `StoreApplication` e percorre o fluxo completo da aplicação:

1. cadastrar um produto;
2. efetuar uma compra;
3. validar o valor total;
4. consultar o estoque resultante.

O objetivo é validar o comportamento integrado do sistema com o mínimo possível de testes de alto nível.

## Por que usar a pirâmide?

Testes de unidade são mais rápidos, baratos e fáceis de diagnosticar. Testes de integração aumentam a confiança nas interações entre componentes, mas possuem maior custo. Testes end-to-end verificam fluxos completos, porém são mais lentos e mais sensíveis a mudanças.

Por isso, a estratégia utilizada neste projeto concentra a maior cobertura na base e reduz progressivamente a quantidade de testes nos níveis superiores.

## Tecnologias

- Java 21
- Maven
- JUnit 5
- Mockito
- JaCoCo

## Executar

A partir deste diretório:

```bash
mvn test
```

Para executar todo o ciclo e gerar o relatório JaCoCo:

```bash
mvn verify
```

O relatório de cobertura é gerado em:

```text
target/site/jacoco/index.html
```

### Executar somente testes unitários

```bash
mvn -Dtest=OrderServiceUnitTest test
```

### Executar somente integração

```bash
mvn -Dtest=OrderServiceIntegrationTest test
```

### Executar somente E2E

```bash
mvn -Dtest=OrderFlowE2ETest test
```

## Conclusão

A solução mostra que a Pirâmide de Testes não significa simplesmente criar três tipos de testes, mas distribuir a responsabilidade de validação de forma eficiente. Regras de negócio são verificadas principalmente por testes unitários, interações reais são verificadas por testes de integração e somente os fluxos mais importantes chegam aos testes end-to-end.
