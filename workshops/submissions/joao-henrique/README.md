# Testcontainers com Java

**João Henrique · Segurança e Teste de Software — 2026.2**

[Slides em PDF](joao-henrique.pdf) · [Código](src/)

## O que é a ferramenta

Testcontainers é uma biblioteca que prepara serviços em contêineres para
testes automatizados. No nosso exemplo, ela inicia um PostgreSQL real para
cada teste, espera o banco ficar pronto e remove o contêiner ao terminar.
Isso permite testar a integração com o banco sem instalar PostgreSQL na máquina.

Usamos **Testcontainers Java 2.0.5**, **JUnit 5** para os testes e **Maven**
para compilar o código e executar a suíte. A imagem do banco é
`postgres:16.4-alpine`.

## Resumo para o seminário

- **Pirâmide de testes:** o uso principal está em integração, serviço e API.
  Também pode preparar infraestrutura para testes de interface.
- **Recursos e integrações:** configuração pelo código, espera pela prontidão,
  host e portas dinâmicos, logs e limpeza. Integra com JUnit 5, CI/CD com Docker
  disponível e navegadores via Selenium. Pode apoiar caixa-preta e caixa-branca;
  a técnica depende dos testes escritos.
- **Alternativas:** Docker Compose organiza uma stack de serviços; H2 é um
  banco incorporado à JVM; Mockito simula colaboradores para testes unitários.
- **Vantagens e limites:** permite testar com a dependência real e isolar os
  dados. Exige acesso ao runtime e consome tempo, CPU, memória e downloads.
  Há documentação e módulos prontos; a curva inicial inclui Docker e a
  configuração do serviço usado.
- **Adoção:** [Uber](https://www.uber.com/gb/en/blog/handling-flaky-tests-java/) e
  [Capital One](https://www.capitalone.com/tech/software-engineering/testcontainers-and-localstack-functional-testing/)
  publicaram relatos de uso. O [Quarkus](https://quarkus.io/guides/dev-services/)
  utiliza Testcontainers em vários Dev Services.
- **Quando adotar:** quando precisamos verificar a integração com uma
  dependência real. Para regras locais, testes unitários simples mantêm o
  retorno rápido. A configuração do contêiner determina o ambiente exercitado.

## O que os testes verificam

1. Cadastrar um usuário e buscar seu e-mail pelo ID.
2. Rejeitar o cadastro de um e-mail duplicado.
3. Desfazer todo um lote de cadastros quando ocorre uma duplicidade.

Cada teste usa seu próprio banco temporário, com dados independentes.

## Como executar no nosso ambiente

### 1. Abrir Docker Desktop

No Windows, abra **Docker Desktop** e aguarde ele iniciar.
No Ubuntu/WSL, precisamos de **JDK 21**, **Maven** e acesso ao Docker.

### 2. Entrar na pasta do projeto

Se clonou o projeto em outro local, abra o terminal na **raiz do
repositório** e execute:

```bash
cd workshops/submissions/joao-henrique
```

Na nossa máquina, também é possível entrar diretamente pelo Ubuntu/WSL:

```bash
cd /home/jhos/work/university/stsw/workshops/submissions/joao-henrique
```

Todos os comandos abaixo devem ser executados nessa pasta. O `pom.xml`
fica em `src/`, por isso usamos `-f src/pom.xml` nos comandos Maven.

Confira se as ferramentas estão disponíveis:

```bash
java -version
javac -version
mvn -version
docker info
```

### 3. Preparar e executar pela primeira vez

Com internet, baixe a imagem do PostgreSQL e execute os testes:

```bash
docker pull postgres:16.4-alpine
mvn -B -f src/pom.xml test
```

O Maven baixa as dependências, compila o projeto e executa os três testes.
Testcontainers cria os bancos e cuida da remoção dos contêineres.

### 4. Executar novamente

Depois da preparação, use o modo offline para as dependências Maven:

```bash
mvn -B -o -f src/pom.xml test
```

O terminal mostra o endereço de cada PostgreSQL. Ao final, o resultado esperado é:

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Isso significa que os três testes passaram e nenhum foi ignorado.

## Mostrar a execução no dashboard

Para esta opção, abra também **Testcontainers Desktop** no Windows,
selecione **Docker Desktop** como runtime e use a mesma conta/perfil do dashboard.
O auxiliar precisa de Python 3 no WSL e do Python incluído no LibreOffice
do Windows, em `C:\Program Files\LibreOffice\program\python.exe`.

Depois de preparar as dependências, execute na mesma pasta:

```bash
python3 src/executar_dashboard.py
```

Esse comando executa os mesmos três testes usando a conexão do Testcontainers
Desktop. Abra [o dashboard](https://app.testcontainers.cloud/dashboard) e
atualize as sessões. A sessão `LOCAL / LIVE` pode reunir várias execuções;
observe os três PostgreSQL adicionados. O resultado dos testes aparece no terminal.

## Se houver problema

- **Docker indisponível:** confira Docker Desktop e sua integração com
  Ubuntu/WSL; repita `docker info`.
- **Dependências ausentes no modo offline:** execute
  `mvn -B -f src/pom.xml test` com internet e tente novamente.
- **Dashboard sem registro:** confira runtime e conta/perfil; teste a conexão
  com `python3 src/executar_dashboard.py --verificar` e atualize a página.

[Documentação oficial do Testcontainers Java](https://java.testcontainers.org/).
