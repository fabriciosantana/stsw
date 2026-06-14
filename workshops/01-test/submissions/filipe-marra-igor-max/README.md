# SonarQube + JaCoCo (v10.x / v0.8.x)

**Alunos:** Filipe Marra e Igor Max  
**Data da Apresentacao:** Abril de 2026

---

## Introducao

**SonarQube** e uma plataforma de inspecao continua de qualidade de codigo. Ela realiza analise estatica para identificar bugs, vulnerabilidades e code smells, alem de gerar indicadores de qualidade e governanca tecnica.

**JaCoCo** e uma ferramenta de cobertura para projetos Java/JVM. Ela mede quais instrucoes, linhas, metodos e branches foram executados pelos testes automatizados.

### Posicao na Piramide de Automacao de Testes

As duas ferramentas atuam de forma complementar:

- **JaCoCo** atua sobre testes de unidade/integracao, medindo cobertura.
- **SonarQube** consolida qualidade de codigo e cobertura no mesmo dashboard.

```text
         /\
        /UI\          <- menos testes, mais lentos
       /----\
      / API  \        <- integracao/servicos
     /--------\
    / Unidade \      <- mais testes, mais rapidos <- JaCoCo atua aqui
   /____________\
         |
 [SonarQube analisa o codigo e consome o relatorio do JaCoCo]
```

---

## Principais Funcionalidades

### SonarQube

- Analise estatica de codigo (bugs, vulnerabilidades e code smells)
- Dashboard com metricas de qualidade
- Quality Gates para acompanhamento de qualidade
- Historico de evolucao da base
- Integracao com pipeline CI/CD

### JaCoCo

- Cobertura por instrucoes, linhas, metodos e branches
- Integracao com Maven via plugin
- Relatorios HTML, XML e CSV
- Integracao direta com JUnit

### Tipos de teste

| Ferramenta | Caixa-branca | Caixa-preta | Integracao com JUnit |
|---|---|---|---|
| JaCoCo | Sim (instrumentacao de bytecode) | Nao | Sim |
| SonarQube | Sim (analise estatica) | Nao | Sim (via relatorio) |

---

## Demonstracao

O exemplo implementado neste repositorio e um **avaliador de score de seguranca** em Java, com:

- Calculo de score com regras de penalidade
- Classificacao de risco (`LOW`, `MEDIUM`, `HIGH`, `CRITICAL`)
- Testes automatizados com JUnit 5 cobrindo cenarios validos, limites e excecoes

Classes principais:

- `SecurityScoreCalculator`
- `RiskLevel`
- `SeminarioApplication`

Testes:

- `SecurityScoreCalculatorTest`
- `SeminarioApplicationTest`

---

## Frameworks Similares

### Alternativas ao SonarQube

- PMD
- SpotBugs
- Checkstyle
- Semgrep
- SonarCloud (SaaS da familia Sonar)

### Alternativas ao JaCoCo

- Cobertura
- OpenClover
- Istanbul/nyc (JavaScript/TypeScript)
- Coverage.py (Python)
- gcov (C/C++)

---

## Vantagens e Desvantagens

### SonarQube

| Vantagens | Desvantagens |
|---|---|
| Dashboard centralizado de qualidade | Requer infraestrutura local quando self-hosted |
| Visao de bugs, vulnerabilidades e smells | Configuracao inicial exige ajuste |
| Integracao com processo de engenharia | Pode aumentar tempo de pipeline em bases grandes |

### JaCoCo

| Vantagens | Desvantagens |
|---|---|
| Integracao simples com Maven | Cobertura alta nao garante qualidade de teste |
| Relatorio claro para analise tecnica | Focado no ecossistema Java/JVM |
| Formatos de relatorio padrao (HTML/XML/CSV) | Requer interpretacao correta das metricas |

---

## Casos de Sucesso

- Projetos Java open-source usam JaCoCo em pipelines de validacao.
- Times corporativos adotam SonarQube/SonarCloud para padrao de qualidade em PRs.
- A combinacao SonarQube + JaCoCo e comum em esteiras de CI de projetos Java.

---

## Conclusao

A combinacao **SonarQube + JaCoCo** atende bem projetos Java que precisam de acompanhamento continuo de qualidade.

- O JaCoCo mostra o quanto o codigo esta coberto por testes.
- O SonarQube mostra a saude tecnica do codigo.
- Juntos, entregam visibilidade para evolucao sustentavel do projeto.

---

## Instrucoes para Execucao do Exemplo

### Pre-requisitos

- Java 17+
- Docker Desktop
- Internet na primeira execucao

### 1. Clonar o repositorio

```bash
git clone https://github.com/filipemarraa/Semin-rio-sobre-Frameworks-de-Automa-o-de-Testes.git
cd Semin-rio-sobre-Frameworks-de-Automa-o-de-Testes
```

### 2. Executar testes e gerar relatorio JaCoCo

```powershell
.\mvnw.cmd clean verify
```

Relatorio gerado em:

```text
target/site/jacoco/index.html
```

### 3. Subir SonarQube local

```powershell
.\scripts\start-sonarqube.ps1
```

Acesso:

- URL: `http://localhost:9000`
- Login inicial: `admin` / `admin`

### 4. Gerar token no SonarQube

1. Acesse `http://localhost:9000`
1. Va em `My Account -> Security -> Generate Tokens`
1. Copie o token

### 5. Enviar analise para o SonarQube

```powershell
$env:SONAR_TOKEN="SEU_TOKEN_AQUI"
.\mvnw.cmd sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=$env:SONAR_TOKEN
```

### 6. Visualizar resultado

1. Acesse `http://localhost:9000/projects`
1. Abra o projeto `seminario-sonarqube-jacoco`

### 7. Encerrar ambiente local

```powershell
.\scripts\stop-sonarqube.ps1
```

---

## Estrutura do Projeto

```text
.
|-- pom.xml
|-- README.md
|-- docker-compose.yml
|-- sonar-project.properties
|-- mvnw
|-- mvnw.cmd
|-- scripts/
|   |-- start-sonarqube.ps1
|   |-- stop-sonarqube.ps1
|   `-- run-quality.ps1
|-- src/
|   |-- main/java/br/com/filipemarraa/seminario/
|   |   |-- RiskLevel.java
|   |   |-- SecurityScoreCalculator.java
|   |   `-- SeminarioApplication.java
|   `-- test/java/br/com/filipemarraa/seminario/
|       |-- SecurityScoreCalculatorTest.java
|       `-- SeminarioApplicationTest.java
`-- docs/
    `-- workshop-readme-template.md
```

