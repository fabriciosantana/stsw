# Workshop 01 - SonarQube

**Aluno:** Guilherme Abade

## Objetivo

Demonstração do SonarQube como ferramenta de apoio à qualidade de software.

O projeto utiliza:

- Java
- Maven
- JUnit 5
- JaCoCo
- SonarQube

A classe `ShippingCostCalculator` é usada como exemplo para executar testes, gerar cobertura e enviar a análise ao SonarQube.

Um dos caminhos de frete expresso fica sem teste de propósito, para demonstrar uma lacuna de cobertura.

## Requisitos

- JDK 17 ou superior
- Maven
- Docker Desktop ou Docker Engine
- SonarQube Community

Os comandos abaixo devem ser executados na pasta da submissão, onde está o arquivo `pom.xml`.

## Executar os testes

```bash
mvn test
```

## Gerar cobertura

```bash
mvn verify
```

O relatório do JaCoCo será gerado em:

```text
target/site/jacoco/index.html
```

## Iniciar o SonarQube

```bash
docker run --rm -d \
  --name sonarqube-demo \
  -p 9000:9000 \
  sonarqube:community
```

Para acompanhar a inicialização:

```bash
docker logs -f sonarqube-demo
```

Quando o servidor estiver pronto, acessar:

```text
http://localhost:9000
```

No primeiro acesso:

```text
Usuário: admin
Senha: admin
```

Depois de alterar a senha, gerar um token em:

```text
My Account -> Security
```

## Configurar o token

No terminal:

```bash
read -rsp 'SONAR_TOKEN: ' SONAR_TOKEN; echo
export SONAR_TOKEN
```

O token não deve ser salvo no repositório.

## Executar a análise no SonarQube

Primeiro, gerar os testes e a cobertura:

```bash
mvn verify
```

Depois, executar o scanner:

```bash
mvn org.sonarsource.scanner.maven:sonar-maven-plugin:5.8.0.7211:sonar \
  -Dsonar.host.url=http://localhost:9000
```

Se tudo funcionar corretamente, o Maven deve terminar com:

```text
BUILD SUCCESS
```

Os resultados estarão disponíveis no dashboard do SonarQube em:

```text
http://localhost:9000
```

Na execução utilizada na demonstração, o projeto apresentou:

- Quality Gate: Passed
- Coverage: 89,5%
- Duplications: 0,0%
- Security: 0 issues
- Reliability: 0 issues
- Maintainability: 1 issue

## O que mostrar na demonstração

Durante a apresentação:

1. executar `mvn verify`;
2. mostrar os testes passando;
3. abrir o relatório do JaCoCo em `target/site/jacoco/index.html`;
4. mostrar o trecho sem cobertura;
5. executar a análise do SonarQube;
6. abrir o dashboard em `http://localhost:9000`;
7. mostrar o Quality Gate;
8. mostrar a cobertura;
9. mostrar as métricas e issues encontradas.

## Encerrar o SonarQube

Ao terminar:

```bash
docker stop sonarqube-demo
```

Como o container foi iniciado com `--rm`, ele será removido automaticamente ao ser parado.
