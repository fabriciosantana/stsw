# Playwright 1.63 + Playwright MCP 0.0.83

**Aluno:** Marcelo Mello · **Apresentação:** 02/10/2026
**Slides:** [`marcelo-mello.pdf`](marcelo-mello.pdf) · **Código:** [`src/`](src/)

---

## Introdução

O **Playwright** é um framework open source da Microsoft, lançado em 2020, para automação de navegadores e
testes **end-to-end (E2E)** de aplicações web. Com uma única API ele controla os três motores de navegador
(**Chromium, Firefox e WebKit**), em Linux, macOS e Windows, com bibliotecas para **TypeScript/JavaScript,
Python, Java e .NET**.

Por dentro, o código de teste conversa com um *driver* em Node.js, que controla cada navegador pelo seu
**protocolo de depuração** (CDP no Chromium e protocolos equivalentes no Firefox e no WebKit). A conexão é
persistente e orientada a eventos, diferente do WebDriver, que usa uma requisição HTTP por comando. É isso que
viabiliza a espera automática, a interceptação de rede e o isolamento por contexto.

O **Playwright MCP** é o servidor oficial do *Model Context Protocol* que expõe o Playwright como ferramentas
para um modelo de IA. O modelo recebe a página como **árvore de acessibilidade em texto** (e não como imagem),
escolhe uma ação (clicar, digitar, navegar), e o servidor a executa e devolve o código Playwright equivalente.

### Posição na pirâmide de automação de testes

| Camada | Papel do Playwright | No exemplo |
|---|---|---|
| **Exploratório** (acima da pirâmide) | Playwright MCP: uma IA explora a aplicação e transforma o que encontra em teste E2E | Demonstração com o Google Antigravity |
| **Interface / E2E** (topo) | Uso principal: fluxos completos pelo navegador | 39 testes |
| **Serviço / API** (meio) | Fixture `request`: testes HTTP sem navegador | 2 testes |
| **Unidade** (base) | Fora do escopo; ficam com Jest, Vitest, JUnit, pytest | — |

O Playwright não muda a forma da pirâmide: ele **reduz o custo do topo**, deixando os testes E2E mais rápidos e
menos instáveis. Em um projeto real, a lógica de `src/app/app.js` também teria testes unitários na base.

---

## Principais Funcionalidades

### Recursos suportados
- **Auto-waiting:** antes de cada ação, verifica se o elemento está anexado, visível, estável, habilitado e recebendo eventos.
- **Asserções web-first:** o `expect` tenta de novo até a condição ser verdadeira ou o tempo acabar (5 s por padrão).
- **Locators** por papel de acessibilidade (`getByRole`), rótulo, texto e `data-testid`. São preguiçosos, estritos e reavaliados a cada uso.
- **Isolamento:** um `BrowserContext` novo por teste, como um perfil anônimo, criado em milissegundos.
- **Paralelismo** com *workers* e **projects** para rodar o mesmo teste em vários navegadores e dispositivos.
- **Rede:** interceptação e mock com `page.route`, além de testes de API com `request`.
- **Emulação:** dispositivos móveis, toque, geolocalização, fuso horário, permissões e **relógio** (`page.clock`).
- **Arquivos e janelas:** upload, download, diálogos, várias abas e popups, iframes.
- **Teste visual:** `toHaveScreenshot`, que compara a tela pixel a pixel com uma referência.
- **Ferramentas:** Codegen (grava ações como código), UI Mode, Trace Viewer e relatório HTML com screenshot, vídeo e trace.

### Tipos de teste
- **Caixa-preta (principal):** testes E2E e de API, pela interface pública, sem conhecer o código interno.
- **Caixa-cinza:** o teste também pode manipular o ambiente da aplicação, como simular respostas de rede, controlar o relógio e executar JavaScript na página (`page.evaluate`).
- **Caixa-branca:** não é o foco. O suporte a testes de componente (React, Vue, Svelte) existe, mas ainda é experimental.

### Integrações
- **Navegadores:** Chromium, Firefox e WebKit em versões fixadas por release, além do Google Chrome e do Microsoft Edge instalados (`channel`).
- **Test runners:** `@playwright/test` (nativo, em Node); `pytest-playwright` (Python); JUnit e TestNG (Java); NUnit e MSTest (.NET).
- **CI/CD:** GitHub Actions (o `npm init playwright` já gera o workflow), GitLab CI, Azure Pipelines, Jenkins, CircleCI, Bitbucket e outros; há uma imagem Docker oficial (`mcr.microsoft.com/playwright`).
- **Relatórios:** HTML, JUnit XML, JSON, `github`, `blob` (para juntar execuções divididas em partes) e reporters de terceiros, como o Allure.
- **Ecossistema:** extensão oficial para o VS Code; `@axe-core/playwright` para acessibilidade; Playwright MCP para clientes de IA (Antigravity, VS Code, Claude, Cursor).

