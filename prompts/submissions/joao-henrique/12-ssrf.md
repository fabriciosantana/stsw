# Prompt 12: OWASP Top Ten: Server-Side Request Forgery (A10:2021)

Referências: [OWASP A10:2021](https://owasp.org/Top10/A10_2021-Server-Side_Request_Forgery_%28SSRF%29/), [OWASP SSRF](https://owasp.org/www-community/attacks/Server_Side_Request_Forgery), [PortSwigger SSRF](https://portswigger.net/web-security/ssrf), [SSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html)

> Nota: ferramentas como o SSRFmap só devem ser usadas em laboratório próprio. Os resultados do SSRFmap descritos abaixo são o comportamento esperado da ferramenta, não saída capturada por mim.

## 1. Introdução

SSRF acontece quando a aplicação busca um recurso remoto a partir de uma URL fornecida (ou influenciada) pelo usuário sem validá-la. O atacante faz o **servidor** enviar a requisição, escolhendo o destino.

- **Contexto:** funcionalidades que buscam URLs: pré-visualização de links, importação de imagem por URL, webhooks, conversores de PDF, integrações.
- **Por que é crítica:** a requisição sai de dentro da rede da empresa, por isso passa por firewalls e acessa serviços que a internet não alcança (painéis admin, bancos, metadados de nuvem).
- **"Requisição forjada do lado do servidor":** o atacante não ataca o navegador de uma vítima (como no XSS/CSRF); ele usa o servidor como intermediário confiável. O cliente só informa a URL.
- **Alvos comuns:** `localhost`/`127.0.0.1`, serviços internos (`http://10.0.0.5:8080/admin`), endpoint de metadados da nuvem (`169.254.169.254`), arquivos locais (`file://`), portas internas.
- **Por que entrou no Top 10 em 2021:** veio da pesquisa com a comunidade (incidência baixa nos dados, mas alto impacto e crescimento por causa da nuvem). Ficou em A10.
- **Impactos:** vazamento de dados internos e credenciais de nuvem, acesso a funções administrativas, mapeamento da rede interna, execução remota em alguns casos.
- **Microserviços e nuvem:** há muitos serviços internos que confiam uns nos outros (sem autenticação "porque é rede interna") e as instâncias têm um endpoint de metadados que entrega credenciais temporárias. Um SSRF vira passo direto para escalar acesso.

## 2. Exemplos práticos de ataque

- **Busca de imagem por URL:** o atacante informa `http://localhost/admin` ou `http://192.168.0.10:8080/` em vez de uma imagem; o servidor busca e devolve (ou usa) o conteúdo interno.
- **AWS:** `http://169.254.169.254/latest/meta-data/iam/security-credentials/<role>` devolve chaves temporárias da função da instância (AccessKeyId, SecretAccessKey, Token), que o atacante usa para acessar S3 etc.
- **Código sem validação:**

```java
@GetMapping("/preview")
public String preview(@RequestParam String url) throws IOException {
    HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
    return new String(c.getInputStream().readAllBytes());   // devolve o corpo
}
```

- **Exploração:** `GET /preview?url=http://localhost:8080/admin/usuarios` faz o servidor acessar o serviço interno e devolver a resposta ao atacante.
- **Simples x cego:** no SSRF simples a resposta da requisição forjada volta para o atacante. No **cego** ela não volta; o atacante só sabe do efeito por canais indiretos (tempo de resposta, códigos de erro, ou um servidor externo dele que recebe a chamada: *out-of-band*).
- **Port scanning:** o atacante varia a porta (`http://10.0.0.5:22`, `:3306`, `:6379`...) e observa a diferença de resposta/tempo (conexão recusada x aberta x timeout) para descobrir quais serviços internos existem.

## 3. Mitigação e boas práticas

- **Deny list x allow list:** a deny list tenta bloquear o que é perigoso (`localhost`, `127.0.0.1`), mas é contornável (`127.1`, `0x7f000001`, `[::1]`, domínio que resolve para IP interno, redirecionamento). A **allow list** só aceita o que foi previamente aprovado (domínios/IPs/portas/esquemas permitidos). A allow list é mais eficaz porque o que não foi previsto é negado por padrão.
- **Só validar o hostname não basta:** o nome pode resolver para IP interno; o atacante controla o DNS; há redirecionamentos (um site externo permitido que redireciona para o interno) e formatos alternativos de IP.
- **DNS rebinding:** o domínio do atacante responde primeiro com um IP público (passa na validação) e, na segunda resolução (no momento da requisição), com um IP interno. Defesa: resolver o DNS uma vez, validar o IP resolvido e conectar nesse IP, e não seguir redirecionamentos automaticamente.
- **Defesa em profundidade:** segmentação de rede e firewall de saída (egress filtering), serviços internos com autenticação mesmo na rede privada, IMDSv2 na AWS (exige token e bloqueia boa parte do SSRF), proxy de saída dedicado, desabilitar esquemas desnecessários (`file://`, `gopher://`), não devolver a resposta crua ao cliente, WAF como camada extra.
- **Função segura (Java):**

```java
private static final Set<String> HOSTS_PERMITIDOS = Set.of("cdn.exemplo.com", "imagens.parceiro.com");

public static URI validar(String entrada) throws Exception {
    URI uri = new URI(entrada);
    if (!"https".equals(uri.getScheme()))            throw new SecurityException("só https");
    if (uri.getPort() != -1 && uri.getPort() != 443) throw new SecurityException("porta não permitida");
    String host = uri.getHost();
    if (host == null || !HOSTS_PERMITIDOS.contains(host.toLowerCase()))
        throw new SecurityException("host fora da allow list");
    for (InetAddress ip : InetAddress.getAllByName(host)) {   // bloqueia IPs internos
        if (ip.isAnyLocalAddress() || ip.isLoopbackAddress() || ip.isLinkLocalAddress()
            || ip.isSiteLocalAddress() || ip.isMulticastAddress())
            throw new SecurityException("IP interno");
    }
    return uri;   // na requisição: desativar followRedirects
}
```

- **Restringir acesso a internos:** firewall/Security Groups/NetworkPolicy (Kubernetes) com regras de saída "negar tudo e liberar só o necessário"; a aplicação que busca URLs roda numa rede isolada (DMZ) sem rota para os serviços internos.

## 4. SSRFmap

- **O que é:** ferramenta em Python que, a partir de uma requisição HTTP capturada (ex.: salva do Burp) com o parâmetro vulnerável marcado, automatiza a exploração de SSRF por meio de módulos.
- **Serviços que mapeia/explora:** acesso a arquivos locais (`readfiles`), metadados de nuvem (AWS, GCP, Azure), serviços internos como Redis, MySQL, Memcached, FastCGI, Docker, e varredura de portas (`portscan`), em alguns casos via `gopher://`.
- **Módulos ativos x passivos:** no SSRFmap, a maior parte dos módulos é ativa (envia payloads que interagem com o serviço-alvo, ex.: gerar comando gopher para o Redis). Os de reconhecimento (como `portscan`, `networkscan`, `readfiles`) apenas observam respostas; por isso é bom usar primeiro os de reconhecimento e só depois os ativos.
- **Uso em laboratório:**

```bash
git clone https://github.com/swisskyrepo/SSRFmap && cd SSRFmap
pip install -r requirements.txt
python3 ssrfmap.py -r requisicao.txt -p url -m portscan
python3 ssrfmap.py -r requisicao.txt -p url -m readfiles
```

- **Como interpretar:** portas/serviços marcados como acessíveis mostram que o servidor alcança aquele alvo interno; conteúdo de arquivos ou de metadados no resultado indica exfiltração possível. Cada achado deve virar um item de correção (allow list, firewall de saída).
- **Boas práticas:** usar só em ambiente de teste com escopo definido, registrar o que foi executado, começar por módulos de leitura, não usar credenciais reais e restaurar o ambiente no fim.
- **Ética e legalidade:** só testar com autorização por escrito (Lei 12.737/2012 e LGPD no Brasil); respeitar o escopo; não acessar nem divulgar dados reais encontrados; reportar de forma responsável.

## 5. Estudo de caso: Capital One (2019)

- **Falhas:** um WAF mal configurado (ModSecurity) na AWS permitiu SSRF; o atacante fez o servidor consultar o endpoint de metadados (169.254.169.254) e obteve credenciais temporárias de uma função IAM com permissões excessivas (acesso amplo a buckets S3). IMDSv1 não exigia autenticação.
- **Sistemas e dados comprometidos:** cerca de 100 milhões de registros de clientes nos EUA e 6 milhões no Canadá (dados de solicitações de cartão, número de seguro social, contas bancárias de uma parte deles).
- **Resposta e lições:** a empresa detectou a partir de um alerta externo, comunicou o FBI e os clientes, e recebeu multa de US$ 80 milhões do regulador (OCC) mais acordos judiciais (o total chegou a centenas de milhões de dólares). Lições: princípio do menor privilégio em funções IAM, IMDSv2, configuração e monitoramento do WAF, criptografia/segmentação de dados.
- **Requisitos que poderiam ter evitado:**
  1. Validar URLs por allow list e bloquear IPs de link-local/privados.
  2. Exigir IMDSv2 (ou desabilitar metadados quando não necessário).
  3. Funções IAM com permissões mínimas, por serviço.
  4. Filtragem de tráfego de saída (egress) dos servidores.
  5. Monitorar chamadas anômalas à API da nuvem (CloudTrail) e acessos massivos ao S3.
- **Medidas pós-incidente:** revisar todos os pontos que fazem requisições a URLs, rotacionar credenciais, auditar logs para saber o alcance, aplicar as correções acima, fazer testes de intrusão focados em SSRF e treinar a equipe.

## 6. Atividade guiada: simulação e mitigação

Cenário (laboratório local, nenhum alvo externo):

1. **Aplicação vulnerável:** o endpoint `/preview?url=` do item 2.
2. **Serviço interno fictício:** um segundo servidor em `http://localhost:9000/segredo` que só aceita conexões locais e responde `{"chave":"ABC123"}`.
3. **Ataque:** `curl "http://localhost:8080/preview?url=http://localhost:9000/segredo"` devolve o segredo, ou seja, o servidor foi forçado a acessar o serviço interno.
4. **Automação:** capturar a requisição e rodar `python3 ssrfmap.py -r req.txt -p url -m portscan` para listar as portas internas abertas (9000 aparece como aberta).
5. **Defesa:** trocar o handler por `validar(url)` (allow list do item 3), com redirecionamentos desativados.
6. **Resultado após a defesa:** a mesma requisição passa a retornar erro 400 ("host fora da allow list"), e o scan de portas não consegue mais alcançar nada. Somente `https://cdn.exemplo.com/...` é aceita.

Documentação: capturas de tela (antes/depois), código vulnerável e corrigido (acima) e as conclusões: a allow list bloqueou os dois ataques e o firewall de saída seria a segunda camada.

## 7. Conclusão

- **O que é e por que é perigosa:** o atacante faz o servidor realizar requisições em seu nome, usando a confiança da rede interna e podendo chegar a credenciais de nuvem e serviços internos.
- **Causas:** URLs do usuário usadas sem validação, serviços internos sem autenticação, permissões de nuvem excessivas, ausência de filtro de saída, uso de deny list.
- **Mitigações:** allow list, validar o IP resolvido, não seguir redirecionamentos, bloquear esquemas, segmentação e firewall de saída, IMDSv2, privilégio mínimo, autenticação entre serviços.
- **Aspecto mais difícil na prática:** cobrir todas as entradas que causam requisições (webhooks, importadores, bibliotecas de terceiros) e tratar os desvios de validação (DNS rebinding, redirecionamento, codificações de IP). Por isso a defesa de rede é indispensável além da validação no código.
