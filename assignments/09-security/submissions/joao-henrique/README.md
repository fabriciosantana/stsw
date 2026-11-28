# I2-09 — Resolver laboratórios de Security Misconfiguration, Vulnerable and Outdated Components e Security Logging and Monitoring Failures

**Juice Shop:** desafios resolvidos na instância local já usada nas atividades 06 e 10 (evidências em [`evidencias/`](./evidencias/)).
**BurpSuite:** 4 labs de information disclosure (evidências em [`evidencias/portswigger/`](./evidencias/portswigger/)).

## Juice Shop — Security Misconfiguration

| Desafio | Resposta | Evidência |
|---|---|---|
| Provoke an error that is neither very gracefully nor consistently handled | Obtido como efeito colateral de manipular a URL do mecanismo de busca durante a atividade de XSS (`#/search?q=...`), o que já gera um erro de tratamento inconsistente no backend. | — (resolvido de passagem; sem print isolado) |
| Use a deprecated B2B interface that was not properly shut down | O formulário de reclamação (`/#/complain`) ainda aceita o upload de um arquivo de fatura (`Invoice:`), remanescente da antiga interface B2B. Bastou enviar qualquer arquivo (`test.xml`) nesse campo para que o servidor reconhecesse o uso da interface legada — não é necessário nenhum ataque, só usá-la. | `03-deprecated-interface.png` |

## Juice Shop — Vulnerable and Outdated Components

| Desafio | Resposta | Evidência |
|---|---|---|
| Typosquatting imposter no frontend | O pacote `ngy-cookie`, incluído entre as dependências Angular do frontend (`3rdpartylicenses.txt`), é um **typosquat** do pacote legítimo `ngx-cookie`. Reportei via formulário de feedback (`/#/contact`). | `01-typosquatting.png` |
| Biblioteca vulnerável | O backup `ftp/package.json.bak` (do desafio de atividade anterior) lista duas dependências fixadas em versão exata (sem `~`/`^`, diferente de todas as outras): `express-jwt@0.1.3` e `sanitize-html@1.4.2`. Reportei **`sanitize-html@1.4.2`**, que tem uma vulnerabilidade de alta severidade conhecida (CVE-2017-16021, ReDoS). | `02-vulnerable-library.png` |

## Labs do BurpSuite — Security Logging (Information Disclosure)

| Lab | Técnica | Evidência |
|---|---|---|
| Information disclosure in error messages | Enviar `productId=xyz` (não numérico) para `/product?productId=` provoca um erro 500 com stack trace completo do Java, que revela no rodapé a versão exata do framework usado: `Apache Struts 2 2.3.31`. Submeti essa string como resposta. | `23-infoleak-error-messages.png` |
| Information disclosure on debug page | O endpoint de debug `/cgi-bin/phpinfo.php` está acessível e exibe as variáveis de ambiente do servidor, incluindo `SECRET_KEY=afdhj9uy9p2iibqg68jxiu10exogesxv`. Submeti essa chave como resposta. | `24-infoleak-debug-page.png` |
| Source code disclosure via backup files | O `robots.txt` indica `Disallow: /backup`; o diretório tem *directory listing* aberto e expõe `ProductTemplate.java.bak`, cujo código-fonte contém a credencial do banco de dados em texto claro: `r1prf0kprrm4ug0kf2py7e90wfa906cp`. | `25-infoleak-backup-files.png` |
| Authentication bypass via information disclosure | Uma requisição `TRACE / HTTP/1.1` é refletida pelo servidor e revela um cabeçalho customizado adicionado pelo proxy/front-end, `X-Custom-IP-Authorization`, com o IP de origem do cliente. Enviando esse mesmo cabeçalho manualmente com o valor `127.0.0.1` em `GET /admin`, o back-end aceitou a requisição como vinda do servidor local (confiável) e permitiu deletar o usuário `carlos` em `/admin/delete?username=carlos`. | `26-auth-bypass-infoleak.png` |

## Observações

- Os quatro labs de "Information Disclosure" cobrem exatamente a categoria "Security Logging" pedida no roteiro da atividade (vazamento de informação sensível por más práticas de logging/erro/debug).
- Não encontrei neste roteiro labs específicos de "Vulnerable and Outdated Components" ou "Security Misconfiguration" no BurpSuite além dos quatro listados; os dois primeiros ficaram cobertos pelos desafios do Juice Shop acima.