---

## Demonstração

Foi construída uma aplicação web própria, a **IDP Tarefas** (HTML e JavaScript puros, com servidor Node sem
dependências), com cinco telas. Cada tela exercita um recurso diferente do Playwright. São **41 testes**
automatizados e **2 bugs propositais** para os testes encontrarem.

| Tela | Recurso demonstrado | Teste |
|---|---|---|
| Login | Auto-waiting: o servidor responde em 1 s e não há nenhum `sleep` | `02-login.spec.ts` |
| Tarefas | Listas, checkboxes, helpers reaproveitáveis | `03-tarefas.spec.ts` |
| Loja | Mock de rede (`page.route`): produto falso, erro 500, lista vazia; testes de API sem navegador | `04-rede-e-api.spec.ts` |
| Galeria | Upload e **leitura de pixels** da imagem exibida; nova aba (`popup`); teclado | `06-galeria.spec.ts` |
| Todas | Teste visual (`toHaveScreenshot`) e uma "sabotagem" que só ele detecta | `07-visual.spec.ts` |
| Kanban | Arrastar e soltar (`dragTo`), diálogo `confirm`, download e leitura do CSV | `08-kanban.spec.ts` |
| Foco (Pomodoro) | Relógio falso: **25 minutos testados em menos de 1 segundo** | `09-foco-relogio.spec.ts` |
| Todas | Emulação de celular (Pixel 7) com toque | `10-celular.spec.ts` |
| Todas | Acessibilidade WCAG com axe-core | `11-acessibilidade.spec.ts` |

**Bugs propositais** (testes marcados com `@bug`):
1. O contador de tarefas não volta a subir quando uma tarefa é desmarcada.
2. Uma foto da galeria aponta para um arquivo inexistente: o teste captura a imagem quebrada e o **404**.

**Demonstração com IA:** no Google Antigravity, a IA usa o Playwright MCP para explorar a aplicação, encontrar o
bug do contador, gerar um `.spec.ts` que falha reproduzindo o bug, corrigir o código e rodar o teste de novo,
que então passa.

**Código e instruções:** [`src/`](src/) (as instruções estão no fim deste README).

---

## Lista de Frameworks Similares

| Ferramenta | Nível da pirâmide | Diferença em relação ao Playwright |
|---|---|---|
| **Selenium WebDriver** | Interface | Padrão W3C, o mais antigo e maior ecossistema; usa HTTP por comando e exige esperas explícitas |
| **Cypress** | Interface | Roda dentro do navegador: ótima experiência para front-end, mas limitado com várias abas e domínios; só JS/TS |
| **WebdriverIO** | Interface | Runner em Node sobre WebDriver (e WebDriver BiDi), com suporte a mobile via Appium |
| **Puppeteer** | Interface | Biblioteca do Google para Chrome e Firefox, sem test runner próprio; o Playwright nasceu do mesmo time |
| **TestCafe** | Interface | Funciona por proxy, sem drivers; ecossistema menor |
| **Robot Framework** (Browser Library) | Interface | Testes em palavras-chave; a Browser Library usa o Playwright por baixo |
| **Appium** | Interface (mobile) | Para apps nativos e dispositivos reais, que o Playwright apenas emula |

---

## Vantagens e Desvantagens

| Vantagens | Desvantagens |
|---|---|
| Auto-waiting e asserções web-first reduzem drasticamente os testes instáveis | Os navegadores ocupam espaço em disco e precisam de download (≈ centenas de MB) |
| Uma API, 3 motores, 4 linguagens, com os mesmos recursos em todas | O WebKit do Playwright não é o Safari oficial; mobile é **emulado**, não um dispositivo real |
| Isolamento por contexto e paralelismo nativo: rápido e escalável no CI | Exige familiaridade com `async/await` e promises |
| Depuração excelente: Trace Viewer, UI Mode, Codegen e relatório HTML | Testes visuais dependem do sistema operacional e das fontes: as referências precisam ser geradas no mesmo ambiente |
| Documentação completa, com releases frequentes e mantidas pela Microsoft | Testes de componente ainda são experimentais |
| Recursos avançados nativos: rede, relógio, dispositivos, várias abas | **MCP:** não é determinístico, consome tokens e não é barreira de segurança |

**Maturidade:** projeto ativo desde 2020, com releases frequentes, amplamente adotado e com comunidade grande.
**Curva de aprendizado:** baixa para quem conhece JavaScript ou Python. O Codegen e o UI Mode aceleram o início.
**Performance:** contextos isolados são baratos e os testes rodam em paralelo por padrão, com um *worker* por núcleo disponível.

---

## Casos de Sucesso

