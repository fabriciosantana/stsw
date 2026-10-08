---
description: Explora uma página do IDP Tarefas como analista de QA, relata os bugs e gera um teste Playwright que os reproduz
---

Página a explorar: a que eu escrevi depois do comando (exemplo: `/cacar-bugs Galeria`). Se eu não escrevi nenhuma, use Tarefas.

1. Com o servidor MCP do Playwright, abra http://localhost:3000 e faça login com `aluno@idp.edu.br` / `playwright123`.
2. Vá até a página escolhida pelo menu do topo.
3. Teste à mão todas as ações da página (botões, campos, filtros, arrastar...). Depois de cada ação, confira os textos e contadores na tela.
4. Confira também a lista de requisições de rede (`browser_network_requests`) e as mensagens do console (`browser_console_messages`), procurando erros 4xx/5xx.
5. Para cada bug encontrado, escreva: passos para reproduzir, resultado esperado e resultado obtido.
6. Crie `tests/gerados-pela-ia/<pagina>.spec.ts` com um teste que reproduza o bug. O teste deve FALHAR enquanto o bug existir.
7. Rode `npx playwright test tests/gerados-pela-ia --project=chromium` e mostre o resultado.
8. Não corrija o código da aplicação. Pare aqui e me mostre o resumo.
