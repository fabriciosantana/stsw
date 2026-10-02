# 🎓 Seminário de Testes de Integração: WireMock vs Mockoon

Repositório prático para demonstração e execução de testes automatizados de APIs mockadas utilizando **WireMock** e **Mockoon**.

---

## 📋 Pré-requisitos
- **Node.js** (v18+)
- **Mockoon Desktop** (para a interface gráfica do Mockoon)

---

## ⚡ 1. Como Testar o WireMock

O WireMock demonstra: **Stubs estáticos**, **JsonPath matching**, **Latência fixa de 3s**, **Fault Injection (Connection Reset)** e **Cenários Stateful (Retry 500 ➔ 200)**.

### Executar os testes automatizados do WireMock:
```bash
npm run test:wiremock
```

---

## 🔮 2. Como Testar o Mockoon

O Mockoon demonstra: **Catálogo via Data Bucket (`catalogo_livros`)**, **Faker.js Templating**, **Regras Condicionais (Rules 201 Created vs 422 Unprocessable Entity)** e **Latência de 3s**.

### Iniciar o Mockoon (Porta 3001):
1. Abra o aplicativo **Mockoon Desktop**.
2. Vá em **File > Open environment file** e selecione o arquivo `mockoon/mockoon-environment.json`.
3. Clique no botão **Play (Verde)** no topo para iniciar na porta `3001`.

### Executar os testes automatizados do Mockoon:
```bash
npm run test:mockoon
```

---

## 🧪 3. Executar Todos os Testes Juntos

Para rodar simultaneamente as duas suítes de testes (WireMock + Mockoon):

```bash
npm test
```

---

## 📁 Estrutura do Projeto

```text
├── mockoon/
│   └── mockoon-environment.json    # Ambiente com as rotas, Data Bucket e Callback
├── wiremock/
│   ├── mappings/                   # Arquivos de Stubs JSON (Matching, Delay, Fault, Retry)
│   └── __files/                    # Payloads estáticos externos (ex: users.json)
├── tests/
│   ├── mockoon.test.js             # Suíte de testes automatizados do Mockoon
│   └── wiremock.test.js            # Suíte de testes automatizados do WireMock
├── test-runner.js                  # Executor integrado de testes
├── package.json                    # Scripts npm do projeto
└── README.md                       # Documentação única e centralizada
```
