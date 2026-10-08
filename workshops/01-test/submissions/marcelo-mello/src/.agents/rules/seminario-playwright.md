---
trigger: always_on
---

# Contexto do projeto: seminário de Playwright

## A aplicação em teste
- "IDP Tarefas", rodando em http://localhost:3000 (sobe com `npm start`).
- Login: `aluno@idp.edu.br` / `playwright123`. O servidor demora 1 segundo para responder o login.
- Páginas no menu do topo: **Tarefas** (com a Loja do aluno), **Kanban**, **Galeria** e **Foco**.

## Como usar o navegador
- Para abrir e controlar o navegador, use SEMPRE as ferramentas do servidor MCP **playwright**
  (`browser_navigate`, `browser_snapshot`, `browser_click`, `browser_fill_form`, `browser_network_requests`...).
  Não use o navegador embutido do Antigravity: esta demonstração é sobre o Playwright MCP.
- Antes de agir numa página, leia o snapshot. Depois de cada ação, confira o resultado na tela.
- Se http://localhost:3000 não responder, peça para rodar `npm start` e pare.

## Como escrever testes
- TypeScript com `@playwright/test`. Testes novos vão em `tests/gerados-pela-ia/`.
- Localizadores: `getByRole`, `getByLabel`, `getByText`, `getByTestId`. Nunca seletores CSS ou XPath.
- Reaproveite `fazerLogin`, `adicionarTarefa` e `irPara` de `tests/helpers.ts`.
- Rode com: `npx playwright test <arquivo> --project=chromium` e mostre o resultado.

## Não altere
- Os testes `tests/01` a `tests/11`, a pasta `tests/__screenshots__` e a pasta `scripts/backup`.
- Só mexa em `app/` quando eu pedir uma correção.

## Respostas
- Em português do Brasil, curtas e diretas. Ao relatar um bug: passos, resultado esperado e resultado obtido.
