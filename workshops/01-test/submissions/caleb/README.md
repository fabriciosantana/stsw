# Playwright (v1.59) — Seminário de Frameworks de Automação de Testes

**Alunos:** Caleb Medeiros, Kelwin Menezes
**Data da apresentação:** 24/04/2026
**Slides:** https://gamma.com.ai/aippt/?shareId=1776366807478086530

---

## Introdução

O **Playwright** é um framework open-source da Microsoft para automação de testes end-to-end (E2E) em aplicações web modernas. Ele permite executar testes em Chromium, Firefox e WebKit a partir de uma única API, suportando JavaScript/TypeScript, Python, Java e .NET.

Na **pirâmide de automação de testes**, o Playwright se posiciona no **topo**, atuando na camada de **Interface de Usuário (UI / E2E)**. Ele exercita a aplicação da mesma forma que um usuário real: abre um navegador, clica em botões, preenche formulários e valida o que aparece na tela. Também pode atuar na camada de **API** (via `request` fixture) quando é preciso validar chamadas de backend no meio do fluxo.

---

## Principais Funcionalidades

- **Multi-navegador:** Chromium, Firefox e WebKit com a mesma API.
- **Auto-wait:** espera inteligente por elementos, sem `sleep` manual.
- **Trace Viewer:** gravação completa do teste (DOM, rede, console, screenshots) para debug post-mortem.
- **Codegen:** gera código de teste gravando interações do usuário no navegador.
- **Testes paralelos e isolados:** cada teste roda em um contexto de navegador independente.
- **Network interception:** mock de requisições HTTP, simulação de falhas, validação de payloads.
- **Tipos de testes possíveis:** caixa-preta (foco principal) e caixa-cinza (combinando UI + API + estado de rede). Não é indicado para caixa-branca pura de unidade.
- **Integrações:** GitHub Actions, GitLab CI, Jenkins, Azure DevOps, Allure, Docker, VS Code extension e reporters customizados (HTML, JUnit, JSON).

---

## Demonstração

A demonstração foi implementada no projeto **LegalFlow / AgentFlow (Modelo SaaS)**, um editor visual de fluxos de agentes de IA baseado em React + Next.js + React Flow. Três suítes de testes Playwright foram construídas para validar a UI completa do editor:

| Suíte | Arquivo | Objetivo |
|---|---|---|
| **Whiteboard** | [src/whiteboard.spec.ts](src/whiteboard.spec.ts) | Valida drag-and-drop de blocos e conexão entre nodes no canvas React Flow. |
| **Visual Test** | [src/visual-test.spec.ts](src/visual-test.spec.ts) | Tour visual com log injetado em tela (overlay verde) demonstrando cada ação. |
| **UI Tour Completo** | [src/ui-tour-completo.spec.ts](src/ui-tour-completo.spec.ts) | Varre todos os componentes do editor: modal de boas-vindas, galeria, sidebar, painel de configuração, controles do React Flow, conexão e deleção de nodes. |

A configuração ([src/playwright.config.ts](src/playwright.config.ts)) usa `headless: false` e `slowMo: 400ms` para permitir observar a execução em tempo real, com `trace: 'retain-on-failure'` e `video: 'retain-on-failure'` para diagnóstico.

**Screenshots capturados durante a execução** ([pasta completa](screenshots/)):

- `01-carregado.png` → aplicação carregada
- `02-welcome-modal.png` → modal de boas-vindas
- `03-galeria.png` → galeria de templates
- `08-sidebar.png` → sidebar de blocos (Entrada, Processamento, Lógica, Saída)
- `20-nodes-arrastados.png` → drag-and-drop de nodes no canvas
- `21-config-panel.png` / `26-config-preenchida.png` → painel de configuração do node
- `27-conectado.png` → conexão entre dois nodes
- `31-node-deletado.png` → deleção de node
- `32-mermaid.png` → exportação para Mermaid
- `33-final.png` → estado final do fluxo montado

**Repositório do projeto de demonstração:** `/Users/calebmedeiros/Trabalholoy/Modelo SaaS` (pasta `packages/frontend/tests`).

---

## Lista de Frameworks Similares

Ferramentas que atuam na mesma camada (UI/E2E) da pirâmide:

