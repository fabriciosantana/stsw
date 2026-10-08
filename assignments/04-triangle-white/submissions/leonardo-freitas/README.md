# I1-04 — Testes unitários em JUnit com técnica caixa-branca

## Objetivo

Aplicar técnicas de teste **caixa-branca** ao classificador de triângulos, analisando a estrutura interna do código e derivando testes JUnit 5 para critérios de cobertura de instruções, decisões, condições e caminhos representativos.

A implementação do triângulo mantém a mesma regra usada na atividade anterior: domínio dos lados de `1` a `200`, validação da desigualdade triangular e classificação em `Equilátero`, `Isósceles` ou `Escaleno`.

## Estrutura analisada

O método `Triangle.classify(int a, int b, int c)` possui quatro decisões principais, além da condição composta do método auxiliar de domínio.

| ID | Decisão/condição | Objetivo de cobertura |
|---|---|---|
| D1 | lado A, B ou C fora do domínio | decisão verdadeira/falsa e cada posição inválida |
| D2 | uma das três desigualdades triangulares falha | cada condição verdadeira e conjunto todo falso |
| D3 | `a == b && b == c` | decisão verdadeira/falsa e condições atômicas |
| D4 | `a == b || a == c || b == c` | cada igualdade verdadeira e todas falsas |
| D5 | `side >= 1 && side <= 200` | abaixo do mínimo, dentro do domínio e acima do máximo |

## Meta de cobertura

- **Statement coverage:** executar todas as instruções e todos os retornos do método.
- **Decision coverage:** fazer cada decisão assumir os resultados verdadeiro e falso.
- **Condition coverage:** fazer as condições atômicas relevantes assumirem verdadeiro e falso, respeitando o curto-circuito de `&&` e `||`.
- **Condition/decision coverage:** combinar os dois critérios anteriores em uma suíte parametrizada.
- **Path coverage:** exercitar caminhos representativos até cada uma das cinco saídas possíveis. Não se pretende enumerar todos os caminhos teóricos, pois combinações com curto-circuito crescem rapidamente.

## Suítes de teste

```text
src/test/java/br/edu/idp/es/stsw/trianglewhite/
├── StatementCoverageTest.java
├── DecisionCoverageTest.java
├── ConditionCoverageTest.java
├── ConditionDecisionCoverageTest.java
└── PathCoverageTest.java
```

### Caminhos representativos

| Caminho | Entrada | Saída esperada |
|---|---|---|
| P1 | `(0, 100, 100)` | `Lados inválidos` |
| P2 | `(50, 50, 100)` | `Não é um triângulo` |
| P3 | `(100, 100, 100)` | `Equilátero` |
| P4 | `(100, 100, 120)` | `Isósceles` |
| P5 | `(3, 4, 5)` | `Escaleno` |

Para demonstrar as condições compostas também são usados casos em que cada lado é inválido separadamente, cada desigualdade triangular é a responsável pela rejeição e cada igualdade possível produz um triângulo isósceles.

## Execução

Requisitos:

- Java 21
- Maven

Rodar os testes:

```bash
mvn test
```

Gerar o relatório JaCoCo:

```bash
mvn verify
```

O relatório HTML será gerado em:

```text
target/site/jacoco/index.html
```

## Observação sobre cobertura

O JaCoCo mede diretamente cobertura de instruções e ramos. A cobertura das condições atômicas é demonstrada pelo desenho dos casos de teste e documentada neste README; ela não deve ser confundida automaticamente com MC/DC.
