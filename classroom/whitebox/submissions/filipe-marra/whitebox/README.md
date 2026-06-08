# Whitebox - Calculadora de Descontos

Este projeto implementa uma calculadora de descontos e organiza testes unitarios
por criterios de cobertura de caixa branca.

## Objetivo

Validar a regra de negocio do metodo `calculateDiscount(...)` usando suites de
teste separadas por intencao didatica:

- `StatementCoverageTest`
- `DecisionCoverageTest`
- `ConditionCoverageTest`
- `ConditionDecisionCoverageTest`
- `PathCoverageTest`

## Regra De Negocio

O desconto inicia em `0` e recebe os seguintes incrementos:

1. Compra com valor maior ou igual a `100`: adiciona `10`.
2. Cliente premium: adiciona `5`.
3. Cupom valido e compra maior ou igual a `200`: adiciona `15`.
4. Black Friday ou cliente premium com compra maior ou igual a `300`: adiciona `20`.
5. O desconto maximo permitido e `40`.

O metodo retorna o percentual final de desconto.

## Tecnologias

- Java 21
- Maven
- JUnit 5
- JaCoCo

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

## Como Executar

Rodar todos os testes:

```bash
mvn test
```

Rodar uma suite especifica:

```bash
mvn -Dtest=DecisionCoverageTest test
```

O relatorio HTML do JaCoCo e gerado em:

```text
target/site/jacoco/index.html
```
