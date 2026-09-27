# TestNG 7.12.0 - Seminário de Automação de Testes

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software - IDP  
**Atividade:** A1-01 - 1ª Avaliação Prática - Seminário: frameworks de automação de testes  
**Framework apresentado:** TestNG 7.12.0  
**Java:** 21

## 1. Introdução

O **TestNG** é um framework de testes para Java inspirado em JUnit e NUnit. Seu objetivo é oferecer uma estrutura flexível para criação, organização e execução de testes automatizados, desde testes unitários até cenários de integração e automação de interface quando combinado com outras ferramentas.

O nome TestNG vem de **Testing Next Generation**. Entre seus recursos mais importantes estão:

- anotações de teste e ciclo de vida;
- testes orientados a dados com `@DataProvider`;
- organização por grupos;
- dependências entre métodos e grupos;
- parametrização;
- execução paralela;
- configuração de suítes com `testng.xml`;
- listeners e reporters;
- integração com Maven, Gradle e ferramentas de CI/CD.

A versão utilizada neste trabalho é a **7.12.0**.

## 2. Onde o TestNG se posiciona na pirâmide de testes

O TestNG não está limitado a apenas um nível da pirâmide de testes. Ele atua como **framework/test runner**, podendo coordenar testes em diferentes níveis:

### Base - Testes de unidade

Pode testar classes e métodos Java isoladamente, de forma rápida e determinística.

Exemplo neste projeto:

- `TransferServiceTest`

### Meio - Testes de serviço/API e integração

Pode ser combinado com ferramentas como REST Assured, clientes HTTP, bancos de dados e mocks para testar integrações e serviços.

### Topo - Testes de UI/E2E

Pode ser utilizado em conjunto com Selenium ou Appium para organizar e executar testes de interface e ponta a ponta.

Assim, o TestNG **não substitui Selenium, REST Assured ou Mockito**. Ele coordena a execução dos testes que utilizam essas ferramentas.

## 3. Caixa-preta e caixa-branca

TestNG não é, por si só, uma técnica de caixa-preta ou caixa-branca. Ele pode ser usado nas duas abordagens.

### Caixa-preta

O teste observa entradas e saídas sem depender da implementação interna. Neste projeto, os casos de **Boundary Value Analysis (BVA)** e **Equivalence Class Partitioning (ECP)** podem ser planejados a partir das regras externas do serviço.

### Caixa-branca

O framework também pode executar testes planejados a partir da estrutura do código, como testes voltados a cobertura de instruções, decisões, condições e caminhos.

A técnica define **o que testar**. O TestNG oferece mecanismos para **organizar, executar e avaliar** esses testes.

## 4. Estudo de caso da demonstração

A demonstração utiliza uma classe chamada `TransferService`.

Regras de negócio:

1. o saldo não pode ser negativo;
2. o valor mínimo de transferência é **R$ 10,00**;
3. o valor máximo de transferência é **R$ 10.000,00**;
4. a transferência não pode ultrapassar o saldo disponível;
5. uma transferência válida reduz o saldo pelo valor transferido.

Essas regras foram escolhidas porque permitem demonstrar recursos do TestNG em conjunto com técnicas estudadas na disciplina.

## 5. Estrutura do projeto

```text
leonardo-freitas/
├── README.md
├── leonardo-freitas.pdf
├── pom.xml
├── testng.xml
└── src/
    ├── main/java/br/edu/idp/stsw/testngdemo/
    │   └── TransferService.java
    └── test/java/br/edu/idp/stsw/testngdemo/
        ├── TransferServiceTest.java
        ├── TransferWorkflowTest.java
        └── ParallelExecutionTest.java
```

## 6. Principais funcionalidades demonstradas

### 6.1 `@Test`

A anotação `@Test` identifica um método que deve ser executado pelo TestNG como caso de teste.

```java
@Test(groups = {"smoke", "regression"})
public void shouldAcceptAValidTransfer() {
    boolean result = service.isValid(5_000.00, 1_000.00);
    Assert.assertTrue(result);
}
```

### 6.2 Ciclo de vida - `@BeforeMethod` e `@AfterMethod`

Antes de cada teste é criada uma nova instância do serviço. Depois do teste, a referência é liberada.

