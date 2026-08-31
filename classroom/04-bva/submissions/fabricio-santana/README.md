# Implementação: Boundary Value Analysis em Missões de Drones de Resgate

Esta submissão implementa o estudo de caso proposto em `classroom/04-bva`.

## Regra implementada

A classe `DroneMissionPolicy` autoriza uma missão somente se:

- `30 <= bateria <= 100`
- `0 <= vento <= 40`
- `1 <= pesoCarga <= 8`

Caso qualquer entrada esteja fora desses intervalos, a missão é negada.

## Estrutura

- `src/main/java/.../domain/DroneMissionPolicy.java`: regra de negócio
- `src/main/java/.../DroneMissonApp.java`: aplicação console
- `src/test/java/.../NormalBvaTest.java`: BVA normal, `13` casos
- `src/test/java/.../RobustBvaTest.java`: BVA robusto, `19` casos
- `src/test/java/.../WorstCaseBvaTest.java`: worst-case, `125` casos
- `src/test/java/.../RobustWorstCaseBvaTest.java`: robust worst-case, `343` casos
- `src/test/java/.../DroneMissionTestSpecification.java`: limites e decisões compartilhados pelos testes
- `src/test/resources/features/DroneMission-BVA.feature`: documentação executável em Gherkin
- `src/test/java/.../runners/RunCucumberWithJunitTest.java`: runner Cucumber integrado ao JUnit Platform
- `src/test/java/.../runners/RunCucumberWithCli.java`: runner direto da CLI do Cucumber
- `src/test/java/.../steps/DroneMissionSteps.java`: definições dos steps da feature

## Quantidade de casos

Como o problema possui `n = 3` variáveis:

- BVA normal: `4n + 1 = 13`
- BVA robusto: `6n + 1 = 19`
- Worst-case: `5^n = 125`
- Robust worst-case: `7^n = 343`

Total esperado: `500` execuções de teste.

Além dos testes unitários completos, há uma suíte BDD com Cucumber:

- BVA normal completo: `13` exemplos
- BVA robusto completo: `19` exemplos
- worst-case completo: `125` exemplos
- robust worst-case completo: `343` exemplos

Assim, tanto os testes JUnit parametrizados quanto a feature Cucumber apresentam
explicitamente os `500` casos derivados pelas quatro variações de BVA. Os testes
usam tabelas estáticas com `@CsvSource`, sem geração programática. Os limites e
as decisões são definidos uma vez em `DroneMissionTestSpecification`, sem
depender das constantes da implementação, e cada combinação permanece visível
para inspeção em aula.

## Como executar todos os testes

```bash
cd classroom/04-bva/submissions/fabricio-santana
mvn test
```

Esse comando executa os testes unitários JUnit e a feature Cucumber por meio do
JUnit Platform.

## Como executar o Cucumber com o runner JUnit

Para executar somente os cenários Cucumber usando o runner
`RunCucumberWithJunitTest` e o JUnit Platform, use:

```bash
mvn test -Dtest=RunCucumberWithJunitTest
```

O runner utilizado é:

```text
br.edu.idp.es.stsw.bva.runners.RunCucumberWithJunitTest
```

O relatório HTML dessa execução fica em:

```text
target/site/cucumber-reports/Cucumber.html
```

## Como executar o Cucumber com o runner CLI

Para executar o Cucumber diretamente pela CLI, sem usar o JUnit Platform, use:

```bash
mvn test-compile exec:java \
  -Dexec.mainClass=br.edu.idp.es.stsw.bva.runners.RunCucumberWithCli \
  -Dexec.classpathScope=test
```

Esse comando usa a classe:

```text
br.edu.idp.es.stsw.bva.runners.RunCucumberWithCli
```

O goal `test-compile` é necessário porque o runner e as dependências do Cucumber
estão no classpath de testes. O relatório HTML dessa execução fica em:

```text
target/site/cucumber-reports/CucumberCli.html
```

## Como executar a classe principal

Para compilar e executar a aplicação console `DroneMissonApp`, use:

```bash
mvn compile exec:java \
  -Dexec.mainClass=br.edu.idp.es.stsw.bva.DroneMissionApp \
  -Dexec.classpathScope=compile
```

A aplicação solicita bateria, vento e peso da carga e imprime `AUTORIZADA` ou
`NEGADA` no terminal.

## Como visualizar o relatório local do Cucumber

O relatório HTML do Cucumber fica em:

```text
target/site/cucumber-reports/Cucumber.html
```

Para visualizar o relatório gerado pelo runner JUnit com `jwebserver`:

```bash
cd target/site/cucumber-reports
jwebserver -p 8001
```

Depois acesse:

```text
http://localhost:8001/Cucumber.html
```

Para o relatório gerado pelo runner CLI, acesse:

```text
http://localhost:8001/CucumberCli.html
```

## Como gerar cobertura

```bash
cd classroom/04-bva/submissions/fabricio-santana
mvn verify
```

O relatório HTML do JaCoCo fica em:

```text
target/site/jacoco/index.html
```

Para visualizar com `jwebserver`:

```bash
cd target/site/jacoco
jwebserver -p 8000
```

Depois acesse:

```text
http://localhost:8000
```
