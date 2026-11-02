# P2-12 — OWASP Top Ten: Server-Side Request Forgery (SSRF)

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software — IDP  
**Professor:** Fabricio Santana  
**Referência da tarefa:** [`prompts/12-ssrf.md`](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/12-ssrf.md)  
**Categoria estudada:** OWASP Top 10 **A10:2021** — SSRF

> **Escopo e método.** Respondo às sete seções do roteiro da disciplina, com explicação conceitual, trechos de código, estudo de caso e uma simulação **executada em localhost**. O laboratório usa serviços e dados *fictícios* e não acessa sistemas de terceiros. A simulação não equivale à resolução de laboratórios PortSwigger/Juice Shop da atividade I2-07. Etapas de ferramentas externas são identificadas separadamente conforme sua execução efetivamente comprovada.

## 1. Introdução à vulnerabilidade SSRF

**O que é?** SSRF (*Server-Side Request Forgery*, falsificação de requisição do lado do servidor) ocorre quando um usuário consegue influenciar o **destino de uma solicitação feita pelo próprio servidor**, normalmente por um parâmetro de URL não confiável. Imagine um serviço que baixa uma imagem fornecida pelo cliente. O comportamento esperado é acessar uma imagem pública; o comportamento perigoso é transformar a aplicação em intermediária para chegar a recursos que o próprio usuário não acessaria diretamente.

```text
          URL fornecida pelo usuário
CLIENTE -------------------------------> APLICAÇÃO /fetch?url=...
                                               |
                                        requisição feita PELO SERVIDOR
                                               |
                 +-----------------------------+--------------------------+
                 |                             |                          |
            imagem pública            localhost / administração    serviço interno/cloud
               (esperado)              (não autorizado)             (não autorizado)
```

**Onde surge e por que é crítica?** É frequente em importadores de URLs, pré-visualizadores de links, geradores de PDF, webhooks, integração com APIs, importação de imagens e serviços que seguem redirecionamentos. O risco não decorre de uma URL perigosa por si só: vem de o **servidor ter conectividade, permissões e identidade diferentes** das do usuário externo. Uma solicitação aparentemente inocente pode atravessar limites de rede e atingir serviços internos. Severidade depende do ambiente; pode chegar a vazamento de dados e ações privilegiadas.

**Servidor versus cliente.** Em ataques *client-side*, a ação ocorre no navegador do visitante. Em SSRF, quem abre a conexão é a aplicação no backend; por isso endereço `127.0.0.1` representa **o host do servidor que processa o pedido**, não o computador do atacante. Uma aplicação hospedada em nuvem pode alcançar endpoints de metadados de instância, endpoints internos de Kubernetes e APIs administrativas inacessíveis a partir da Internet.

**Alvos comuns:** interfaces de administração em `localhost`, serviços privados (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), endereços link-local como `169.254.169.254`, interfaces IPv6 locais, servidores Redis/Elasticsearch ou dashboards internos, além de serviços que confiam na origem da conexão.

