# I1-03 — Testes em Cucumber e Gherkin com técnica blackbox

## Objetivo

Implementar testes de caixa-preta para o problema do triângulo usando Cucumber, Gherkin e Boundary Value Analysis (BVA), considerando o domínio válido de cada lado entre 1 e 200.

## Estratégia de teste

Os testes usam somente a API pública `Triangle.classify(int a, int b, int c)`. Nenhum detalhe interno da implementação é acessado pelos steps.

Foram consideradas as seguintes classes de equivalência:

- triângulo equilátero;
- triângulo isósceles;
- triângulo escaleno;
- valores no domínio que não satisfazem a desigualdade triangular;
- valores abaixo do limite mínimo;
- valores acima do limite máximo.

Para BVA robusto, cada um dos três lados é testado individualmente com:

- `min - 1 = 0`;
- `min = 1`;
- `min + 1 = 2`;
- `max - 1 = 199`;
- `max = 200`;
- `max + 1 = 201`.

Nos cenários de fronteira, os outros dois lados usam o valor 150 para manter um triângulo válido quando o valor analisado pertence ao domínio. Isso isola a fronteira da variável sob teste.

## Estrutura

```text
src/main/java/br/edu/idp/es/stsw/triangleblack/Triangle.java
src/test/java/br/edu/idp/es/stsw/triangleblack/TriangleSteps.java
src/test/java/br/edu/idp/es/stsw/triangleblack/RunCucumberTest.java
src/test/resources/features/triangle.feature
```

## Execução

Requisitos:

- Java 21;
- Maven.

Execute:

```bash
mvn test
```

O Maven executará o runner Cucumber por meio do JUnit Platform e validará todos os cenários definidos no arquivo `triangle.feature`.
