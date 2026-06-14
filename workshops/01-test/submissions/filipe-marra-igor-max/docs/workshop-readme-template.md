# SonarQube + JaCoCo - [Seu Nome] - [Data]

## Introducao

- SonarQube e uma plataforma de analise continua de qualidade e seguranca de codigo.
- JaCoCo e uma ferramenta de cobertura de testes para Java.
- Posicionamento na piramide: foco principal em **testes de unidade** e governanca de qualidade de codigo.

## Principais funcionalidades

- Medicao de cobertura de testes por linha e por branch.
- Deteccao de bugs, code smells e vulnerabilidades com regras estaticas.
- Qualidade com Quality Gate no pipeline CI/CD.
- Integracao com Maven, JUnit, Jenkins, GitHub Actions e outras ferramentas.
- Tipos de teste: principalmente caixa-branca (via cobertura e analise de codigo), com suporte indireto a caixa-preta em pipelines maiores.

## Demonstracao

- Exemplo implementado: calculadora de score de seguranca com testes unitarios e cobertura.
- Repositorio com instrucoes: [colar link do seu repositorio]
- Comandos demonstrados:
  - `./mvnw clean verify`
  - `./mvnw sonar:sonar -Dsonar.token=...`

## Frameworks similares

- PIT (mutation testing para Java)
- Cobertura (cobertura para Java)
- PMD, Checkstyle, SpotBugs (analise estatica complementar)
- SonarCloud (versao SaaS do Sonar)

## Vantagens e desvantagens

### Vantagens

- Integracao simples com pipeline.
- Feedback rapido sobre qualidade e cobertura.
- Visualizacao clara para time tecnico e lideranca.

### Desvantagens

- Necessita ajuste de regras para reduzir falso positivo.
- Pode consumir recursos em projetos grandes.
- Governance ruim pode virar "metricas sem acao".

## Casos de sucesso

- Empresas e times enterprise usam SonarQube/SonarCloud para quality gates em PR.
- Projetos Java com CI maduro usam JaCoCo para controle minimo de cobertura e regressao.

## Conclusao

- SonarQube + JaCoCo e uma combinacao forte para elevar qualidade de codigo Java.
- Recomenda-se adotar quando existe pipeline de CI/CD e cultura de testes.
- Nao resolve sozinho qualidade: precisa de boas praticas de teste e revisao.

## Instrucoes para execucao do exemplo

1. Subir SonarQube com Docker Compose.
1. Rodar `./mvnw clean verify` para testes e cobertura.
1. Abrir `target/site/jacoco/index.html`.
1. Rodar `./mvnw sonar:sonar -Dsonar.token=...` para publicar no SonarQube.