```java
@BeforeMethod(alwaysRun = true)
public void setUp() {
    service = new TransferService();
}

@AfterMethod(alwaysRun = true)
public void tearDown() {
    service = null;
}
```

Isso demonstra como o TestNG controla preparação e limpeza do ambiente de testes.

### 6.3 `@DataProvider`

`@DataProvider` permite executar o mesmo método de teste várias vezes com conjuntos de dados diferentes.

Neste projeto ele é utilizado para BVA e ECP.

#### BVA

As fronteiras são R$ 10 e R$ 10.000:

| Valor | Resultado esperado |
|---:|:---|
| 9 | inválido |
| 10 | válido |
| 11 | válido |
| 9.999 | válido |
| 10.000 | válido |
| 10.001 | inválido |

```java
@DataProvider(name = "boundaryValues")
public Object[][] boundaryValues() {
    return new Object[][]{
        {9.00, false},
        {10.00, true},
        {11.00, true},
        {9_999.00, true},
        {10_000.00, true},
        {10_001.00, false}
    };
}
```

O DataProvider não escolhe os valores por nós. A técnica **BVA determina os valores** e o TestNG fornece uma forma eficiente de executá-los.

#### ECP

Também são exercitadas classes de equivalência representativas:

- valores abaixo do mínimo;
- valores válidos;
- valores acima do máximo.

### 6.4 Groups

Os casos de teste são classificados em grupos:

- `smoke`;
- `regression`;
- `bva`;
- `ecp`;
- `workflow`;
- `parallel-demo`.

Grupos permitem selecionar subconjuntos da suíte de acordo com o objetivo da execução.

Exemplo:

```java
@Test(groups = {"smoke", "regression"})
```

Em um pipeline real, uma equipe poderia executar testes `smoke` a cada commit e uma regressão mais ampla em outro momento.

### 6.5 Dependências

A classe `TransferWorkflowTest` demonstra `dependsOnMethods`.

Fluxo didático:

```text
authenticate()
      ↓
executeTransfer()
      ↓
logout()
```

Exemplo:

```java
@Test(dependsOnMethods = "authenticate")
public void executeTransfer() {
    ...
}
```

Se a dependência obrigatória falhar, o TestNG pode pular o método dependente. Dependências devem ser utilizadas com cuidado porque podem aumentar o acoplamento entre testes.

### 6.6 Execução paralela

O arquivo `testng.xml` define uma suíte que pode executar métodos em paralelo:

```xml
<suite name="A1-01 TestNG Demo"
       parallel="methods"
       thread-count="3">
```

A classe `ParallelExecutionTest` imprime a thread usada por cada cenário, tornando o paralelismo visível durante a demonstração.

## 7. `testng.xml`

O arquivo XML funciona como uma configuração de orquestração da suíte. Ele pode definir, entre outros elementos:

- suites;
- testes;
- classes;
- grupos;
- parâmetros;
- paralelismo.

Neste projeto ele reúne as três classes de teste e configura execução paralela por método com três threads.

## 8. TestNG x JUnit

JUnit e TestNG possuem hoje grande sobreposição funcional. A comparação correta não é afirmar que um framework é sempre superior ao outro.

| Recurso | TestNG | JUnit moderno |
|---|---|---|
| Testes unitários | Sim | Sim |
| Assertions | Sim | Sim |
| Testes parametrizados | `@DataProvider` | `@ParameterizedTest` |
| Agrupamento | `groups` | `@Tag` |
| Ciclo de vida | Sim | Sim |
| Dependências declarativas entre testes | Recurso nativo | Sem equivalente direto simples |
| Execução paralela | Sim | Sim |
| Configuração explícita de suíte por XML | Forte suporte com `testng.xml` | Abordagem diferente |
| Maven/Gradle | Sim | Sim |
| Selenium/API | Pode coordenar | Pode coordenar |

### Quando TestNG pode ser especialmente interessante

- suítes Java grandes;
- forte uso de testes orientados a dados;
- necessidade de grupos e seleção dinâmica de suítes;
- dependências controladas entre cenários;
- configuração explícita da execução;
- execução paralela configurável.

### Quando JUnit pode ser suficiente ou preferível

- projetos já padronizados em JUnit;
- suítes menores e simples;
- equipes que não precisam dos recursos específicos de orquestração do TestNG;
- ecossistemas em que JUnit já é o padrão consolidado.

