# Whitebox - Calculadora de Descontos

Este projeto foi desenvolvido para praticar **teste caixa branca** com Java.
A ideia foi implementar uma regra de negócio simples e criar testes pensando
nos critérios de cobertura vistos em aula.

## Objetivo do projeto

Implementar o método `calculateDiscount(...)` na classe `DiscountCalculator`
e validar seu comportamento com suítes de testes organizadas por critério.

## Regras implementadas

O desconto começa em `0` e recebe incrementos por regra:

1. Compra com valor **>= 100**: +10
2. Cliente premium: +5
3. Cupom válido **e** compra **>= 200**: +15
4. Black Friday **ou** (premium **e** compra **>= 300**): +20
5. Teto máximo de desconto: **40**

O método retorna o **percentual total de desconto**.

## Estrutura

```text
whitebox
├── pom.xml
├── README.md
└── src
    ├── main/java/com/example/whitebox/DiscountCalculator.java
    └── test/java/com/example/whitebox/
        ├── StatementCoverageTest.java
        ├── DecisionCoverageTest.java
        ├── ConditionCoverageTest.java
        ├── ConditionDecisionCoverageTest.java
        └── PathCoverageTest.java
```

## Tecnologias

- Java 21
- Maven
- JUnit 5
- JaCoCo

## Como executar

```bash
mvn test
```

Para rodar uma suíte específica:

```bash
mvn -Dtest=DecisionCoverageTest test
```

## O que cada suíte demonstra

- `StatementCoverageTest`: percorre instruções principais do método.
- `DecisionCoverageTest`: força decisões para `true` e `false`.
- `ConditionCoverageTest`: exercita condições atômicas nas decisões compostas.
- `ConditionDecisionCoverageTest`: combina decisão + condição no mesmo conjunto.
- `PathCoverageTest`: percorre caminhos representativos da lógica.

## Resultado atual

Todos os testes passam e o projeto gera relatório do JaCoCo em:

`target/site/jacoco/index.html`
