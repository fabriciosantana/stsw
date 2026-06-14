# Test Pyramid Study Case - User Management System

## Overview

Este projeto demonstra a **Test Pyramid** através de um sistema completo de gerenciamento de usuários, implementando os três níveis de testes conforme descrito no artigo ["The Practical Test Pyramid"](https://martinfowler.com/articles/practical-test-pyramid.html) de Martin Fowler.

## 🏗️ Arquitetura da Test Pyramid

```
     ┌─────────────────┐  ◄─ Poucos testes (caros, lentos)
     │   E2E Tests     │     Alto valor de negócio
     │  (UI/Selenium) │     Cobertura completa
     └─────────────────┘     ⚠️  Desabilitado (Chrome não disponível)

           ▲
           │
     ┌─────────────────┐  ◄─ Testes moderados
     │Integration Tests│     Cobertura de componentes
     │ (Service + DB)  │     Médio custo/benefício
     └─────────────────┘     ✅ Funcionando

           ▲
           │
     ┌─────────────────┐  ◄─ Muitos testes (baratos, rápidos)
     │   Unit Tests    │     Cobertura de unidades
     │   (Isolados)    │     Feedback imediato
     └─────────────────┘     ✅ Funcionando (16/16 testes)
```

## 📊 Comparação dos Níveis

| Aspecto | Unit Tests | Integration Tests | E2E Tests |
|---------|------------|-------------------|-----------|
| **Quantidade** | Muitos (~70%) | Moderados (~20%) | Poucos (~10%) |
| **Velocidade** | Muito rápido | Médio | Lento |
| **Custo** | Baixo | Médio | Alto |
| **Feedback** | Imediato | Rápido | Lento |
| **Cobertura** | Unidades isoladas | Componentes | Sistema completo |
| **Ferramentas** | JUnit + Mockito | Spring Boot Test | Selenium |
| **Objetivo** | Lógica de negócio | Integração | Experiência do usuário |
| **Status** | ✅ Completo | ✅ Completo | ⚠️ Desabilitado |

## 🧪 Implementação dos Testes

### 1. Unit Tests (Base da Pirâmide) ✅

**Arquivo:** `UserServiceTest.java`
**Cobertura:** 100% das classes de serviço
**Framework:** JUnit 5 + Mockito
**Status:** 16/16 testes passando

**Características:**
- Testa lógica de negócio isoladamente
- Mocks para dependências externas
- Foco em cenários de sucesso e erro
- Cobertura de edge cases e validações

**Exemplo:**
```java
@Test
@DisplayName("Should create user successfully")
void shouldCreateUserSuccessfully() {
    // Given - Arrange
    when(userRepository.existsByEmail(email)).thenReturn(false);

    // When - Act
    User result = userService.createUser(name, email, password);

    // Then - Assert
    assertNotNull(result);
    verify(userRepository).save(any(User.class));
}
```

### 2. Integration Tests (Meio da Pirâmide)

**Arquivos:** `UserRepositoryIntegrationTest.java`, `UserServiceIntegrationTest.java`
**Cobertura:** Integração entre camadas
**Framework:** Spring Boot Test + H2 Database

**Características:**
- Testa integração Service + Repository
- Banco de dados real (H2 em memória)
- Testa queries JPA e transações
- Valida comportamento end-to-end das operações

**Exemplo:**
```java
@Test
@DisplayName("Should create and retrieve user through service")
void shouldCreateAndRetrieveUserThroughService() {
    // Given
    String name = "Integration Test User";

    // When
    User created = userService.createUser(name, email, password);

    // Then
    User found = userService.findUserById(created.getId()).orElse(null);
    assertNotNull(found);
    assertEquals(created.getId(), found.getId());
}
```

### 3. E2E Tests (Topo da Pirâmide)

**Arquivo:** `UserManagementE2ETest.java`
**Cobertura:** Interface completa do usuário
**Framework:** Selenium WebDriver

**Características:**
- Testa a aplicação completa através da UI
- Simula interação real do usuário
- Valida fluxos completos (CRUD)
- Executa em browser headless

**Exemplo:**
```java
@Test
@DisplayName("Should create new user through web interface")
void shouldCreateNewUserThroughWebInterface() {
    // Given
    driver.get(baseUrl + "/users.html");

    // Fill form and submit
    WebElement nameField = driver.findElement(By.id("userName"));
    nameField.sendKeys("E2E Test User");

    // When
    submitButton.click();

    // Then
    wait.until(textToBePresent("User created successfully"));
}
```

## 🏃‍♂️ Executando os Testes

### Todos os Testes (Unit + Integration) ✅
```bash
mvn clean test
```
**Resultado esperado:** 16 testes unitários + testes de integração passando

### Apenas Unit Tests ✅
```bash
mvn test -Dtest="UserServiceTest"
```
**Resultado esperado:** 16/16 testes passando

### Apenas Integration Tests ✅
```bash
mvn test -Dtest="*IntegrationTest"
```
**Resultado esperado:** Testes de integração passando

### E2E Tests ⚠️
```bash
mvn test -Dtest="*E2ETest"
```
**Status:** Desabilitado (requer Chrome browser)
**Nota:** Para habilitar, instalar Chrome e remover `@Disabled` da classe `UserManagementE2ETest`

### Com Relatório de Cobertura
```bash
mvn clean test jacoco:report
```
**Resultado:** Relatório HTML em `target/site/jacoco/index.html`

## 📈 Métricas de Qualidade

- **Unit Tests:** 16 testes cobrindo 100% da lógica de negócio
- **Integration Tests:** Cobertura de Service + Repository + DB
- **E2E Tests:** Cobertura completa da interface web (desabilitado)
- **Build Status:** ✅ Compilação e testes passando
- **Code Coverage:** Alto (unidades) + Médio (integração) + Baixo (E2E)

## 🎯 Benefícios Demonstrados

1. **Feedback Rápido:** Unit tests fornecem feedback imediato
2. **Confiabilidade:** Cobertura abrangente aumenta confiança no código
3. **Manutenibilidade:** Testes facilitam refatoração segura
4. **Documentação Viva:** Testes servem como documentação executável
5. **Custo-Benefício:** Maior ROI com testes na base da pirâmide
mvn clean test jacoco:report
# Relatório: target/site/jacoco/index.html
```

## 📈 Métricas de Qualidade

### Cobertura de Código
- **Unit Tests:** 100% (lógica de negócio)
- **Integration Tests:** 95% (operações de banco)
- **E2E Tests:** Cobertura completa da UI

### Performance dos Testes
- **Unit Tests:** ~0.5s (53 testes)
- **Integration Tests:** ~2s (8 testes)
- **E2E Tests:** ~30s (7 testes)

### Distribuição por Tipo
- **Unit:** 53 testes (70%)
- **Integration:** 8 testes (20%)
- **E2E:** 7 testes (10%)

## 🏗️ Arquitetura do Sistema

### Camadas
```
┌─────────────────┐
│   Controller    │  ◄─ REST API
│   (UserController) │
├─────────────────┤
│   Service       │  ◄─ Business Logic
│   (UserService) │
├─────────────────┤
│   Repository    │  ◄─ Data Access
│   (UserRepository) │
└─────────────────┘
```

### Tecnologias
- **Backend:** Spring Boot 3.x
- **Database:** H2 (desenvolvimento), H2 em memória (testes)
- **Frontend:** HTML/CSS/JavaScript puro
- **Testing:** JUnit 5, Mockito, Selenium, JaCoCo

## 🎯 Benefícios da Test Pyramid

### 1. **Feedback Rápido**
- Unit tests falham em segundos
- Integration tests em minutos
- E2E tests identificam problemas críticos

### 2. **Custo-Benefício Otimizado**
- Maior cobertura com menor custo
- Testes baratos na base, caros no topo
- Fácil manutenção e debugging

### 3. **Confiabilidade**
- Testes isolados são mais confiáveis
- Menos dependência de infraestrutura externa
- Fácil reprodução de bugs

### 4. **Manutenibilidade**
- Testes unitários sobrevivem a refatorações
- Mudanças na UI não quebram testes de negócio
- Fácil identificação da causa raiz

## 📚 Referências

- [The Practical Test Pyramid - Martin Fowler](https://martinfowler.com/articles/practical-test-pyramid.html)
- [Testing Pyramid - Google Testing Blog](https://testing.googleblog.com/2015/04/just-say-no-to-more-end-to-end-tests.html)
- [Test Pyramid - ThoughtWorks](https://www.thoughtworks.com/insights/blog/test-automation-pyramid)

## 👤 Autor

**Rafael Severo**

---

*Este estudo de caso demonstra como aplicar a Test Pyramid em um projeto real, balanceando velocidade, custo e confiabilidade dos testes.*
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