## 9. Frameworks e ferramentas relacionadas

Ferramentas que podem ser consideradas alternativas ou complementares, dependendo da camada de teste:

- **JUnit** - principal framework de testes do ecossistema Java;
- **Spock** - framework de testes para JVM, especialmente associado a Groovy;
- **Selenium** - automação de navegadores, normalmente combinado com um test runner;
- **REST Assured** - testes de APIs HTTP em Java;
- **Mockito** - criação de mocks e doubles de teste.

## 10. Vantagens

- DataProvider simples e poderoso;
- organização por grupos;
- dependências declarativas;
- configuração flexível de suítes;
- execução paralela;
- listeners e reporters;
- integração com Maven e Gradle;
- aplicável a diferentes níveis da pirâmide quando combinado com outras ferramentas.

## 11. Desvantagens e cuidados

- possui sobreposição significativa com JUnit moderno;
- dependências mal utilizadas podem tornar a suíte frágil;
- paralelismo exige cuidado com estado compartilhado e thread safety;
- `testng.xml` e outras configurações podem ser excesso de complexidade para projetos pequenos;
- a escolha deve respeitar os padrões já adotados pela equipe e pelo projeto.

## 12. Casos de uso

TestNG é apropriado para cenários como:

- regressões automatizadas em projetos Java;
- testes data-driven;
- automação web combinada com Selenium;
- testes de API combinados com bibliotecas HTTP;
- suites organizadas em grupos `smoke`, `regression` e outros;
- pipelines CI/CD que precisam selecionar ou paralelizar subconjuntos de testes.

## 13. Validação realizada

Antes da entrega, o projeto foi validado em ambiente GitHub Actions com **Java 21**.

Foram executados com sucesso:

```bash
mvn -B test
```

E a suíte configurada pelo `testng.xml`:

```bash
mvn -B -Psuite test
```

Os dois comandos finalizaram com sucesso.

## 14. Como executar

### Pré-requisitos

- Java 21;
- Maven 3.x.

Verificar instalação:

```bash
java -version
mvn -version
```

### Executar todos os testes normalmente

Na pasta `workshops/01-test/submissions/leonardo-freitas`:

```bash
mvn test
```

### Executar a suíte `testng.xml` com paralelismo

```bash
mvn -Psuite test
```

### Executar somente o grupo smoke

```bash
mvn -Dgroups=smoke test
```

### Executar somente BVA

```bash
mvn -Dgroups=bva test
```

### Executar somente ECP

```bash
mvn -Dgroups=ecp test
```

## 15. Roteiro curto da demonstração

1. abrir `TransferService.java` e explicar as regras;
2. abrir `TransferServiceTest.java` e mostrar um `@Test` simples;
3. mostrar `@BeforeMethod` e `@AfterMethod`;
4. explicar `@DataProvider` com BVA;
5. relacionar os valores 9, 10, 11 e 9999, 10000, 10001 às fronteiras;
6. mostrar ECP;
7. mostrar os `groups`;
8. abrir `TransferWorkflowTest.java` e explicar `dependsOnMethods`;
9. abrir `testng.xml` e destacar `parallel="methods"` e `thread-count="3"`;
10. executar `mvn test`;
11. executar `mvn -Psuite test` e observar as threads no terminal.

## 16. Conclusão

TestNG é mais do que uma forma de colocar `@Test` sobre um método. Seu principal valor aparece na **organização e orquestração de suítes**, oferecendo recursos claros para testes orientados a dados, grupos, dependências e paralelismo.

Neste estudo de caso, o framework foi conectado às técnicas vistas na disciplina. **BVA e ECP determinam quais casos devem ser testados; o TestNG oferece os mecanismos para fornecer os dados, organizar a suíte, executar os casos e verificar os resultados.**

A adoção de TestNG deve ser uma decisão de engenharia: ele é especialmente útil quando seus recursos de orquestração resolvem problemas reais da suíte, mas não deve ser adotado apenas por possuir mais opções de configuração.

## Referências

- TestNG Documentation: https://testng.org/documentation.html
- TestNG - Maven: https://testng.org/maven
- TestNG releases: https://github.com/testng-team/testng/releases
- JUnit User Guide: https://docs.junit.org/