- **Cypress** — foco em DX (developer experience) para aplicações web, roda dentro do próprio navegador.
- **Selenium WebDriver** — padrão histórico do mercado, maior suporte a linguagens, mas API mais verbosa.
- **Puppeteer** — antecessor do Playwright, Chromium-only.
- **WebdriverIO** — baseado em WebDriver e Chrome DevTools Protocol, forte em mobile (Appium).
- **TestCafe** — sem necessidade de WebDriver, roda em Node.js puro.

---

## Vantagens e Desvantagens

### Vantagens
- **Velocidade:** mais rápido que Selenium em execuções paralelas.
- **API unificada** para três engines de navegador diferentes.
- **Trace Viewer** é diferencial real para debug em CI.
- **Auto-wait** elimina a maior causa de testes flaky.
- **Documentação oficial excelente**, mantida ativamente pela Microsoft.
- **Codegen** acelera muito a criação de testes iniciais.

### Desvantagens
- **Curva de aprendizado** maior que Cypress para quem nunca usou async/await intensivamente.
- **Ecossistema de plugins** menor que o do Selenium (que tem 15+ anos de comunidade).
- **Não cobre testes mobile nativos** (iOS/Android apps) — só web mobile via emulação.
- **Consumo de recursos** considerável em CI quando se roda os três navegadores em paralelo.

---

## Casos de Sucesso

- **Microsoft** — usa Playwright internamente em VS Code, Azure DevOps e Bing.
- **Disney+** — validação de UI em múltiplos navegadores e resoluções.
- **Adobe** — testes E2E de aplicações web da suíte Creative Cloud.
- **VS Code** — a própria IDE tem sua suíte E2E escrita em Playwright.
- **ING Bank** — migrou de Protractor para Playwright em 2022.

---

## Conclusão

O Playwright é a escolha recomendada para projetos web novos que precisam de testes E2E confiáveis e rápidos, especialmente quando há necessidade de cobrir múltiplos navegadores. O Trace Viewer e o auto-wait resolvem dores antigas de flakiness que são o principal motivo de times abandonarem suítes E2E.

**Quando adotar:** aplicações web modernas (SPA, Next.js, React, Vue, Angular), times que já usam TypeScript, projetos que precisam rodar em Chromium + Firefox + WebKit, pipelines de CI/CD onde estabilidade de testes é crítica.

**Quando NÃO adotar:** aplicações mobile nativas (usar Appium/Detox), times que precisam de testes unitários puros (usar Jest/Vitest), contextos onde o Cypress já está consolidado e migrar não traz ganho claro.

No caso do **AgentFlow / Modelo SaaS**, o Playwright provou ser adequado para validar um editor visual complexo baseado em React Flow com drag-and-drop, algo notoriamente difícil de testar — a API de `dragTo` combinada com `page.mouse` manual cobriu os casos em que o evento sintético do navegador não disparava corretamente.

---

## Instruções para execução do exemplo

Os testes foram desenvolvidos contra o projeto **AgentFlow / Modelo SaaS** (fora deste repositório). Para executá-los localmente:

### 1. Pré-requisitos

- Node.js 18+
- npm
- Projeto LegalFlow / AgentFlow rodando em `http://localhost:3005`

### 2. Clonar ou apontar para o projeto

```bash
cd "/Users/calebmedeiros/Trabalholoy/Modelo SaaS"
npm install
```

### 3. Instalar navegadores do Playwright

```bash
cd packages/frontend
npx playwright install chromium
```

### 4. Subir a aplicação alvo

Em um terminal separado, a partir da raiz do projeto:

```bash
docker-compose up -d        # sobe backend + dependências
cd packages/frontend
npm run dev                 # Next.js em http://localhost:3005
```

### 5. Executar os testes

Dentro de `packages/frontend`:

```bash
# Tour visual completo (modo observável, slowMo ativo)
npm run test:tour

# Versão mais lenta para apresentação
npm run test:tour:slow

# Teste visual com overlay de log
npm run test:visual

# Modo UI interativo do Playwright
npm run test:ui
```

### 6. Visualizar relatório

```bash
npx playwright show-report test-results/report
```

### 7. Reproduzir apenas com os arquivos desta submissão

Os arquivos em [src/](src/) podem ser colocados em `packages/frontend/tests/` (e o `playwright.config.ts` na raiz de `packages/frontend`) para reproduzir exatamente a execução demonstrada no seminário.
