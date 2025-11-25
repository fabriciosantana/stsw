# Seminário – JaCoCo (Java Code Coverage)

**Aluno:** Pedro Calil • **Data:** 30/09/2025  
**Framework:** JaCoCo 0.8.13

## Introdução
JaCoCo é uma biblioteca de *code coverage* para Java. Ela mede quanto do seu código foi realmente executado por testes automatizados, gerando relatórios HTML/XML (linhas, ramos, métodos, classes e complexidade ciclomática).

**Nível na pirâmide de testes:** baseia-se em testes automatizados (principalmente unidade), mas também pode coletar cobertura de testes de integração e de cenários end-to-end via *Java Agent*.

## Principais Funcionalidades
- Contadores: **INSTRUCTION (C0)**, **BRANCH (C1)**, **LINE**, **METHOD**, **CLASS** e **COMPLEXITY**.  
- Relatórios em **HTML** e **XML** (compatível com SonarQube).  
- Integração com **Maven/Gradle**, **JUnit** e **CI/CD** (GitHub Actions, Jenkins, etc.).  
- Regras de cobertura para **quebrar o build** quando abaixo do mínimo.

**Tipos de teste suportados:** caixa-branca (métricas de cobertura) e aplicável a qualquer teste caixa‑preta que execute o código.

**Integrações:** Maven (jacoco-maven-plugin), Gradle (jacoco plugin), JUnit/Jupiter, SonarQube.

## Demonstração
### Exemplo implementado
Classe `TriangleClassifier` com regras (válido 1..200; triângulo válido obedece à desigualdade triangular) e testes JUnit cobrindo caminhos (equilátero, isósceles, escaleno, inválido, fora do intervalo).

### Como executar
```bash
cd src
mvn -q -DskipTests=false clean verify
# Relatório HTML: target/site/jacoco/index.html
# Relatório XML:  target/site/jacoco/jacoco.xml
```

## Lista de Frameworks Similares
- **OpenClover**, **Cobertura** (antigo), cobertura integrada de IDEs (IntelliJ/Eclipse), **Gradle Jacoco plugin** (empacota o JaCoCo), **Kover** (Kotlin).

## Vantagens e Desvantagens
**Vantagens:** maduro, leve, relatórios claros, integração ampla, regras para *quality gates*.  
**Desvantagens:** cobertura ≠ qualidade; pode incentivar “teste para número”; necessidade de configurar exclusões (classes geradas, DTOs).

## Casos de Sucesso (exemplos)
- Projetos Java open‑source (ex.: bibliotecas do ecossistema Spring/Apache) e integração padrão com SonarQube em pipelines corporativos.

## Conclusão
**Quando adotar:** times Java que rodam JUnit e precisam de métricas e *quality gates* no CI.  
**Quando evitar:** quando a equipe confunde cobertura com qualidade real; quando o foco é só E2E sem unidade; projetos não‑JVM.

---

# Instruções para execução do exemplo

1) Requisitos: Java 17+ e Maven 3.9+.  
2) Dentro de `src/`, rode:
```bash
mvn -q -DskipTests=false clean verify
xdg-open target/site/jacoco/index.html || open target/site/jacoco/index.html
```
3) Para forçar *quality gate* (ex.: **Branch ≥ 80%**, **Line ≥ 90%**), o `pom.xml` já inclui `jacoco:check`. O build falha abaixo desses limites.

---

## Estrutura do repositório
```
workshops/
  submissions/
    pedro-calil/
      README.md
      src/
        pom.xml
        src/main/java/stsw/jacoco/TriangleClassifier.java
        src/test/java/stsw/jacoco/TriangleClassifierTest.java
        .github/workflows/ci.yml
```