**Por que entrou no OWASP Top 10 em 2021?** A OWASP registrou **A10:2021** como categoria nova, impulsionada pela pesquisa com a comunidade de segurança, mesmo com incidência estatística menor que a de outras categorias. O aumento de funcionalidades que recuperam URLs, o uso de nuvem e a arquitetura distribuída ampliaram a relevância do problema. No documento de 2021, a OWASP informa média de incidência de **2,72%** entre o conjunto testado para essa categoria e alto potencial de exploração, sem que isso signifique que exatamente 2,72% de *todas* as aplicações existentes sejam vulneráveis. [Fonte OWASP Top 10:2021](https://top10.owasp.org/2021/pt-BR/A10_2021-Server-Side_Request_Forgery_%28SSRF%29/).

**Principais impactos:** acesso a APIs administrativas, descoberta de serviços internos, exposição de segredos, abuso de credenciais fornecidas pela aplicação, acesso a metadados em nuvem, movimentação lateral e negação de serviço. Em microserviços, a comunicação entre componentes é extensa e frequente; políticas de rede permissivas podem transformar um único ponto de SSRF em uma ponte para várias APIs.

## 2. Exemplos práticos de ataques SSRF

### 2.1 Aplicação que importa imagens

Uma API recebe `GET /preview?url=https://cdn.exemplo.test/foto.png`, baixa a imagem e a exibe. Se não impõe uma política sobre **destino efetivo**, o usuário pode passar uma URL que aponte para uma interface interna ou para uma URL pública que redirecione ao destino proibido.

**Exemplo didático de URL suspeita, para laboratório autorizado:**

```text
http://127.0.0.1:19081/admin/status
```

Se a aplicação executar a requisição e devolver a resposta, ela terá acessado o próprio serviço interno por conta do usuário. Não são necessários dados de usuários reais nem rede corporativa para demonstrar o defeito.

### 2.2 Exemplo de risco AWS (somente conceitual)

Em algumas configurações de instância EC2 existe um serviço de metadados acessível pelo endereço link-local:

```text
http://169.254.169.254/latest/meta-data/
```

Uma aplicação vulnerável que recupere URLs arbitrárias **poderia**, em circunstâncias específicas, consultar esses metadados. A disponibilidade de credenciais e o sucesso do ataque dependem da configuração de rede, das permissões do processo e do uso de controles como **IMDSv2**, que requer obtenção de token por requisição específica, em vez de assumir que uma única requisição GET basta. Este endereço **não foi consultado** neste trabalho.

### 2.3 Código Java vulnerável (exemplo para leitura, não para produção)

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public String baixarUrlNaoConfiavel(String url) throws Exception {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
    return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
}
```

**Falha:** o código não define quais hosts/IPs/portas podem receber requisições. A validação de ser uma `URI` sintaticamente válida não é uma autorização para acessá-la. Se o usuário define o destino, o servidor pode encaminhar chamadas não autorizadas.

### 2.4 SSRF simples, SSRF cego e descoberta de portas

- **SSRF simples (com resposta):** o aplicativo exibe os dados retornados pelo alvo; é possível ver, por exemplo, uma resposta de um serviço interno fictício.
- **SSRF cego (*blind*):** o aplicativo não mostra a resposta, mas o request pode chegar ao destino. Indicadores incluem registros de acesso de um servidor de teste que controlamos ou sinais de tempo/erros. Esses indícios precisam ser interpretados com cuidado para evitar falsos positivos.
- **Mapeamento de portas:** em um laboratório, um avaliador poderia comparar status HTTP, atrasos e erros ao solicitar URLs em portas locais diferentes. Não se deve tratar diferenças de tempo isoladas como prova conclusiva de porta aberta, nem varrer redes sem autorização.

## 3. Técnicas de mitigação e boas práticas

### 3.1 Allowlist versus denylist

| Política | Exemplo | Problema ou vantagem |
|---|---|---|
| **Denylist** (bloquear alguns destinos) | Rejeitar somente `localhost` | Contornável por representações alternativas de IP, domínios que resolvem para IPs locais, IPv6 e redirecionamentos. |
| **Allowlist** (permitir destinos definidos) | Autorizar exclusivamente o serviço de imagens conhecido | Reduz a superfície: tudo que não estiver explicitamente aprovado é negado. Preferível quando os destinos são conhecidos. |

**Validar apenas o hostname não basta.** `exemplo.test.evil.test` não é `exemplo.test`; URLs podem conter `user@host`, portas inesperadas e codificações. Mais importante, o domínio permitido pode resolver para um IP privado. Requisições que **seguem HTTP 301/302** podem começar em domínio autorizado e terminar em serviço interno. Validar string, resolver DNS e conectar são passos distintos.

**DNS rebinding:** o atacante controla um domínio cujo DNS muda entre o momento da validação e o momento da conexão. O backend pode validar um IP público e depois conectar a outro endereço, privado. Para mitigar: verificar todos os registros A/AAAA, impor política de IP de destino, **fixar a conexão ao IP validado** mantendo Host/SNI/certificado corretos, aplicar o mesmo cuidado a novas tentativas e redirecionamentos e, principalmente, restringir conexões de saída pela rede.

**Defesa em profundidade recomendada:**

1. Evitar aceitar uma URL livre quando é possível usar um ID e montar o destino no servidor.
2. Se necessário, limitar esquemas a `https`, portas e nomes de host exatos, caminhos e tamanho da entrada. Normalizar e interpretar com parser confiável.
3. Rejeitar IPs de loopback, link-local, privados, multicast e reservados **quando os destinos devem ser públicos**, cobrindo IPv4 e IPv6.
4. Resolver DNS e garantir que a conexão ocorra somente com os endereços efetivamente autorizados, evitando *time-of-check/time-of-use*.
5. Desativar redirecionamentos automáticos, ou revalidar cada salto com a mesma política.
6. Isolar o serviço em rede: firewall de saída (**egress**), regras de Kubernetes NetworkPolicy, VPC/security groups e restrição de acesso ao endpoint de metadados da nuvem.
7. Usar o menor privilégio de identidade e de acesso a serviços; timeouts, limite de tamanho de resposta, logging, monitoramento e alertas.
8. Tratar WAF como camada adicional, **não** como substituto da política de destino: ele pode não ver a resolução DNS ou o redirecionamento final.

**Função segura — exemplo Java do trabalho anterior:** em [`I2-07`](https://github.com/fabriciosantana/stsw/pull/256), `src/SsrfGuard.java` valida esquema, host, porta e caminho permitidos, enquanto `SafeStockClient.java` desativa redirecionamentos automáticos. Essa é **uma política didática de URL**, não uma implementação completa de resolução DNS/egress de produção.

**Pseudocódigo de uma política de produção:**

```text
function downloadApprovedAsset(assetId):
    resource = lookupInternalCatalog(assetId)  // cliente não escolhe a URL
    assert resource belongs to approved source
    dnsIPs = resolveAandAAAA(resource.host)
    reject if ANY IP is outside network policy
    connectToValidatedIP(dnsIPs, preserveTLSHostAndCertValidation=true)
    allowRedirects = false
    impose timeout, response size cap and egress firewall
    return strictly validated content
```

## 4. Detecção e exploração com SSRFmap

**Ferramenta:** [SSRFmap](https://github.com/swisskyrepo/SSRFmap) é um projeto aberto que recebe uma **requisição HTTP previamente capturada** e identifica um parâmetro controlado pelo usuário (`-r` para arquivo, `-p` para parâmetro). Possui módulos de exploração/diagnóstico, incluindo `portscan`, `networkscan`, provedores de cloud e serviços como Redis. Uma comparação de respostas e sinais de rede ajuda o pesquisador a identificar vetores, mas pode apresentar falsos positivos.

**Ativo versus passivo:** no uso comum, *ativo* significa enviar payloads para o alvo; *passivo* significa analisar respostas/logs/tráfego sem provocar novas interações. **O SSRFmap é principalmente um fuzzer/explorador ativo**: o repositório organiza módulos por finalidade (`portscan`, `readfiles` etc.), não estabelece uma classificação oficial de cada módulo como “ativo” ou “passivo”. Essa distinção, portanto, se refere à **metodologia de análise**, não a um seletor específico da ferramenta.

**Instalação indicada pelo projeto (quando houver Internet e ambiente de laboratório):**

```bash
git clone https://github.com/swisskyrepo/SSRFmap.git
cd SSRFmap
uv sync --frozen
uv run python ssrfmap.py --help
```

**Amostra de requisição controlada por URL** (`lab/request-ssrfmap.txt`), destinada exclusivamente ao `127.0.0.1:19080` da simulação do item 6:

```http
GET /fetch?url=http%3A%2F%2F127.0.0.1%3A19082%2Fimage%2Fdemo.png HTTP/1.1
Host: 127.0.0.1:19080
Connection: close

```

**Executar com segurança:** mantenha o serviço local ligado (`python lab/ssrf_lab.py --serve`) e, em outra janela, execute a ferramenta **somente no destino autorizado `127.0.0.1`**. O módulo `portscan` lê sua lista em `data/ports`; reduza-a a `19081` e `19082` para não testar portas alheias ao cenário:

```bash
# Execute a partir do diretório do repositório SSRFmap, com o lab em localhost ativo
printf '19081\n19082\n' > data/ports
uv run python ssrfmap.py -r /CAMINHO/ABSOLUTO/DO/PROJETO/lab/request-ssrfmap.txt \
  -p url -m portscan --level 1 --logfile ssrfmap-local.log
```

**Interpretação:** um retorno diferente do controle pode sugerir que o backend fez uma conexão ao destino. A confirmação mais robusta é conferir o código do laboratório, a resposta de `INTERNAL-ONLY` e os registros locais. A ausência de alertas **não prova** que a aplicação é segura; filtros, respostas uniformes e falhas de comunicação podem ocultar a diferença.

**Execução real do SSRFmap (confirmada):** a ferramenta foi clonada de seu repositório oficial e executada em um runner isolado do GitHub Actions, **somente contra `127.0.0.1`**, com a lista `data/ports` restringida a **19081** e **19082**. O comando usou `-r lab/request-ssrfmap.txt -p url -m portscan --level 1` e terminou com código **0**. O SSRFmap classificou as portas **19081** e **19082** como `open`; a demonstração Python confirmou que o endpoint vulnerável acessa o serviço interno fictício e o endpoint protegido retorna **403** ao mesmo destino. A classificação `open` isoladamente é uma **heurística**, não prova de comprometimento. **Evidência verificável:** [log do SSRFmap em GitHub Actions](evidencias/resultado-ssrfmap-github-actions.txt) e [execução #41528865756](https://github.com/leofreitasb/stsw/actions/runs/41528865756). Nenhum alvo de terceiros foi analisado.

**Uso responsável:** apenas em ambientes próprios ou autorizados por escrito, com escopo, taxa e duração definidos; sem coleta de segredos reais, sem ações destrutivas e mantendo logs mínimos, restritos e sem dados pessoais.

## 5. Estudo de caso real — Capital One (2019)

**Evento.** Em 2019, a Capital One divulgou que um atacante havia obtido dados de pedidos de cartão e clientes. Sua comunicação inicial estimou aproximadamente **100 milhões** de pessoas afetadas nos EUA e **6 milhões** no Canadá, principalmente dados de solicitações de cartão; informou também que não houve comprometimento de senhas de login nem números completos de cartões. [Fonte: comunicado Capital One, 29 jul. 2019](https://www.capitalone.com/about/newsroom/capital-one-announces-data-security-incident/).

**Falha de arquitetura.** De acordo com o Departamento de Justiça dos EUA, o acesso ocorreu através de um **web application firewall mal configurado**. Documentos judiciais discutem uso de SSRF e permissões excessivas de credenciais associadas à infraestrutura da nuvem. É importante distinguir: a Capital One confirmou publicamente uma falha de configuração; detalhes técnicos específicos de credenciais/SSRF podem vir de peças judiciais e investigações, não apenas do comunicado empresarial. [DOJ, 29 jul. 2019](https://www.justice.gov/usao-wdwa/pr/seattle-tech-worker-arrested-data-theft-involving-large-financial-services-company); [peça judicial (PDF)](https://www.capitalonesettlement.com/Content/Documents/Complaint.pdf).

**Resposta e consequências.** A instituição declarou ter corrigido a falha de configuração, trabalhado com autoridades e reforçado verificações automatizadas. O Office of the Comptroller of the Currency aplicou, em 2020, uma penalidade civil de **US$ 80 milhões** relacionada a deficiências de gestão de riscos na migração de operações à nuvem e na correção tempestiva dessas deficiências. [Capital One](https://www.capitalone.com/about/newsroom/capital-one-announces-data-security-incident/); [OCC, 6 ago. 2020](https://www.occ.treas.gov/news-issuances/news-releases/2020/nr-occ-2020-101.html).

**Requisitos de segurança que poderiam mitigar o vetor** (propostos a partir das lições, sem alegar que estavam todos ausentes):

- R01: o backend deve acessar somente destinos explicitamente autorizados e conectados a IPs efetivamente validados.
- R02: a rede deve bloquear egress desnecessário para IPs internos, link-local e de metadados da nuvem.
- R03: serviços devem operar com IAM de menor privilégio; acessos a armazenamento não devem herdar permissões amplas.
- R04: revisar e testar periodicamente configurações de WAF, identidade, cloud e regras de rede, com inventário e gestão de mudança.
- R05: registrar e alertar conexões inesperadas, erros de firewall, acesso a metadados e extração anormal.
- R06: proteger dados sensíveis, estabelecer segregação e testar o plano de resposta a incidentes.

**Pós-incidente:** rotacionar credenciais, limitar privilégios, revisar políticas de egress e configurações de nuvem, verificar indicadores de comprometimento, investigar a amplitude de acesso, notificar afetados conforme legislação, corrigir causa-raiz, testar regressões e acompanhar eficácia dos controles.

## 6. Atividade guiada — simulação executável e mitigação

**Ambiente:** Python 3, biblioteca padrão, somente interface de loopback `127.0.0.1`, portas `19080` (frontend), `19081` (serviço administrativo fictício) e `19082` (recurso público fictício). O código se encontra em [`lab/ssrf_lab.py`](lab/ssrf_lab.py). Não há nenhum alvo externo.

**Objetivo:** demonstrar que a rota vulnerável `/fetch?url=...` aceita como destino o serviço interno fictício, e que a rota `/safe-fetch?url=...` rejeita o mesmo destino por allowlist.

```bash
cd prompts/submissions/leonardo-freitas
python3 lab/ssrf_lab.py --demo
```

**Resultado realmente observado neste ambiente:**

| Cenário | Recurso solicitado | Resultado esperado | Resultado observado |
|---|---|---|---|
| 1. Acesso público normal | `127.0.0.1:19082/image/demo.png` via `/fetch` | HTTP 200 | **PASS: 200** e `PUBLIC-ASSET` |
| 2. SSRF local | `127.0.0.1:19081/admin/status` via `/fetch` | A vulnerabilidade permite 200 | **PASS: 200** e `INTERNAL-ONLY` |
| 3. Proteção | mesmo alvo interno via `/safe-fetch` | HTTP 403 | **PASS: 403** e `BLOCKED` |
| 4. Compatibilidade | URL pública permitida via `/safe-fetch` | HTTP 200 | **PASS: 200** e `PUBLIC-ASSET` |

**Evidência textual original:** [`evidencias/resultado-lab-local.txt`](evidencias/resultado-lab-local.txt). São **quatro verificações automatizadas com asserções reais**, não resultados de um exercício externo PortSwigger. O *endpoint vulnerável foi deliberadamente limitado* a portas do próprio laboratório, para não permitir acesso a outros serviços. A política segura usa comparação exata da URL para fins de aula; não substitui validação DNS, bloqueio de rede e controles de conexão de produção.

**Conclusão da demonstração:** a vulnerabilidade decorreu de confiar que um URL escolhido pelo cliente era um destino autorizado para o servidor. A allowlist específica impediu o acesso ao serviço interno e preservou o acesso legítimo ao recurso público.

## 7. Conclusão geral e reflexão

SSRF é perigosa porque transfere ao usuário o poder de selecionar onde o **servidor**, com sua conectividade própria, enviará uma requisição. Suas causas incluem aceitar URLs arbitrárias, confiar em filtros incompletos, seguir redirecionamentos sem inspeção, não fixar DNS/IP e manter permissões ou conectividade excessivas. As defesas mais confiáveis combinam **redução de destinos, allowlist, validação de endereços efetivos, bloqueio de egress e privilégios mínimos**, acompanhados de testes recorrentes.

**O aspecto mais difícil na prática**, na minha avaliação, é garantir que o destino efetivamente alcançado continue autorizado depois de DNS, proxies e redirecionamentos. Uma comparação simples de string parece funcionar em testes básicos, mas o ambiente real inclui IPv6, múltiplos registros DNS, balanceamento, proxy, serviços internos e mudanças ao longo do tempo. O controle precisa existir na aplicação **e** na rede.

## Fontes consultadas

1. [Enunciado P2-12 — repositório STSW](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/12-ssrf.md).
2. [OWASP Top Ten 2021 — A10 SSRF (português)](https://top10.owasp.org/2021/pt-BR/A10_2021-Server-Side_Request_Forgery_%28SSRF%29/).
3. [OWASP — SSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html).
4. [PortSwigger — Server-Side Request Forgery](https://portswigger.net/web-security/ssrf).
5. [SSRFmap — projeto e instruções de uso](https://github.com/swisskyrepo/SSRFmap).
6. [Capital One — anúncio do incidente, julho de 2019](https://www.capitalone.com/about/newsroom/capital-one-announces-data-security-incident/).
7. [Department of Justice — comunicado de 29/07/2019](https://www.justice.gov/usao-wdwa/pr/seattle-tech-worker-arrested-data-theft-involving-large-financial-services-company).
8. [OCC — penalidade regulatória, agosto de 2020](https://www.occ.treas.gov/news-issuances/news-releases/2020/nr-occ-2020-101.html).

---

**Nota de integridade:** a evidência `resultado-lab-local.txt` registra a execução verdadeira do laboratório Python local. Qualquer instalação/execução do SSRFmap deve ter seu próprio registro antes de ser reportada como concluída. Este arquivo documenta respostas ao roteiro **P2-12**, não afirma ter resolvido os laboratórios externos de **I2-07**.