A página oficial do Playwright destaca empresas e projetos open source que o adotam, entre eles:
**VS Code**, **Bing**, **Outlook**, **Disney+ Hotstar**, **Material UI**, **ING**, **Adobe**,
**React Navigation** e **Accessibility Insights**.

---

## Conclusão

O Playwright é hoje uma das opções mais completas para testes E2E de aplicações web. Ele ataca diretamente os
três problemas clássicos do topo da pirâmide: a **lentidão** (com paralelismo e contextos isolados), a
**instabilidade** (com auto-waiting e asserções que tentam de novo) e a **fragilidade** (com locators por papel de
acessibilidade). O Playwright MCP adiciona uma camada de exploração assistida por IA, mas o artefato que fica no
repositório continua sendo um teste Playwright comum, determinístico e revisável.

**Quando adotar:**
- Aplicações web modernas (SPA, React, Vue, Angular) que precisam de testes E2E confiáveis no CI.
- Quando é necessário testar vários navegadores, incluindo o motor do Safari.
- Fluxos com várias abas, uploads e downloads, mocks de rede ou controle de tempo.
- Equipes que querem gerar ou explorar testes com IA (via MCP).

**Quando não adotar (ou complementar):**
- Testes de unidade e de lógica de negócio: use Jest, Vitest, JUnit ou pytest, que são mais rápidos e baratos.
- Apps mobile nativos ou testes em dispositivos reais: use Appium.
- Equipes com grande investimento em Selenium e uma grade de execução própria: a migração precisa ser justificada.
- Para o MCP: fluxos críticos no CI não devem depender de IA; ela explora, o teste gerado é que roda.

> **A IA explora. O Playwright executa. O teste fica.**

---

## Instruções para execução do exemplo

**Requisito:** Node.js 18 ou mais novo.

```bash
cd src
npm run setup     # instala as dependências, os navegadores e cria os prints de referência
npm test          # resultado esperado: 41 passed
```

### Comandos da demonstração (dentro de `src/`)

| Comando | O que faz |
|---|---|
| `npm start` | Sobe a aplicação em http://localhost:3000 (login: `aluno@idp.edu.br` / `playwright123`) |
| `npm test` | Todos os testes, sem navegador visível |
| `npm run demo:destaques` | Os 5 testes mais visuais, com navegador visível e em câmera lenta |
| `npm run demo:2` | Login com auto-waiting, com navegador visível |
| `npm run demo:falha` | Os 2 testes que encontram os bugs propositais (falham de propósito) |
| `npm run demo:visual` | Testes visuais |
| `npm run sabotar` | Altera cor, bordas e uma imagem para o teste visual falhar |
| `npm run relatorio` | Abre o relatório HTML (screenshot, vídeo, Trace e diferença visual) |
| `npm run resetar` | Desfaz a sabotagem e devolve a aplicação ao estado original |
| `npm run ui` | UI Mode do Playwright |
| `npm run gravar` | Codegen: grava as ações no navegador como código |
| `npm run mcp:por-dentro` | Conversa com o servidor Playwright MCP sem IA, mostrando as mensagens JSON-RPC |

### Playwright MCP (opcional)

1. Em um terminal, dentro de `src/`: `npm start`.
2. Abra a pasta **`src/`** no Google Antigravity. A configuração em `src/.agents/mcp_config.json` é carregada
   automaticamente, junto com as regras em `src/.agents/rules/`.
3. Cole os prompts de [`src/demo-mcp/PROMPTS.md`](src/demo-mcp/PROMPTS.md) no chat do agente.

Em outros clientes MCP (VS Code, Claude, Cursor), use a configuração padrão:

```json
{
  "mcpServers": {
    "playwright": {
      "command": "npx",
      "args": ["-y", "@playwright/mcp@0.0.83", "--browser=chromium", "--isolated", "--caps=testing,network"]
    }
  }
}
```

### Estrutura do código

```
src/
├── app/                  aplicação IDP Tarefas (HTML, CSS, JavaScript)
├── server.js             servidor HTTP sem dependências (porta 3000)
├── playwright.config.ts  webServer, projects (chromium, demo, firefox, webkit, mobile), trace
├── tests/                41 testes + 2 testes @bug, helpers e fixtures
├── scripts/              relatório, sabotagem, reset, Codegen, demonstração do protocolo MCP
├── .agents/              configuração do Playwright MCP para o Antigravity
└── demo-mcp/PROMPTS.md   prompts da demonstração com IA
```

---

## Referências

- Playwright — documentação oficial: https://playwright.dev
- Playwright MCP — repositório oficial: https://github.com/microsoft/playwright-mcp
- Model Context Protocol — especificação: https://modelcontextprotocol.io
- COHN, Mike. *Succeeding with Agile: Software Development Using Scrum*. Addison-Wesley, 2009.
