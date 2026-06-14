# SerenityBDD (v4.1.20) — Matheus e Leonardo

---

## Introdução

SerenityBDD é um framework de automação de testes open-source para Java que combina testes BDD (Behavior-Driven Development) com geração automática de relatórios detalhados. Ele atua como uma camada sobre ferramentas como JUnit, Cucumber, RestAssured e Selenium, tornando os resultados legíveis tanto para desenvolvedores quanto para stakeholders não técnicos.

**Posição na pirâmide de automação:** SerenityBDD atua principalmente nas camadas de **serviço/API** e **interface (E2E)**, sendo muito utilizado para testes de integração de APIs REST e testes de UI com Selenium/WebDriver.

---

## Principais Funcionalidades

- Integração nativa com **Cucumber (Gherkin)**, permitindo escrita de testes em linguagem natural (PT-BR inclusive)
- Suporte a testes de **API REST** via RestAssured
- Suporte a testes de **UI** via Selenium e Playwright
- Geração automática de **relatórios HTML interativos** com screenshots, logs e rastreabilidade de requisitos
- Integração com **JUnit 4/5** e **TestNG**
- Integração com pipelines **CI/CD** (GitHub Actions, Jenkins, GitLab CI)
- Suporte a **testes de caixa-preta** (via BDD/Cucumber) e **caixa-branca** (via JUnit direto)

---

## Demonstração

O exemplo implementado testa a API pública [JSONPlaceholder](https://jsonplaceholder.typicode.com) cobrindo os cenários:

| Cenário | Tipo | Endpoint |
|---|---|---|
| Listar todos os posts | GET | `/posts` |
| Buscar post por ID | GET | `/posts/1` |
| Criar novo post | POST | `/posts` |
| Post inexistente retorna 404 | GET | `/posts/99999` |

Os testes são escritos em **Gherkin (PT-BR)** e executados via **SerenityBDD + Cucumber + RestAssured**.

---

## Frameworks Similares

| Framework | Nível da Pirâmide | Linguagem |
|---|---|---|
| RestAssured (puro) | API | Java |
| Karate DSL | API | DSL própria |
| Playwright | UI / API | JS, Python, Java |
| Robot Framework | API / UI | Python |
| Cypress | UI / API | JavaScript |

---

## Vantagens e Desvantagens

**Vantagens**
- Relatórios HTML gerados automaticamente, sem configuração extra
- Gherkin em PT-BR facilita comunicação com times de negócio
- Ecossistema maduro (desde 2012) com ampla documentação
- Rastreabilidade de requisitos embutida

**Desvantagens**
- Verboso para projetos pequenos (overhead de configuração Maven)
- Curva de aprendizado maior que RestAssured puro
- Relatórios consomem espaço em disco em suítes grandes
- Requer Java — não tem suporte nativo a Python

---

## Casos de Sucesso

- **Société Générale** — automação de testes de regressão em sistemas bancários
- **ThoughtWorks** — projetos de consultoria com BDD em larga escala
- **Projetos open-source** citados na documentação oficial: [serenity-bdd.info](https://serenity-bdd.info)

---

## Conclusão

SerenityBDD é uma escolha sólida quando o time precisa de **relatórios profissionais**, **BDD com Gherkin** e cobertura de **API + UI** em um único framework. Não faz sentido adotá-lo para projetos pequenos ou em times que usam Python/JavaScript como stack principal.

**Adote quando:** projeto de médio/grande porte em Java, necessidade de rastreabilidade de requisitos, stakeholders não técnicos precisam ler os relatórios.

**Evite quando:** projeto pequeno, stack não é Java, ou você só precisa de testes de unidade simples.

---

## Instruções para Execução

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Conexão com internet (a API JSONPlaceholder é online)

### Verificar instalação

```bash
java -version
mvn -version
```

### Clonar / abrir o projeto

```bash
cd src/
```

### Rodar os testes

```bash
mvn clean verify
```

### Ver o relatório HTML

Após a execução, abra no navegador:

```
target/site/serenity/index.html
```

### Estrutura do projeto

```
src/
├── pom.xml
└── src/
    └── test/
        ├── java/demo/
        │   ├── runners/CucumberRunner.java   ← ponto de entrada
        │   └── steps/PostsApiSteps.java      ← implementação dos steps
        └── resources/
            ├── features/posts_api.feature    ← cenários em Gherkin (PT-BR)
            └── serenity.properties           ← configuração do Serenity
```
