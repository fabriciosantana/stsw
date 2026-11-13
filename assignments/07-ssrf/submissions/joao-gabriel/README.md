# I2-07 — Vulnerabilidades do tipo SSRF

**Aluno:** João Gabriel G. Oliveira
**Disciplina:** Segurança e Teste de Software — IDP · **Prof.:** Fabricio Santana

Resolução dos laboratórios de **Server-Side Request Forgery (SSRF)** do PortSwigger,
usando o **Burp Suite** para interceptar e reenviar as requisições.

| # | Laboratório | Status | Evidência |
|---|-------------|:------:|-----------|
| L1 | Basic SSRF against the local server | ✅ | `img/l1.png` |
| L2 | Basic SSRF against another back-end system | ✅ | `img/l2.png` |
| L3 | SSRF with blacklist-based input filter | ✅ | `img/l3.png` |
| L4 | SSRF with filter bypass via open redirection | ✅ | `img/l4.png` |

### L1 — Basic SSRF against the local server
A loja checa o estoque fazendo o **servidor** buscar uma URL interna (`stockApi`). Mandei a requisição `POST /product/stock` pro Repeater do Burp e troquei essa URL por `http://localhost/admin` — o servidor acessou o painel de admin interno e me devolveu a lista de usuários. Depois apontei a URL pro link de delete e o servidor apagou o `carlos`.

**Parâmetro stockApi:**
```
http://localhost/admin/delete?username=carlos
```

Exploração no Burp (Repeater) — o servidor respondeu `302 Found` executando o delete:
![L1 Burp](img/l1-burp.png)

Lab resolvido:
![L1](img/l1.png)

### L2 — Basic SSRF against another back-end system
Igual ao L1, mas o painel de admin não estava no `localhost` — ficava em outro servidor interno na rede `192.168.0.x` (porta 8080), com IP desconhecido. Mandei a requisição pro **Intruder**, marquei o último octeto do IP como ponto de ataque e varri de `1` a `255`. O IP `192.168.0.232` respondeu `200` (os outros davam `500`), então era ele o admin. Apontei o `stockApi` pro delete nesse IP e apaguei o `carlos`.

**Parâmetro stockApi:**
```
http://192.168.0.232:8080/admin/delete?username=carlos
```

Exploração no Burp (Repeater) — resposta `302 Found` executando o delete no back-end:
![L2 Burp](img/l2-burp.png)

Lab resolvido:
![L2](img/l2.png)

### L3 — SSRF with blacklist-based input filter
Aqui o servidor tinha um filtro que bloqueava `localhost`, `127.0.0.1` e a palavra `admin`. Burlei de duas formas ao mesmo tempo: troquei `127.0.0.1` por `127.1` (forma abreviada do mesmo IP, que o filtro não pega) e escrevi o "a" de `admin` com **dupla codificação de URL** (`%2561`) — o filtro não reconhece a palavra, mas o servidor decodifica de volta pra `admin` na hora de acessar. Com isso cheguei no painel interno e deletei o `carlos`.

**Parâmetro stockApi:**
```
http://127.1/%2561dmin/delete?username=carlos
```

Exploração no Burp (Repeater) — bypass do filtro, resposta `302 Found`:
![L3 Burp](img/l3-burp.png)

Lab resolvido:
![L3](img/l3.png)

### L4 — SSRF with filter bypass via open redirection
Neste o filtro não deixava apontar o `stockApi` direto pra um IP interno. Mas a loja tinha um **open redirect** na função "next product" (`/product/nextProduct?path=...`), que redireciona pra qualquer endereço no parâmetro `path`. Como `/product/nextProduct` é um caminho válido da própria loja, ele passa pelo filtro — e aí o redirect leva o servidor pro admin interno (`192.168.0.12:8080`). Resultado: o servidor seguiu o redirect e deletou o `carlos` (o Response veio com "User deleted successfully!").

**Parâmetro stockApi:**
```
/product/nextProduct?path=http://192.168.0.12:8080/admin/delete?username=carlos
```

Exploração no Burp (Repeater) — o redirect levou ao admin interno e o usuário foi apagado:
![L4 Burp](img/l4-burp.png)

Lab resolvido:
![L4](img/l4.png)
