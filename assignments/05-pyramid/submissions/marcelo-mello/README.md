# Estudo de Caso: A Pirâmide de Testes (Test Pyramid)

**Aluno:** Marcelo Mello  
**Atividade:** I1-05 - Implementar estudo de caso do test pyramid  
**Disciplina:** Segurança e Teste de Software  

---

## 📌 Visão Geral do Projeto

Este projeto consiste em uma aplicação microsserviço de **Gestão de Usuários** em **Java 17 / Spring Boot 3**, criada para demonstrar a implementação prática das camadas da **Pirâmide de Testes** (*Practical Test Pyramid*, inspirada no artigo de Ham Vocke & Mike Cohn).

---

## 🔺 Estrutura da Pirâmide de Testes no Projeto

```
               / \
              /   \           Nível 3: Testes End-to-End / REST API (Topo)
             / E2E \          - UserManagementE2ETest (MockMvc + Spring Boot)
            /-------\         
           / Integ-  \        Nível 2: Testes de Integração (Meio)
          /  ration   \       - UserRepositoryIntegrationTest (@DataJpaTest + H2)
         /-------------\      - UserServiceIntegrationTest (@SpringBootTest)
        /               \     
       /   Unit Tests    \    Nível 1: Testes Unitários (Base)
      /                   \   - UserServiceTest (JUnit 5 + Mockito)
     /---------------------\
```

---

### 1. Nível 1: Testes Unitários (Base da Pirâmide)
* **Classe:** `UserServiceTest`
* **Tecnologias:** JUnit 5, Mockito (`@ExtendWith(MockitoExtension.class)`), AssertJ.
* **Características:**
  * Execução ultrarrápida em memória isolada sem carregar o contexto do Spring.
  * O repositório de dados (`UserRepository`) é simulado via dublês de teste (*Mocks*).
  * Valida detalhadamente todas as regras de negócio: validação de email, papeis padrão, exceções para emails duplicados e desativação de usuários.

### 2. Nível 2: Testes de Integração (Camada Intermediária)
* **Classes:** `UserRepositoryIntegrationTest` e `UserServiceIntegrationTest`
* **Tecnologias:** `@DataJpaTest`, `@SpringBootTest`, H2 In-Memory Database.
* **Características:**
  * Validam a integração real da lógica de aplicação com a camada de persistência e banco de dados.
  * O `UserRepositoryIntegrationTest` valida o mapeamento JPA, queries customizadas e restrições de unicidade diretamente no banco H2.
  * O `UserServiceIntegrationTest` valida as transações (`@Transactional`) e a persistência real dos dados ao longo do serviço.

### 3. Nível 3: Testes End-to-End / REST API (Topo da Pirâmide)
* **Classe:** `UserManagementE2ETest`
* **Tecnologias:** `@SpringBootTest`, `@AutoConfigureMockMvc`, `MockMvc`.
* **Características:**
  * Testam o ciclo de vida completo (*Core User Journey*) da aplicação via requisições HTTP REST (`POST /api/users`, `GET`, `PUT`, `PATCH`, `DELETE`).
  * Validam códigos de status HTTP (201 Created, 200 OK, 204 No Content, 400 Bad Request) e contratos de resposta JSON.
  * Quantidade menor de testes, focando em fluxos críticos devido ao maior custo de execução.

---

## 🚀 Como Executar os Testes

Navegue até o diretório da submissão:

```bash
cd assignments/05-pyramid/submissions/marcelo-mello
```

### Executar Todos os Testes
Para rodar a suíte completa de testes (Unidade, Integração e E2E) e gerar o relatório de cobertura JaCoCo:

```bash
mvn clean test
```

### Visualizar Relatório de Cobertura JaCoCo
Após executar `mvn test`, o relatório de cobertura de código estará disponível em:

```
target/site/jacoco/index.html
```

---

## 📊 Estrutura de Diretórios

```
assignments/05-pyramid/submissions/marcelo-mello/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/br/edu/idp/es/stsw/pyramid/
    │   │   ├── TestPyramidApplication.java
    │   │   └── user/
    │   │       ├── User.java
    │   │       ├── UserRepository.java
    │   │       ├── UserService.java
    │   │       └── UserController.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/br/edu/idp/es/stsw/pyramid/
            ├── unit/
            │   └── UserServiceTest.java
            ├── integration/
            │   ├── UserRepositoryIntegrationTest.java
            │   └── UserServiceIntegrationTest.java
            └── e2e/
                └── UserManagementE2ETest.java
```
