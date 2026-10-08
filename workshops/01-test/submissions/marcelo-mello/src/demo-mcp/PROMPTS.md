# Prompts da demonstração com Playwright MCP

Pré-requisitos: aplicação rodando (`npm start`) e a pasta `src/` aberta no cliente MCP
(Google Antigravity, com a configuração de `.agents/mcp_config.json`).

## Fluxo principal

**1. Exploração**
```
Use o servidor MCP do Playwright para abrir http://localhost:3000. Faça login com o e-mail aluno@idp.edu.br e a senha playwright123. Depois passe por todas as páginas do menu e descreva, em poucas linhas, as funcionalidades de cada página.
```

**2. Teste exploratório**
```
Aja como um analista de QA. Na página Tarefas, teste pelo navegador: adicionar tarefas, marcar como feita, DESMARCAR e remover. A cada passo, confira o texto do contador de pendentes. Se encontrar um bug, descreva os passos para reproduzir, o resultado esperado e o obtido. Não leia o código-fonte.
```
Resultado esperado: ao desmarcar uma tarefa, o contador não volta a aumentar.

**3. Geração do teste**
```
Transforme esse cenário em um teste Playwright em TypeScript no arquivo tests/gerados-pela-ia/contador.spec.ts. Use getByRole, getByLabel ou getByTestId e reaproveite fazerLogin e adicionarTarefa de tests/helpers.ts. Rode com npx playwright test tests/gerados-pela-ia --project=chromium e mostre o resultado.
```
Resultado esperado: o teste falha, reproduzindo o bug.

**4. Correção**
```
Encontre a causa do bug em app/app.js, corrija com a menor mudança possível e rode o mesmo teste de novo.
```
Resultado esperado: o teste passa.

## Prompts adicionais

- **Imagem quebrada (bug nº 2):** `/cacar-bugs Galeria`. A IA usa `browser_network_requests` e encontra o 404 de `/img/galeria/pordosol.svg`.
- **Arrastar e soltar:** *Na página Kanban, arraste o cartão "Estudar Playwright" para a coluna "Feito" e informe quantos cartões ficaram em cada coluna.*
- **Upload:** *Na Galeria, envie o arquivo tests/fixtures/foto-teste.png pelo botão "Enviar foto" e confirme a mensagem exibida.*
- **Mock de rede:** *Faça a rota /api/produtos responder com status 500, recarregue a página, faça login e descreva o que aparece na Loja do aluno.*

Para desfazer as alterações feitas pela IA: `npm run resetar`.
