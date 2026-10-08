# Seminário: Automação de Testes de API com WireMock e Mockoon

* **Ferramentas:** WireMock (v3.x / Standalone & Node Server) e Mockoon (v8.x / Desktop & CLI)
* **Aluno:** Kayke Santos
* **Linguagem dos Testes:** JavaScript (Node.js Native Test Runner)
* **Data:** Outubro / 2026

---

## 📌 Resumo das Ferramentas

### WireMock
Ferramenta *code-first* e declarativa para simulação e mock de serviços HTTP. Permite criar stubs estáticos, correspondências avançadas via JsonPath/Regex, injeção de falhas de rede (*Fault Injection*) e simulação de latência.

* **Nível na Pirâmide:** Camada de Serviço / API / Integração.
* **Tipos de Teste:** Caixa-preta (validação de contratos e respostas de API) e Caixa-branca/Resiliência (injeção de falhas e cenários stateful).
* **Integrações:** CI/CD (GitHub Actions, GitLab CI), Docker, JUnit/Java, clientes Node.js/JavaScript, Python, Go.

### Mockoon
Ferramenta de virtualização de APIs rápida e visual. Oferece uma interface gráfica intuitiva combinada com um CLI para automação, suporte nativo a dados fictícios com Faker.js e regras condicionais de resposta.

* **Nível na Pirâmide:** Camada de Serviço / API / Integração.
* **Tipos de Teste:** Caixa-preta (validação de payloads, rotas dinâmicas e status codes).
* **Integrações:** Mockoon CLI, Docker, CI/CD, testes de front-end e ferramentas de automação (Playwright, Cypress, Postman).

---

## 🔍 Comparativo Rápido

| Ferramenta | Foco Principal | Vantagens | Desvantagens |
| :--- | :--- | :--- | :--- |
| **WireMock** | Simulação avançada de HTTP e falhas de rede | Injeção de falhas, cenários stateful, alta maturidade | Configuração manual via JSON ou código |
| **Mockoon** | Criação rápida de mocks com GUI e Faker.js | Interface visual intuitiva, templating dinâmico | Menos recursos para injeções de falha em nível TCP |

* **Similares:** Mountebank, MSW (Mock Service Worker), Prism (Stoplight), Hoverfly.
* **Casos de Sucesso:** Netflix, Spotify, Uber (WireMock) e diversas startups e equipes ágeis (Mockoon).

---

## 🧪 Demonstração Prática (JavaScript)

Os testes automatizados foram desenvolvidos em **JavaScript puro** utilizando o **Node.js Test Runner nativo** (`node:test` e `node:assert/strict`), localizados no diretório [`src/`](./src/).

### Cenários Cobertos:
1. **WireMock (`tests/wiremock.test.js`):**
   * `GET /api/users` – Stub estático com payload JSON externo.
   * `POST /api/pagamentos` – Regras condicionais via JsonPath (sucesso `201` vs saldo insuficiente `422`).
   * `GET /api/lento` – Simulação de latência de 3000ms.
   * `GET /api/falha-critica` – Injeção de falha de rede (`CONNECTION_RESET_BY_PEER`).
   * `GET /api/retry-test` – Cenário com estado (*Stateful* que falha com 500 na 1ª requisição e responde 200 na 2ª).

2. **Mockoon (`tests/mockoon.test.js`):**
   * `GET /api/livros` – Consulta a catálogo dinâmico via *Data Bucket*.
   * `POST /api/livros` – Regras condicionais por payload (`preco <= 500` retorna `201`, `preco > 500` retorna `422`).
   * `GET /api/livros/lento` – Latência simulada de 3000ms.

---

## 🚀 Como Executar os Testes

### Pré-requisitos
* **Node.js** (v18+) instalado
* **Mockoon Desktop** (ou `@mockoon/cli`)

### 1. Entrar no diretório do código
```bash
cd workshops/01-test/submissions/kayke-santos/src
```

### 2. Executar os testes do WireMock (Autônomo)
O servidor WireMock sobe automaticamente em segundo plano durante a execução:
```bash
npm run test:wiremock
```

### 3. Executar os testes do Mockoon
Inicie o Mockoon na porta `3001` de uma das duas formas:
* **Via Desktop:** Abra o Mockoon Desktop > *File > Open environment file* > Selecione `mockoon/mockoon-environment.json` > Clique no botão **Play (Verde)**.
* **Via CLI:**
  ```bash
  npx @mockoon/cli start --data ./mockoon/mockoon-environment.json --port 3001
  ```

Em seguida, execute a suíte de testes:
```bash
npm run test:mockoon
```

### 4. Executar Todos os Testes Simultaneamente
Com o Mockoon ativo na porta `3001`:
```bash
npm test
```
