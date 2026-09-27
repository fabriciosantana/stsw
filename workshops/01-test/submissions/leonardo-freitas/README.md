# TestNG 7.12.0 - Seminário de Automação de Testes

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software - IDP  
**Atividade:** A1-01 - 1ª Avaliação Prática - Seminário: frameworks de automação de testes  
**Framework:** TestNG 7.12.0  
**Ambiente:** Java 21 + Maven

## 1. Introdução

O **TestNG (Testing Next Generation)** é um framework/test runner para Java inspirado em JUnit e NUnit. Ele oferece mecanismos para criar, organizar e executar testes automatizados, incluindo lifecycle, testes orientados a dados, grupos, dependências, parametrização, listeners, reporters e execução paralela.

A ideia central deste trabalho é mostrar que o valor do TestNG não está apenas na anotação `@Test`, mas principalmente na **orquestração de uma suíte de testes que cresce**.

## 2. Posição na pirâmide de testes

O TestNG pode coordenar testes em diferentes níveis:

- **Unidade:** Java + TestNG;
- **Serviço/API/integração:** TestNG combinado com clientes HTTP, REST Assured, banco de dados ou mocks;
- **UI/E2E:** TestNG combinado com Selenium ou Appium.

TestNG **não substitui** Selenium, REST Assured ou Mockito. Ele atua como framework/test runner que organiza a execução.

## 3. Caixa-preta e caixa-branca

TestNG não é uma técnica de caixa-preta nem de caixa-branca. Ele pode executar testes desenhados pelas duas abordagens.

Neste projeto, **Boundary Value Analysis (BVA)** e **Equivalence Class Partitioning (ECP)** são usadas como técnicas de caixa-preta. O TestNG fornece os mecanismos para executar os casos produzidos por essas técnicas.

> **BVA e ECP dizem o que testar. TestNG organiza como os dados serão fornecidos, executados e avaliados.**

## 4. Estudo de caso

A demonstração utiliza `TransferService`.

### Regras

1. saldo negativo é inválido;
2. transferência mínima: **R$ 10,00**;
3. transferência máxima: **R$ 10.000,00**;
4. o valor não pode ultrapassar o saldo;
5. uma transferência válida reduz o saldo disponível.

## 5. Estrutura

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

## 6. Funcionalidades demonstradas

### 6.1 `@Test`

```java
@Test(groups = {"smoke", "regression"})
public void shouldAcceptAValidTransfer() {
    boolean result = service.isValid(5_000.00, 1_000.00);
    Assert.assertTrue(result);
}
```

`@Test` identifica um método como caso de teste executável pelo TestNG.

### 6.2 Lifecycle

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

`@BeforeMethod` e `@AfterMethod` executam preparação e limpeza ao redor de cada método de teste.

### 6.3 `@DataProvider` + BVA

Fronteiras do domínio: **10** e **10.000**.

| Valor | Esperado |
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

O mesmo método de teste é reutilizado para cada linha do DataProvider.

### 6.4 ECP

O projeto também usa representantes de classes de equivalência:

- abaixo do mínimo: `-50`, `5`;
- classe válida: `500`, `5000`;
- acima do máximo: `15000`.

### 6.5 Groups

Grupos presentes no exemplo:

- `smoke`;
- `regression`;
- `bva`;
- `ecp`;
- `workflow`;
- `parallel-demo`.

Exemplo:

```java
@Test(groups = {"smoke", "regression"})
```

Isso permite selecionar partes da suíte sem duplicar os testes.

### 6.6 Dependências

`TransferWorkflowTest` demonstra `dependsOnMethods`:

```text
authenticate()
      ↓
executeTransfer()
      ↓
logout()
```

```java
@Test(dependsOnMethods = "authenticate")
public void executeTransfer() {
    ...
}
```

Dependências devem ser utilizadas com cuidado, porque testes excessivamente dependentes podem aumentar o acoplamento da suíte.

### 6.7 Paralelismo

`testng.xml` configura paralelismo por método:

```xml
<suite name="A1-01 TestNG Demo"
       parallel="methods"
       thread-count="3">
```

`ParallelExecutionTest` imprime a thread usada por cada cenário para tornar a execução concorrente visível.

## 7. `testng.xml`

O XML é usado para orquestrar a suíte e pode definir classes, grupos, parâmetros e paralelismo.

Neste projeto ele reúne as classes de demonstração e ativa três threads para execução paralela por método.

## 8. TestNG x JUnit moderno

JUnit e TestNG possuem hoje bastante sobreposição.

