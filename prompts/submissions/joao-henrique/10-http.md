# Prompt 10: Introdução ao protocolo HTTP

Referência: [HTTP – MDN](https://developer.mozilla.org/pt-BR/docs/Web/HTTP)

## Introdução

HTTP (HyperText Transfer Protocol) é o protocolo da camada de aplicação que define como cliente e servidor trocam mensagens na web. O cliente (navegador, app, `curl`) envia uma **requisição** e o servidor devolve uma **resposta**. Ele roda sobre TCP (HTTP/1.1 e 2) ou QUIC/UDP (HTTP/3).

**"Sem estado" (stateless):** o servidor não guarda memória entre duas requisições. Cada requisição precisa carregar tudo o que o servidor precisa para atendê-la. Quando a aplicação precisa de estado (login, carrinho), ele é emulado com cookies, sessões ou tokens enviados em cada requisição.

**Cliente-servidor:** o cliente inicia sempre a conversa e o servidor só responde. O cliente cuida da interface; o servidor, dos dados e regras de negócio. Os dois evoluem de forma independente, desde que respeitem o contrato do protocolo.

**Versões:**

| Versão | Característica principal |
|---|---|
| HTTP/1.0 | Uma conexão TCP por requisição |
| HTTP/1.1 | Conexões persistentes, `Host` obrigatório, pipelining (pouco usado), cache e chunked transfer |
| HTTP/2 | Binário, multiplexação de várias requisições na mesma conexão, compressão de cabeçalhos (HPACK) |
| HTTP/3 | Roda sobre QUIC (UDP), elimina o bloqueio de cabeça de fila do TCP, handshake mais rápido |

**Fluxo de uma requisição:**

```
Navegador                         Servidor
   | -- resolve DNS -->  (IP)        |
   | -- abre conexão TCP/TLS ------> |
   | -- GET /index.html ----------->  |
   |                                  | processa (rota, banco, template)
   | <---- 200 OK + HTML ------------ |
   | -- GET /style.css, /app.js ---> |  (recursos do HTML)
   | <---- 200 OK ------------------- |
   | renderiza a página              |
```

**Exemplo navegador/servidor:** ao abrir `http://exemplo.com/`, o navegador envia `GET / HTTP/1.1` com `Host: exemplo.com`; o servidor responde `200 OK` com o HTML; o navegador lê o HTML e faz novas requisições para CSS, JS e imagens.

## Estrutura de uma requisição

Três partes: **linha de requisição** (método, caminho e versão), **cabeçalhos** (pares nome: valor) e **corpo** (opcional, usado em POST/PUT/PATCH).

**URL:** `https://usuario@exemplo.com:443/produtos/10?cor=azul#detalhes`
- esquema (`https`), host (`exemplo.com`), porta (`443`), caminho (`/produtos/10`), query string (`?cor=azul`) e fragmento (`#detalhes`, que não é enviado ao servidor).
- O navegador usa a URL para montar a linha de requisição (`GET /produtos/10?cor=azul`) e o cabeçalho `Host`.

**Exemplo feito à mão (`nc`/telnet):**

```
POST /login HTTP/1.1
Host: exemplo.com
Content-Type: application/x-www-form-urlencoded
Content-Length: 29

usuario=ana&senha=segredo123
```

## Estrutura de uma resposta

Três partes: **linha de status** (versão, código e frase), **cabeçalhos** e **corpo**.

O **status code** é um número de três dígitos que diz ao cliente o resultado: sucesso, redirecionamento, erro do cliente ou erro do servidor.

```
HTTP/1.1 200 OK
Date: Sat, 28 Nov 2026 15:00:00 GMT
Content-Type: text/html; charset=utf-8
Content-Length: 58
Cache-Control: no-store

<html><body><h1>Bem-vinda, Ana!</h1></body></html>
```

## Métodos HTTP

| Método | Uso | Seguro | Idempotente |
|---|---|---|---|
| GET | Ler um recurso | sim | sim |
| POST | Criar recurso / executar ação | não | não |
| PUT | Substituir o recurso inteiro | não | sim |
| PATCH | Alterar parte do recurso | não | não necessariamente |
| DELETE | Remover | não | sim |
| HEAD | Igual ao GET, sem corpo | sim | sim |
| OPTIONS | Descobrir métodos permitidos (e preflight de CORS) | sim | sim |

- **GET x POST:** GET leva os parâmetros na URL (aparecem em log, histórico e cache) e não deve alterar estado. POST leva os dados no corpo e é usado para criar ou alterar.
- **PUT x PATCH:** PUT envia a representação completa (campos ausentes são apagados/zerados). PATCH envia só o que muda.
- **OPTIONS:** pergunta ao servidor o que ele aceita para aquele recurso (`Allow: GET, POST, OPTIONS`). Os navegadores o usam como *preflight* em requisições CORS.

```bash
# GET
curl -i "https://api.exemplo.com/tarefas?concluida=false"

# POST
curl -i -X POST https://api.exemplo.com/tarefas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Estudar HTTP","concluida":false}'
```

## Códigos de status

Categorias: **1xx** informativo, **2xx** sucesso, **3xx** redirecionamento, **4xx** erro do cliente, **5xx** erro do servidor.

| Código | Significado | Exemplo prático |
|---|---|---|
| 200 OK | Deu certo | GET de uma página |
| 201 Created | Recurso criado | POST de nova tarefa (com `Location`) |
| 301 Moved Permanently | Mudou de endereço para sempre | `http://` → `https://` |
| 400 Bad Request | Requisição malformada | JSON inválido |
| 401 Unauthorized | Não autenticado | Falta o token |
| 403 Forbidden | Autenticado, mas sem permissão | Usuário comum tenta acessar `/admin` |
| 404 Not Found | Recurso não existe | URL errada |
| 500 Internal Server Error | Falha inesperada no servidor | Exceção não tratada |
| 503 Service Unavailable | Servidor sobrecarregado ou em manutenção | Deploy em andamento |

**Redirecionamento permanente (301/308):** o recurso mudou de vez; navegadores e buscadores atualizam o endereço e fazem cache. **Temporário (302/307):** mudou só por agora; o cliente continua usando a URL original nas próximas vezes. Os códigos 307/308 preservam o método; 301/302 podem transformar POST em GET.

## Cabeçalhos HTTP

Cabeçalhos são metadados da mensagem: dizem o formato do corpo, quem é o cliente, como armazenar em cache, qual credencial usar etc.

- **De requisição:** `Host`, `User-Agent`, `Accept`, `Authorization`, `Cookie`, `Content-Type`, `Origin`.
- **De resposta:** `Content-Type`, `Set-Cookie`, `Location`, `Cache-Control`, `ETag`, `Strict-Transport-Security`.
- **Mais comuns:** `Content-Type` (formato do corpo), `Authorization` (credenciais), `Cookie` (estado de sessão), `Accept` (o que o cliente aceita).

Exemplo de requisição autenticada:

```
POST /api/pedidos HTTP/1.1
Host: api.exemplo.com
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
Content-Type: application/json
Accept: application/json
```

## Cookies, sessões e tokens

- O servidor envia `Set-Cookie: ...` na resposta; o navegador guarda e devolve em toda requisição ao mesmo domínio no cabeçalho `Cookie`.
- **HttpOnly:** o JavaScript não consegue ler o cookie (reduz o roubo por XSS). **Secure:** o cookie só viaja por HTTPS. Também existe `SameSite`, que ajuda contra CSRF.
- **Sessão em cookie:** o cookie guarda só um ID aleatório; os dados da sessão ficam no servidor. **Token (JWT):** o token assinado carrega as informações do usuário e o servidor só valida a assinatura (sem estado no servidor), mas é mais difícil de revogar.

```
HTTP/1.1 200 OK
Set-Cookie: sessionid=a3f9c1...; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=3600
```

## Cache

- **Cache-Control** define as regras. Diretivas principais: `max-age=N` (segundos de validade), `no-cache` (precisa revalidar antes de usar), `no-store` (nunca guardar), `public`/`private`, `must-revalidate`.
- **Privado x público:** o cache privado fica no navegador de um usuário (pode guardar dados pessoais). O público fica em proxies/CDNs e é compartilhado, então não pode guardar conteúdo de um usuário específico.
- **ETag:** identificador da versão do recurso. O cliente reenvia com `If-None-Match`; se não mudou, o servidor responde `304 Not Modified` sem corpo, economizando banda.

```
# Ativos estáticos versionados
Cache-Control: public, max-age=31536000, immutable
# Página com dados do usuário
Cache-Control: private, no-store
# Validação
ETag: "v123"        (resposta)
If-None-Match: "v123" (requisição seguinte → 304)
```

## HTTPS e segurança no transporte

- **HTTPS = HTTP sobre TLS.** O TLS dá confidencialidade (criptografia), integridade (ninguém altera no caminho) e autenticação do servidor (certificado).
- **Certificado digital:** liga uma chave pública a um domínio e é assinado por uma Autoridade Certificadora (CA) em que o navegador confia.
- **HTTP x HTTPS na prática:** em HTTP o tráfego (senhas, cookies) trafega em texto claro e pode ser lido/alterado numa rede aberta; em HTTPS não.
- **Handshake TLS (resumo, TLS 1.3):** (1) o cliente envia `ClientHello` com as versões e cifras suportadas e sua parte da troca de chaves; (2) o servidor responde com `ServerHello`, o certificado e sua parte da troca de chaves; (3) o cliente valida o certificado (cadeia, domínio, validade); (4) as duas pontas derivam as chaves de sessão e confirmam com `Finished`; (5) a partir daí os dados HTTP vão criptografados com a chave simétrica.

## APIs e HTTP (REST)

- **API RESTful:** recursos identificados por URLs (substantivos), manipulados pelos métodos HTTP, sem estado entre requisições, com representações (geralmente JSON) e códigos de status significativos.
- **Métodos e status:** GET → 200; POST → 201 + `Location`; PUT/PATCH → 200 ou 204; DELETE → 204; erro de validação → 400/422; não encontrado → 404; sem permissão → 401/403.
- **Boas práticas:** URLs no plural e sem verbos, versionamento (`/v1/`), paginação, filtros por query string, erros padronizados, HTTPS, autenticação por token, `Content-Type` correto.

Exemplo de API de tarefas:

| Método e rota | Ação | Resposta |
|---|---|---|
| `GET /v1/tarefas` | Lista | 200 |
| `GET /v1/tarefas/{id}` | Busca uma | 200 / 404 |
| `POST /v1/tarefas` | Cria | 201 |
| `PUT /v1/tarefas/{id}` | Substitui | 200 |
| `PATCH /v1/tarefas/{id}` | Atualiza parte (ex.: concluída) | 200 |
| `DELETE /v1/tarefas/{id}` | Remove | 204 |

## HTTP/2 e HTTP/3

- **Multiplexação (HTTP/2):** várias requisições/respostas em fluxos independentes na mesma conexão TCP, sem abrir várias conexões.
- **QUIC (HTTP/3):** roda sobre UDP, tem TLS 1.3 embutido, handshake de 1 RTT (ou 0-RTT) e perda de pacote afeta só o fluxo atingido, não a conexão inteira. Funciona melhor em redes móveis, onde há troca de rede.
- **Benefícios de migrar:** menos latência, melhor uso da conexão, compressão de cabeçalhos e melhor desempenho em redes instáveis.

| | HTTP/1.1 | HTTP/2 | HTTP/3 |
|---|---|---|---|
| Formato | Texto | Binário | Binário |
| Transporte | TCP | TCP | QUIC (UDP) |
| Multiplexação | Não | Sim | Sim |
| Bloqueio de cabeça de fila | Sim (HTTP) | Sim (TCP) | Não |
| Compressão de cabeçalhos | Não | HPACK | QPACK |
| TLS | Opcional | Na prática, obrigatório | Obrigatório (embutido) |
