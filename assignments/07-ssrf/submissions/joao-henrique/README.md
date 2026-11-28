# I2-07 — Resolver vulnerabilidades do tipo SSRF

Evidências em [`evidencias/portswigger/`](./evidencias/portswigger/). O Juice Shop não tem desafio específico de SSRF nesta versão, então esta entrega cobre os dois labs do BurpSuite indicados.

| Lab | Técnica | Evidência |
|---|---|---|
| Basic SSRF against the local server | O formulário "Check stock" envia uma URL (`stockApi`) de volta ao próprio servidor; troquei o valor por `http://localhost/admin/delete?username=carlos`, fazendo o servidor executar a ação administrativa contra si mesmo. | `09-ssrf-localhost.png` |
| Basic SSRF against another back-end system | Varri a rede interna `192.168.0.0/24` na porta `8080` através do mesmo parâmetro `stockApi`, até achar o painel admin em `192.168.0.160:8080`, e then chamei `/admin/delete?username=carlos` nesse host interno. | `10-ssrf-backend.png` |

## Detalhes

### Lab 1 — SSRF contra o servidor local
O formulário de "verificar estoque" de um produto monta uma requisição `POST /product/stock` com um campo oculto `stockApi` escolhido entre três opções (lojas de Londres, Paris e Milão), cada uma apontando para `http://stock.weliketoshop.net:8080/...`. Por não validar o host, basta substituir o valor por uma URL apontando para o próprio servidor da aplicação (`http://localhost/admin/delete?username=carlos`) para que o back-end faça a requisição em nome do atacante e delete o usuário `carlos`.

### Lab 2 — SSRF contra outro sistema interno
Mesmo mecanismo, mas o alvo não é `localhost`: é um segundo sistema acessível apenas pela rede interna. Fiz uma varredura automatizada trocando o `stockApi` por `http://192.168.0.X:8080/admin` para `X` de 1 a 255 e observando a resposta (`#stockCheckResult`, carregado via AJAX). O host `192.168.0.160` respondeu com o painel administrativo; em seguida usei `http://192.168.0.160:8080/admin/delete?username=carlos` para completar a exploração.
