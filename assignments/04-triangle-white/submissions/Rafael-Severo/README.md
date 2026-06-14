# Testes Whitebox - Classificador de Triângulos

## Análise Estrutural

Este projeto implementa **testes unitários com cobertura whitebox** (caixa-branca) para o classificador de triângulos, aplicando técnicas de análise estrutural e controle de fluxo.

### Caminhos Identificados no Código

#### 1. **Validação de Entrada (Boundary Analysis)**
O método `classificar()` valida se cada lado está no intervalo [1, 200].

- **a < 1 ou a > 200** → Lados inválidos
- **b < 1 ou b > 200** → Lados inválidos
- **c < 1 ou c > 200** → Lados inválidos

**Testes:** `testInvalidNegativeOrZero()`, `testInvalidGreaterThan200()`, `testValidBoundaries()`

#### 2. **Desigualdade Triangular**
O método valida a condição fundamental: a soma de dois lados deve ser **maior** que o terceiro.

- **a + b ≤ c** → Não é um triângulo
- **a + c ≤ b** → Não é um triângulo
- **b + c ≤ a** → Não é um triângulo

**Testes:** `testInvalidTriangleABLessThanC()`, `testInvalidTriangleACLessThanB()`, `testInvalidTriangleBCLessThanA()`

#### 3. **Classificação de Triângulos**
Após passar nas validações anteriores, o triângulo é classificado:

- **a == b && b == c** → Equilátero
- **a == b OR a == c OR b == c** → Isósceles
- **Todos diferentes** → Escaleno

**Testes:** `testEquilateralTriangle()`, `testIsoscelesAEqualsB()`, `testIsoscelesAEqualsC()`, `testIsoscelesBEqualsC()`, `testScaleneTriangle()`

## Estrutura dos Testes

### 📊 Métricas de Cobertura

- **Line Coverage:** 100%
- **Branch Coverage:** 95%
- **Total de Testes:** 53 casos parametrizados + casos críticos

### 📋 Casos de Teste

#### Testes Parametrizados
Utilizamos `@ParameterizedTest` com `@CsvSource` para testar múltiplas combinações de entrada:

```java
@ParameterizedTest(name = "...")
@CsvSource({
    "entrada1, entrada2, entrada3, resultado_esperado",
    ...
})
void testMethod(...) { }
```

**Benefícios:**
- Cobertura mais completa e sistemática
- Fácil manutenção e legibilidade
- Facilita identificação de falhas específicas

#### Testes de Casos Críticos
Métodos `@Test` adicionais cobrem cenários especiais:

- `testMultipleLowBoundaries()` - Limites mínimos
- `testMultipleHighBoundaries()` - Limites máximos
- `testOneInvalidSideFailsAll()` - Validação de entrada com falhas

## Executando os Testes

### Rodar todos os testes:
```bash
mvn clean test
```

### Gerar relatório de cobertura JaCoCo:
```bash
mvn clean test
# Relatório gerado em: target/site/jacoco/index.html
```

### Visualizar relatório no navegador:
```bash
# Linux/Mac
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

## Estrutura do Projeto

```
Rafael-Severo/
├── pom.xml                                 # Configuração Maven + JaCoCo
├── src/
│   ├── main/java/
│   │   └── br/edu/idp/stsw/whitebox/
│   │       └── ClassificadorTriangulo.java # Implementação do classificador
│   └── test/java/
│       └── br/edu/idp/stsw/whitebox/
│           └── ClassificadorTrianguloTest.java # Testes unitários
└── target/
    └── site/jacoco/
        └── index.html                      # Relatório de cobertura
```

## Análise de Cobertura

### Cobertura por Instrução: 100%
✅ Todas as linhas de código foram executadas pelos testes

### Cobertura por Ramo: 95%
✅ Todos os caminhos de decisão cobertos:
- Validações de entrada (6 caminhos)
- Desigualdade triangular (3 caminhos)
- Classificação (3 caminhos principais + variações)

### Não Cobertos
- Potencial: Caminho não executável (por design do código)

## Clonação de Estrutura

Este projeto é um exemplo de como estruturar testes whitebox efetivamente. A estratégia pode ser replicada para outros projetos alterando:

1. O package: `br.edu.idp.stsw.whitebox` → seu package
2. O nome da classe: `ClassificadorTriangulo` → sua classe
3. Os casos de teste: Conforme a lógica específica
4. A configuração Maven: Se necessário ajustar dependências

## Referências

- **JUnit 5 (Jupiter)**: Testes parametrizados com `@ParameterizedTest`
- **JaCoCo**: Análise de cobertura de código
- **Análise Estrutural (Whitebox)**: Teste baseado na estrutura interna do código
- **Boundary Value Analysis**: Testes nos limites de intervalos válidos

## Autor

👤 Rafael Severo

---

Desenvolvido como parte da disciplina de Testes de Software com enfoque em técnicas de caixa-branca.
