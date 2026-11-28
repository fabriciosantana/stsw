# I2-06 — Resolver vulnerabilidades do tipo Injection

**Laboratório:** OWASP Juice Shop v20.4.0 (local, Node.js, `http://localhost:3000`)

Evidências (prints) em [`evidencias/`](./evidencias/). Captura geral do Score Board em `evidencias/00-score-board-geral.png`.

## Desafios do Juice Shop resolvidos

| Desafio | Categoria | Evidência |
|---|---|---|
| Score Board | Miscellaneous | `00-score-board-geral.png` |
| Login Admin | Injection (SQLi) | `01-login-admin.png` |
| User Credentials | Injection (SQLi, UNION) | `02-user-credentials.png` |
| DOM XSS | XSS | `03-dom-xss.png` |
| Bonus Payload | XSS | `04-bonus-payload.png` |

### Score Board
Encontrar a página de placar escondida em `#/score-board`.

### Login Admin — SQL Injection
Bypass de autenticação no campo de e-mail do login com o payload:
```
' OR 1=1--
```
A cláusula `WHERE email = '' OR 1=1--'` sempre é verdadeira e comenta o restante da query, retornando o primeiro usuário da tabela (administrador).

### User Credentials — UNION SQL Injection
A busca de produtos monta uma query do tipo `WHERE ((name LIKE '%<q>%' OR description LIKE '%<q>%') ...)`. Primeiro determinei o número de colunas com `ORDER BY N` (10 colunas), depois usei:
```
qwert')) UNION SELECT id, email, password, '4','5','6','7','8','9','10' FROM Users--
```
na caixa de busca (`/rest/products/search?q=...`), o que retornou e-mails e hashes MD5 das senhas de todos os usuários (ex.: `admin@juice-sh.op` / `0192023a7bbd73250516f069df18b500`).

### DOM XSS
XSS baseado em DOM via caixa de busca (reflexão via `innerHTML`):
```
<iframe src="javascript:alert(`xss`)">
```

### Bonus Payload
Mesmo sink de XSS, usando o payload bônus que embute o player do SoundCloud com a música tema do Juice Shop.

## Labs do BurpSuite (PortSwigger) — SQL Injection e XSS

Evidências em [`evidencias/portswigger/`](./evidencias/portswigger/).

| Lab | Técnica | Evidência |
|---|---|---|
| SQL injection vulnerability in WHERE clause allowing retrieval of hidden data | `' OR 1=1--` no parâmetro `category` | `01-sqli-hidden-data.png` |
| SQL injection vulnerability allowing login bypass | `administrator'--` no campo de usuário | `02-sqli-login-bypass.png` |
| Reflected XSS into HTML context with nothing encoded | `<script>alert(1)</script>` no parâmetro `search` | `03-xss-reflected.png` |
| Stored XSS into HTML context with nothing encoded | `<script>alert(1)</script>` no comentário de um post do blog | `04-xss-stored.png` |
| DOM XSS in document.write sink using source location.search | `?search="><img src=1 onerror=alert(1)>` | `05-dom-xss-documentwrite.png` |
| DOM XSS in innerHTML sink using source location.search | `?search=<img src=1 onerror=alert(1)>` | `06-dom-xss-innerhtml.png` |
| DOM XSS in jQuery anchor href attribute sink using location.search source | `?returnPath=javascript:alert(document.cookie)` no link de feedback | `07-dom-xss-jquery-href.png` |
| DOM XSS in jQuery selector sink using a hashchange event | `#<img src=1 onerror=print()>`, entregue via Exploit Server (precisa de um evento `hashchange` real após o load) | `08-dom-xss-jquery-selector.png` |

O último lab exige o *Exploit Server* porque digitar a URL com o fragmento diretamente na barra de endereços não dispara o evento `hashchange`; a entrega automática via iframe no servidor de exploração simula a vítima navegando e então mudando o hash.
