# Prompt 10: Protocolo HTTP

**Aluno:** Guilherme Abade
**Referência principal:** [HTTP: MDN](https://developer.mozilla.org/pt-BR/docs/Web/HTTP)

As requisições, as respostas e os comandos `curl` apresentados têm caráter ilustrativo e destinam-se a um ambiente de estudo local.

## 1. Introdução

O HTTP (HyperText Transfer Protocol) é o protocolo de camada de aplicação empregado na troca de recursos como HTML, JSON, imagens e vídeos na web. Constitui a base de navegadores, APIs REST, aplicativos móveis e da comunicação entre microsserviços. Seu funcionamento assenta-se no modelo requisição/resposta: o cliente envia uma mensagem solicitando um recurso e o servidor devolve uma resposta composta por um código de status e, quando cabível, um corpo.

Em alto nível, o ciclo compreende a resolução do nome do host via DNS, a abertura de uma conexão de transporte (TCP no HTTP/1.1 e no HTTP/2, QUIC sobre UDP no HTTP/3, com TLS quando se trata de HTTPS), o envio da requisição com método, caminho, cabeçalhos e corpo, o processamento no servidor e a devolução da resposta. Ao final, a conexão pode ser reaproveitada por meio de *keep-alive* ou encerrada.

Afirmar que o HTTP é um protocolo "sem estado" significa que cada requisição é tratada de forma independente. O servidor não guarda, por força do protocolo, memória das requisições anteriores de um mesmo cliente. Para preservar contexto, como a sessão de login ou o carrinho de compras, a aplicação recorre a mecanismos complementares: cookies, tokens e sessões mantidas no servidor.

No modelo cliente-servidor, a comunicação é sempre iniciada pelo cliente, seja um navegador, o utilitário `curl` ou um aplicativo, cabendo ao servidor apenas responder. Essa divisão separa responsabilidades, pois concentra a interface no cliente e os dados e as regras de negócio no servidor. Entre as duas pontas podem existir intermediários como proxies, CDNs, balanceadores de carga e gateways.

As versões do protocolo evoluíram conforme as necessidades de desempenho e segurança da web, como resume a tabela a seguir.

| Versão | Ano | Características |
|---|---|---|
| HTTP/0.9 | 1991 | Apenas `GET`, sem cabeçalhos, somente HTML |
| HTTP/1.0 | 1996 | Cabeçalhos, códigos de status, `Content-Type`; uma conexão por requisição |
| HTTP/1.1 | 1997/1999 | Conexões persistentes, `Host` obrigatório, *chunked transfer* e cache mais rico |
| HTTP/2 | 2015 | Formato binário, multiplexação de streams, compressão de cabeçalhos (HPACK) e server push |
| HTTP/3 | 2022 | Transporte sobre QUIC (UDP), sem bloqueio *head-of-line* e TLS 1.3 integrado |

O fluxograma abaixo representa o ciclo de uma requisição.

```mermaid
flowchart LR
    A[Usuário digita URL] --> B[Resolução DNS]
    B --> C[Conexão TCP/QUIC]
    C --> D{HTTPS?}
    D -- sim --> E[Handshake TLS]
    D -- não --> F[Envia requisição HTTP]
    E --> F
    F --> G[Servidor processa]
    G --> H[Resposta: status + headers + corpo]
    H --> I[Navegador renderiza / cliente consome]
    I --> J{Mais recursos?}
    J -- sim --> F
    J -- não --> K[Fecha ou mantém conexão]
```

Uma comunicação simples entre navegador e servidor pode ser exemplificada pela requisição e pela resposta seguintes.

```http
GET /index.html HTTP/1.1
Host: www.exemplo.com
User-Agent: Mozilla/5.0
Accept: text/html
```

```http
HTTP/1.1 200 OK
Content-Type: text/html; charset=utf-8
Content-Length: 48

<html><body><h1>Olá, mundo!</h1></body></html>
```

Após receber o HTML, o navegador identifica elementos como `<link>` e `<script>` e emite novas requisições para obter folhas de estilo, scripts e imagens.

## 2. Estrutura de uma requisição HTTP

Uma requisição organiza-se em três partes. A linha de requisição traz o método, o caminho e a versão, como em `POST /api/login HTTP/1.1`. Os cabeçalhos reúnem pares `Nome: valor` com metadados de host, tipo de conteúdo, autenticação e cookies, e encerram-se por uma linha em branco. O corpo, opcional, carrega os dados enviados e é comum em `POST`, `PUT` e `PATCH`.

A URL relaciona-se diretamente com a requisição, pois dela derivam vários de seus componentes. Tomando `https://api.exemplo.com:443/produtos/10?campo=nome#topo`, o esquema `https` define o uso de HTTP com TLS e a porta padrão 443; o host `api.exemplo.com` compõe o cabeçalho `Host`; o valor `443` indica a porta; o caminho `/produtos/10` e a *query string* `?campo=nome` integram a linha de requisição; e o fragmento `#topo` permanece apenas no cliente, uma vez que não é enviado ao servidor.

Uma requisição montada manualmente, por exemplo com `telnet` ou `nc`, assume a forma a seguir.

```http
POST /api/tarefas HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Content-Length: 41

{"titulo":"Estudar HTTP","feita":false}
```

## 3. Estrutura de uma resposta HTTP

A resposta é composta pela linha de status, que reúne versão, código e frase, como em `HTTP/1.1 404 Not Found`; pelos cabeçalhos, a exemplo de `Content-Type`, `Content-Length`, `Set-Cookie` e `Cache-Control`; e pelo corpo opcional, que transporta o recurso ou a mensagem de erro.

O código de status é um número de três dígitos que informa ao cliente o resultado da requisição de maneira padronizada, dispensando a interpretação do corpo para determinar se houve sucesso ou falha. Uma resposta completa simulada ilustra esses elementos.

```http
HTTP/1.1 201 Created
Date: Fri, 27 Nov 2026 14:00:00 GMT
Server: nginx
Content-Type: application/json; charset=utf-8
Content-Length: 58
Location: /api/tarefas/42
Cache-Control: no-store

{"id":42,"titulo":"Estudar HTTP","feita":false}
```

## 4. Métodos HTTP

Cada método HTTP expressa a intenção de uma operação sobre um recurso. A tabela resume os principais, com suas propriedades de segurança e idempotência.

| Método | Uso | Seguro | Idempotente | Corpo |
|---|---|---|---|---|
| GET | Ler um recurso | Sim | Sim | Não |
| HEAD | Igual ao GET, sem corpo na resposta | Sim | Sim | Não |
| POST | Criar recurso ou executar ação | Não | Não | Sim |
| PUT | Substituir o recurso inteiro | Não | Sim | Sim |
| PATCH | Alterar parte do recurso | Não | Em geral não | Sim |
| DELETE | Remover recurso | Não | Sim | Opcional |
| OPTIONS | Descobrir métodos permitidos ou preflight CORS | Sim | Sim | Não |

Entende-se por método seguro aquele que não altera o estado no servidor, e por idempotente aquele cuja repetição produz o mesmo efeito de uma única execução.

As diferenças entre `GET` e `POST` decorrem de onde os dados trafegam e de como são tratados. O `GET` transmite parâmetros na própria URL, que ficam registrados em histórico, logs e cache, razão pela qual não deve conduzir dados sensíveis; em contrapartida, pode ser cacheado e favoritado. O `POST` envia os dados no corpo, não é idempotente, de modo que uma repetição pode gerar duplicatas, e normalmente não é cacheado.

A escolha entre `PUT` e `PATCH` depende do escopo da alteração. Emprega-se `PUT` quando o cliente envia a representação completa do recurso, hipótese em que os campos ausentes passam a ser considerados removidos ou redefinidos para o padrão. Reserva-se o `PATCH` ao envio apenas dos campos efetivamente alterados, como ocorre ao marcar uma tarefa como concluída.

O método `OPTIONS` retorna os métodos suportados por meio do cabeçalho `Allow`. No navegador, atua no *preflight* do CORS: antes de um `PUT` entre origens distintas, o navegador consulta se a origem, o método e os cabeçalhos pretendidos são permitidos.

Os exemplos a seguir ilustram uma requisição `GET` e uma `POST` com `curl`.

```bash
# GET
curl -i https://api.exemplo.com/tarefas?status=aberta

# POST com JSON
curl -i -X POST https://api.exemplo.com/tarefas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Revisar PR","feita":false}'
```

## 5. Códigos de status

Os códigos de status classificam-se em cinco faixas, cada uma associada a uma categoria de resultado.

| Faixa | Significado |
|---|---|
| 1xx | Informativo (por exemplo, `100 Continue` e `101 Switching Protocols`) |
| 2xx | Sucesso |
| 3xx | Redirecionamento |
| 4xx | Erro do cliente |
| 5xx | Erro do servidor |

Alguns códigos ocorrem com frequência e convém conhecê-los em detalhe, conforme o quadro a seguir.

| Código | Nome | Quando ocorre | Exemplo |
|---|---|---|---|
| 200 | OK | Requisição atendida | `GET /tarefas` retorna a lista em JSON |
| 201 | Created | Recurso criado | `POST /tarefas` responde com `Location: /tarefas/42` |
| 301 | Moved Permanently | URL alterada definitivamente | Redirecionamento de `http://` para `https://` |
| 400 | Bad Request | Requisição malformada ou inválida | JSON com sintaxe quebrada |
| 401 | Unauthorized | Autenticação ausente ou inválida | Requisição sem token recebe `WWW-Authenticate: Bearer` |
| 403 | Forbidden | Autenticado, porém sem permissão | Usuário comum tenta acessar `/admin` |
| 404 | Not Found | Recurso inexistente | `GET /tarefas/999` |
| 500 | Internal Server Error | Erro inesperado no servidor | Exceção não tratada |
| 503 | Service Unavailable | Servidor sobrecarregado ou em manutenção | Resposta com `Retry-After: 120` |

A distinção entre redirecionamento permanente e temporário orienta o comportamento de clientes e de mecanismos de busca. O redirecionamento permanente, representado por `301` e `308`, indica que o recurso mudou de endereço de forma definitiva, de modo que navegadores e buscadores atualizam o vínculo e podem armazená-lo em cache; o `308` preserva o método original. O redirecionamento temporário, expresso por `302`, `307` e `303`, determina o uso do novo endereço apenas naquele momento, como no encaminhamento à tela de login; o `307` mantém o método, ao passo que o `303` força o uso de `GET` após um `POST`, conforme o padrão Post/Redirect/Get.

## 6. Cabeçalhos HTTP

Os cabeçalhos são metadados que governam o conteúdo, a autenticação, o cache, a segurança e a conexão. Entre os de requisição figuram `Host`, `User-Agent`, `Accept`, `Accept-Language`, `Authorization`, `Cookie`, `Content-Type`, `Origin`, `Referer` e `If-None-Match`. Entre os de resposta estão `Content-Type`, `Content-Length`, `Set-Cookie`, `Location`, `Cache-Control`, `ETag`, `WWW-Authenticate`, `Strict-Transport-Security`, `Content-Security-Policy` e `X-Content-Type-Options`.

Alguns cabeçalhos concentram grande parte do uso cotidiano. O `Content-Type` descreve o formato do corpo, como `application/json` ou `text/html`. O `Authorization` transporta credenciais nos esquemas `Basic` ou `Bearer`. Os cabeçalhos `Cookie` e `Set-Cookie` sustentam o estado entre requisições. O `Cache-Control` define a política de cache, e o `Accept` indica os formatos que o cliente aceita.

Uma requisição de autenticação evidencia o papel desses cabeçalhos. O cliente submete as credenciais, recebe um token e passa a enviá-lo nas requisições seguintes.

```http
POST /api/auth/login HTTP/1.1
Host: api.exemplo.com
Content-Type: application/json

{"usuario":"guilherme","senha":"********"}
```

```http
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: no-store

{"access_token":"eyJhbGciOiJIUzI1NiJ9...","token_type":"Bearer","expires_in":900}
```

```http
GET /api/perfil HTTP/1.1
Host: api.exemplo.com
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

A autenticação `Basic` deve trafegar exclusivamente sobre HTTPS, uma vez que Base64 é codificação, e não criptografia.

```http
Authorization: Basic Z3VpbGhlcm1lOnNlbmhh
```

## 7. Cookies, sessões e tokens

O cookie viabiliza a manutenção de estado sobre um protocolo sem estado. O servidor emite `Set-Cookie: nome=valor` acompanhado de atributos, e o navegador armazena o valor e o reenvia automaticamente em `Cookie: nome=valor` a cada requisição dirigida ao mesmo domínio e caminho.

Diversos atributos reforçam a segurança do cookie. O atributo `HttpOnly` impede o acesso via JavaScript por `document.cookie`, o que reduz o risco de roubo de sessão por XSS. O `Secure` restringe o envio ao tráfego sobre HTTPS. O `SameSite`, nos modos `Strict`, `Lax` ou `None`, controla o envio em requisições entre sites e mitiga ataques CSRF. Os atributos `Max-Age` ou `Expires`, `Domain` e `Path` delimitam a duração e o escopo.

A escolha entre sessão baseada em cookie e autenticação baseada em token orienta-se pelo tipo de aplicação, como contrasta a tabela.

| Aspecto | Sessão (cookie) | Token (JWT) |
|---|---|---|
| Estado | Mantido no servidor, com o identificador no cookie | Autocontido no token assinado |
| Revogação | Simples, por remoção da sessão no servidor | Trabalhosa, exige expiração curta ou lista de bloqueio |
| Escalabilidade | Requer armazenamento compartilhado, como Redis | Sem estado, de escala mais simples |
| Envio | Automático pelo navegador, com exposição a CSRF | Manual no `Authorization`, com exposição a XSS quando mantido em `localStorage` |
| Uso típico | Aplicações web tradicionais | APIs, SPAs, aplicativos móveis e microsserviços |

A resposta a seguir configura um cookie de sessão com os atributos de segurança recomendados.

```http
HTTP/1.1 200 OK
Set-Cookie: SESSIONID=8f3a1c9e7b2d; Path=/; Secure; HttpOnly; SameSite=Strict; Max-Age=1800
Content-Type: application/json

{"mensagem":"login realizado"}
```

## 8. Cache no HTTP

O cache evita o novo download de recursos que não sofreram alteração, o que reduz a latência, o consumo de banda e a carga sobre o servidor. O controle ocorre principalmente pelo cabeçalho `Cache-Control`, cujas diretivas definem por quanto tempo e em quais condições um recurso pode ser armazenado. A diretiva `max-age=N` estabelece a validade em segundos; `s-maxage=N` aplica-se somente a caches compartilhados, como os de CDN; `no-cache` permite guardar, mas exige revalidação antes do uso; `no-store` proíbe o armazenamento em qualquer ponto, o que convém a dados sensíveis; `private` restringe o armazenamento ao navegador do usuário; e `public` autoriza qualquer cache intermediário.

A distinção entre cache privado e cache público tem implicações diretas de segurança. O cache privado pertence ao navegador e guarda conteúdo específico de um usuário, como a página "Minha conta". O cache público é compartilhado entre usuários por CDNs e proxies e destina-se a conteúdo idêntico para todos, como o logotipo e as folhas de estilo. Marcar um dado pessoal como `public` pode expô-lo de um usuário a outro.

A validação por `ETag` otimiza o uso do cache. O servidor responde com `ETag: "v5-abc123"`, identificador da versão do recurso. Quando o cache expira, o cliente pergunta por meio de `If-None-Match: "v5-abc123"`. Caso nada tenha mudado, o servidor responde `304 Not Modified` sem corpo; do contrário, devolve `200` com o novo conteúdo e uma nova ETag. Os exemplos abaixo mostram configurações de cache para cenários distintos.

```http
# Arquivo estático versionado (app.3f9a.js)
Cache-Control: public, max-age=31536000, immutable

# Página HTML que muda com frequência
Cache-Control: no-cache
ETag: "home-2026-11-27"

# Dados de conta e respostas com token
Cache-Control: no-store
```

## 9. HTTPS e segurança no transporte

O HTTPS consiste no HTTP transportado sobre TLS, camada que assegura três propriedades. A confidencialidade decorre da criptografia simétrica do tráfego, com algoritmos como AES-GCM e ChaCha20. A integridade resulta da detecção de alterações por mecanismos de autenticação de mensagem. A autenticidade provém do certificado, que comprova a identidade do servidor.

O certificado digital é um documento no formato X.509 que associa uma chave pública a um domínio e é assinado por uma Autoridade Certificadora reconhecida. O navegador valida a cadeia de confiança até uma raiz, além da validade, da correspondência do domínio e do estado de revogação.

Na prática, a diferença entre HTTP e HTTPS determina o grau de exposição dos dados. Sobre HTTP, qualquer intermediário na rede, seja um ponto de Wi-Fi público, o provedor ou um proxy, consegue ler e alterar senhas, cookies e conteúdo. Sobre HTTPS, o intermediário observa apenas o domínio, por meio do SNI, e o volume de tráfego. O HTTPS também condiciona o uso de HTTP/2 nos navegadores, de cookies `Secure`, de Service Workers e da geolocalização.

O estabelecimento da sessão segura ocorre no handshake TLS. Na versão 1.3, o processo inicia com o `ClientHello`, em que o cliente informa versões e cifras suportadas, um número aleatório e sua parte pública do ECDHE. Segue-se o `ServerHello`, em que o servidor comunica a cifra escolhida e sua parte pública, de modo que ambas as partes derivam a chave de sessão por Diffie-Hellman, o que garante *forward secrecy*. Já de forma criptografada, o servidor envia o certificado, o `CertificateVerify`, que comprova a posse da chave privada, e o `Finished`. O cliente valida o certificado e responde com seu próprio `Finished`, após o que os dados da aplicação passam a trafegar cifrados. Esse procedimento consome um RTT, ou nenhum na retomada com 0-RTT.

## 10. APIs REST e HTTP

Uma API é considerada RESTful quando se organiza em recursos identificados por URLs, manipulados pelos métodos HTTP padrão, sem estado entre requisições, com representações em formatos como JSON e respostas cacheáveis quando apropriado. A interface é uniforme e os códigos de status carregam significado.

O uso de métodos e de códigos reflete a semântica de cada operação. O `GET` realiza leitura e responde `200` ou `404`; o `POST` cria e responde `201` com `Location`; o `PUT` e o `PATCH` alteram e respondem `200` ou `204`; o `DELETE` remove e responde `204`. Erros de validação retornam `400` ou `422`, de autenticação `401`, de permissão `403` e de conflito `409`.

Entre as boas práticas para o desenho de endpoints, recomenda-se o uso de substantivos no plural, como `/tarefas` em vez de `/getTarefas`, a expressão de hierarquia por meio de caminhos aninhados, como `/projetos/7/tarefas`, e a aplicação de filtros e paginação na *query string*, como `?status=aberta&page=2&size=20`. Convém ainda versionar a API, padronizar os erros segundo a RFC 9457, exigir HTTPS, autenticar por token, validar as entradas, aplicar limitação de taxa e documentar o contrato com OpenAPI.

A modelagem de uma API de gerenciamento de tarefas sintetiza essas práticas, como mostra a tabela.

| Método | Endpoint | Ação | Sucesso |
|---|---|---|---|
| GET | `/v1/tarefas` | Listar, com filtros `status` e `page` | 200 |
| GET | `/v1/tarefas/{id}` | Detalhar | 200 ou 404 |
| POST | `/v1/tarefas` | Criar | 201 com `Location` |
| PUT | `/v1/tarefas/{id}` | Substituir | 200 |
| PATCH | `/v1/tarefas/{id}` | Atualizar parcialmente, por exemplo `{"feita":true}` | 200 |
| DELETE | `/v1/tarefas/{id}` | Remover | 204 |

A implementação em Spring Boot a seguir concretiza essa modelagem.

```java
@RestController
@RequestMapping("/v1/tarefas")
class TarefaController {
    private final TarefaService service;
    TarefaController(TarefaService service) { this.service = service; }

    @GetMapping
    List<Tarefa> listar(@RequestParam(required = false) String status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    ResponseEntity<Tarefa> buscar(@PathVariable long id) {
        return service.buscar(id).map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<Tarefa> criar(@Valid @RequestBody NovaTarefa dto) {
        Tarefa t = service.criar(dto);
        return ResponseEntity.created(URI.create("/v1/tarefas/" + t.id())).body(t);
    }

    @PatchMapping("/{id}")
    Tarefa atualizar(@PathVariable long id, @RequestBody Map<String, Object> campos) {
        return service.atualizarParcial(id, campos);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> remover(@PathVariable long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
```

## 11. HTTP/2 e HTTP/3

A multiplexação introduzida no HTTP/2 permite que várias requisições e respostas trafeguem simultaneamente em uma mesma conexão TCP, divididas em *frames* de *streams* independentes. No HTTP/1.1, cada conexão atendia uma requisição por vez, o que levava os navegadores a abrir até seis conexões por domínio. O HTTP/2 ainda comprime cabeçalhos por HPACK e permite priorizar streams.

O HTTP/3 adota o QUIC, protocolo de transporte sobre UDP com TLS 1.3 integrado, o que traz efeitos relevantes de desempenho. O handshake torna-se mais rápido, com um RTT ou nenhum na retomada. O bloqueio *head-of-line* do TCP deixa de ocorrer, de modo que a perda de um pacote atrasa apenas o stream afetado. A migração de conexão passa a ser possível por meio do *connection ID*, o que permite alternar de Wi-Fi para rede móvel sem reconectar. O resultado é um desempenho superior em redes móveis e em cenários com perda de pacotes.

A migração para HTTP/2 ou HTTP/3 reduz a latência e o número de conexões, melhora o aproveitamento da banda e acelera o carregamento de páginas com muitos recursos, sem exigir artifícios como *domain sharding* ou concatenação de arquivos. O quadro a seguir compara as três versões.

| Característica | HTTP/1.1 | HTTP/2 | HTTP/3 |
|---|---|---|---|
| Transporte | TCP | TCP | QUIC (UDP) |
| Formato | Texto | Binário | Binário |
| Multiplexação | Ausente, uma requisição por conexão por vez | Presente | Presente |
| Head-of-line blocking | Na aplicação e no transporte | Somente no transporte (TCP) | Ausente |
| Compressão de cabeçalhos | Ausente | HPACK | QPACK |
| TLS | Opcional | Obrigatório na prática | Obrigatório (TLS 1.3) |
| Handshake de nova conexão | TCP e TLS, de 2 a 3 RTT | TCP e TLS, de 2 a 3 RTT | 1 RTT, 0-RTT na retomada |
| Migração de conexão | Ausente | Ausente | Presente |