| Recurso | TestNG | JUnit moderno |
|---|---|---|
| Testes unitários | Sim | Sim |
| Assertions | Sim | Sim |
| Parametrização | `@DataProvider` | `@ParameterizedTest` |
| Agrupamento | `groups` | `@Tag` |
| Lifecycle | Sim | Sim |
| Dependências declarativas | Nativo | Sem equivalente direto simples |
| Paralelismo | Sim | Sim |
| Configuração explícita de suíte | `testng.xml` | abordagem diferente |

A escolha correta depende do contexto. TestNG é especialmente interessante quando a suíte se beneficia de **DataProvider, groups, dependencies, configuração explícita e paralelismo**. Projetos já padronizados em JUnit podem não ganhar nada ao trocar apenas por trocar.

## 9. Frameworks e ferramentas similares ou complementares

- **JUnit** - framework de testes Java;
- **Spock** - testes na JVM com forte associação ao Groovy;
- **Selenium** - automação de navegadores;
- **REST Assured** - testes de APIs HTTP em Java;
- **Mockito** - mocks e test doubles.

## 10. Vantagens

- DataProvider para testes data-driven;
- organização por grupos;
- dependências declarativas;
- configuração flexível de suítes;
- execução paralela;
- listeners e reporters;
- integração com Maven/Gradle e CI/CD;
- aplicável em diferentes camadas quando combinado com outras ferramentas.

## 11. Desvantagens e cuidados

- sobreposição significativa com JUnit moderno;
- dependências podem acoplar testes;
- paralelismo exige isolamento e thread safety;
- configuração pode ser excessiva para projetos pequenos;
- a adoção deve respeitar padrões e necessidades da equipe.

## 12. Casos reais

Em vez de citar empresas sem evidência pública, foram verificados projetos open source reais:

### Apache Atlas

O repositório do **Apache Atlas** possui diversos módulos que declaram `org.testng:testng` em seus arquivos Maven, incluindo módulos com escopo de teste.

Fonte: https://github.com/apache/atlas

### Apache BifroMQ

A suíte de testes do **Apache BifroMQ** também inclui `org.testng:testng` entre suas dependências.

Fonte: https://github.com/apache/bifromq

Esses exemplos demonstram uso verificável do TestNG em projetos Java reais de código aberto.

## 13. Validação do projeto

O projeto foi executado em **GitHub Actions com Java 21**.

Executados com sucesso:

```bash
mvn -B test
```

```bash
mvn -B -Psuite test
```

A validação incluiu a execução padrão e a suíte definida por `testng.xml` com paralelismo.

## 14. Como executar

### Pré-requisitos

- Java 21;
- Maven 3.x.

```bash
java -version
mvn -version
```

Entre no diretório da submissão:

```bash
cd workshops/01-test/submissions/leonardo-freitas
```

### Todos os testes

```bash
mvn test
```

### Grupo smoke

```bash
mvn -Dgroups=smoke test
```

### BVA

```bash
mvn -Dgroups=bva test
```

### ECP

```bash
mvn -Dgroups=ecp test
```

### Suíte XML com paralelismo

```bash
mvn -Psuite test
```

## 15. Roteiro da demonstração

1. abrir `TransferService.java` e explicar as regras;
2. mostrar um `@Test` simples;
3. mostrar `@BeforeMethod` e `@AfterMethod`;
4. explicar `@DataProvider` com BVA;
5. mostrar ECP;
6. mostrar os `groups`;
7. abrir `TransferWorkflowTest.java` e explicar `dependsOnMethods`;
8. abrir `testng.xml` e destacar `parallel="methods"` e `thread-count="3"`;
9. executar `mvn test`;
10. executar `mvn -Psuite test` e observar as threads no terminal.

## 16. Conclusão

TestNG é mais do que uma anotação para executar testes. Seu principal diferencial aparece na **organização e orquestração da suíte**.

Neste estudo de caso, técnicas de projeto de testes foram conectadas ao framework: **BVA e ECP definem quais casos são importantes; TestNG oferece mecanismos para fornecer dados, agrupar, ordenar e executar esses casos, inclusive em paralelo**.

A adoção deve ser uma decisão de engenharia. O framework é especialmente útil quando seus recursos resolvem problemas reais de organização e escala, e não apenas por oferecer mais opções de configuração.

## Referências

- TestNG Documentation: https://testng.org/documentation.html
- TestNG + Maven: https://testng.org/maven
- TestNG Releases: https://github.com/testng-team/testng/releases
- JUnit User Guide: https://docs.junit.org/
- Apache Atlas: https://github.com/apache/atlas
- Apache BifroMQ: https://github.com/apache/bifromq
