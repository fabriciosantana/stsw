# I2-08 — Resolver vulnerabilidades do tipo Identification and Authentication Failures e Broken Access Control

Evidências em [`evidencias/portswigger/`](./evidencias/portswigger/). O Juice Shop local já havia sido usado nas atividades 06/09; esta entrega cobre os labs do BurpSuite (3 de autenticação + 9 de controle de acesso).

## Authentication

| Lab | Técnica | Evidência |
|---|---|---|
| Username enumeration via different responses | A resposta do login difere quando o usuário existe ("Invalid username") de quando não existe. Baixei as wordlists oficiais da PortSwigger (listas de 100 usuários e 99 senhas), testei cada usuário com uma senha inválida até achar o único que não dava "Invalid username" (`academico`), e depois testei as senhas contra ele até achar a que redirecionava com 302 (`summer`). | `11-username-enumeration.png` |
| 2FA simple bypass | Fiz o primeiro fator de login como `carlos:montoya` (chegando à tela de código 2FA) e, em vez de informar o código, naveguei direto para `/my-account`. A aplicação não verifica se o segundo fator foi realmente completado antes de considerar a sessão autenticada. | `12-2fa-bypass.png` |
| Password reset broken logic | Pedi a redefinição de senha para **mim mesmo** (`wiener`) e usei o token válido recebido por e-mail. Na página de redefinição, o campo `username` vem oculto no formulário e **não está ligado ao token**: troquei o valor de `wiener` para `carlos`, mantendo o token de wiener, e o servidor trocou a senha de `carlos`. | `13-password-reset-broken-logic.png` |

## Broken Access Control

| Lab | Técnica | Evidência |
|---|---|---|
| Unprotected admin functionality | O `robots.txt` revela o caminho `/administrator-panel`, que não exige autenticação nem autorização; usei-o para deletar o usuário `carlos`. | `14-unprotected-admin.png` |
| Unprotected admin functionality with unpredictable URL | O caminho do painel admin é gerado aleatoriamente a cada carregamento da home e fica embutido em um `<script>` inline (`var adminPanelTag.setAttribute('href', '/admin-XXXXXX')`), mesmo com a condição `isAdmin` em `false`. Extraí o caminho do HTML da própria resposta e o acessei na mesma navegação (o valor muda a cada load). | `15-admin-unpredictable-url.png` |
| User role controlled by request parameter | O papel do usuário é decidido por um **cookie** `Admin=false` enviado ao servidor (não só a UI). Logado como `wiener`, adicionei/forcei o cookie `Admin=true` e acessei `/admin`. | `16-role-controlled-param.png` |
| User role can be modified in user profile | O endpoint `POST /my-account/change-email` aceita um corpo JSON livre; além de `email`, ele também aceita (e aplica) o campo `roleid`. Enviei `{"email":"...","roleid":2}` autenticado como `wiener`, o que me promoveu a administrador. | `17-role-modified-profile.png` |
| User ID controlled by request parameter | `GET /my-account?id=<usuario>` não verifica se o `id` pedido é o do usuário autenticado; acessando `?id=carlos` vi a página de conta de `carlos`, incluindo sua API key, que enviei para `/submitSolution`. | `18-userid-param.png` |
| User ID controlled by request parameter, with unpredictable user Ids | O `id` é um GUID, não um nome. Encontrei o GUID de `carlos` nos links de autor de alguns posts do blog (`/blogs?userId=<guid>`) e usei esse GUID em `/my-account?id=<guid>` para obter sua API key. | `19-userid-unpredictable.png` |
| User ID controlled by request parameter with data leakage in redirect | A mesma falha de IDOR aqui redireciona usuários sem permissão para `/login` (302), mas o **corpo da resposta antes do redirecionamento já contém os dados completos de `carlos`**, incluindo a API key; bastou capturar o corpo da resposta 302 sem seguir o redirecionamento. | `20-userid-redirect-leak.png` |
| User ID controlled by request parameter with password disclosure | A página de edição de conta de outro usuário (`?id=administrator`) inclui um campo oculto `password` já preenchido com a senha em texto claro no HTML; usei essa senha para logar como `administrator` e deletar `carlos` pelo painel admin. | `21-userid-password-disclosure.png` |
| Insecure direct object references | O recurso "Live chat" permite baixar a transcrição da conversa em `/download-transcript/<id>.txt`, sem checar se o `<id>` pertence ao usuário logado. Testando IDs sequenciais encontrei a transcrição `1.txt`, na qual a vítima revelou sua própria senha a um "suporte" falso dentro da conversa; usei essa senha para logar como `carlos`. | `22-idor.png` |

## Observações gerais

- Em todos os labs a conta própria usada para explorar foi `wiener:peter`, salvo indicação contrária (ex.: o lab de reset de senha usa o mesmo princípio, mas o alvo final é `carlos`).
- Nos labs "User ID"/"IDOR" o "flag" de conclusão é obtido com `POST /submitSolution` enviando a API key ou dado sensível encontrado como `answer`.
