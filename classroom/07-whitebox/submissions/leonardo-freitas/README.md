# Classroom 07 - Whitebox

Projeto Maven em Java 21 para praticar criterios de teste caixa branca usando JUnit 5 e JaCoCo.

## Regra implementada

A classe `DiscountCalculator` calcula o desconto total de uma compra:

- `10%` se o valor da compra for maior ou igual a `100`;
- `5%` se o cliente for premium;
- `15%` se o cupom for valido e o valor for maior ou igual a `200`;
- `20%` se for Black Friday ou se o cliente for premium e o valor for maior ou igual a `300`;
- teto maximo de `40%`.

## Suites de teste

- `StatementCoverageTest`: executa as principais instrucoes do metodo em um cenario que acumula todos os descontos e aplica o teto.
- `DecisionCoverageTest`: faz cada decisao assumir resultado verdadeiro e falso.
- `ConditionCoverageTest`: exercita as condicoes atomicas das decisoes compostas com valores verdadeiro e falso.
- `ConditionDecisionCoverageTest`: combina cobertura de decisoes e condicoes atomicas.
- `PathCoverageTest`: registra caminhos representativos, como compra sem desconto, valor minimo, premium, cupom, Black Friday, premium com valor alto e teto maximo.

## Como executar

```bash
mvn test
```

O relatorio HTML do JaCoCo e gerado em:

```text
target/site/jacoco/index.html
```
