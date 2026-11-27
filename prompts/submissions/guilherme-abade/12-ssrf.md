# Prompt 12: Server-Side Request Forgery / SSRF (A10:2021)

**Aluno:** Guilherme Abade
**Referências:** [OWASP A10:2021](https://owasp.org/Top10/A10_2021-Server-Side_Request_Forgery_%28SSRF%29/) · [OWASP SSRF](https://owasp.org/www-community/attacks/Server_Side_Request_Forgery) · [PortSwigger SSRF](https://portswigger.net/web-security/ssrf) · [SSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Server_Side_Request_Forgery_Prevention_Cheat_Sheet.html)

Os códigos e comandos, inclusive os relativos ao SSRFmap, constituem roteiros para um laboratório local e controlado. Ferramentas ofensivas só podem ser utilizadas em sistemas próprios ou mediante autorização formal.

## 1. Introdução

A Server-Side Request Forgery ocorre quando uma aplicação realiza uma requisição de rede para uma URL controlada, total ou parcialmente, pelo usuário, sem validar o destino. Por meio dela, o atacante induz o servidor a acessar recursos que ele próprio não alcançaria, como serviços internos, o endereço de *loopback*, as APIs de metadados de nuvem e outros hosts da rede privada.

A expressão "requisição forjada do lado do servidor" indica que a requisição é disparada pelo servidor, valendo-se de sua posição privilegiada na rede. Nisso reside a diferença em relação a ataques do lado do cliente, como o CSRF ou o XSS, cujo abuso acontece no navegador da vítima. Na SSRF, o servidor atua como procurador involuntário do atacante e, por isso, alcança a rede interna, normalmente protegida do exterior por um firewall.

Os alvos mais comuns são o endereço local e o de *loopback*, por meio dos quais se acessam painéis de administração e serviços que confiam em requisições da própria máquina; a rede interna, sujeita a varredura de portas e de hosts; e, sobretudo, os metadados de nuvem, acessíveis em `http://169.254.169.254/` na AWS com IMDSv1 e em `metadata.google.internal` no GCP, que podem devolver credenciais temporárias. Esquemas alternativos como `file://`, `gopher://` e `dict://` ampliam a superfície.

A vulnerabilidade ingressou no Top 10 de 2021 porque, apesar da incidência relativamente baixa nos dados, apresentou a maior taxa de exploração ponderada da pesquisa e foi apontada pela comunidade de segurança, em votação, como risco emergente, impulsionada pela expansão da nuvem e dos microsserviços. Seus impactos incluem a leitura de dados internos, o roubo de credenciais de nuvem e o consequente controle da conta, a varredura de portas internas, o acesso a bancos e filas, a execução remota de código em cadeia e a negação de serviço.

As arquiteturas baseadas em microsserviços e nuvem elevam o risco por reunirem muitos serviços internos que confiam uns nos outros, por incorporarem funcionalidades que buscam URLs, como webhooks, importação por URL, pré-visualização de link e renderização de PDF, e por disporem do endpoint de metadados, que transforma uma SSRF simples em comprometimento de credenciais.

## 2. Exemplos práticos de ataques

Um recurso que permite informar a URL de uma foto de perfil, fazendo o servidor baixar a imagem, ilustra o abuso: o atacante substitui a URL por um endereço interno, como `http://localhost:8080/admin`, o endpoint de metadados ou um serviço de banco interno, e o servidor acessa o recurso em seu nome. O ataque ao endpoint de metadados da AWS, na versão IMDSv1, segue duas etapas. A primeira consulta lista a *role* associada à instância; a segunda devolve as credenciais temporárias.

```
http://169.254.169.254/latest/meta-data/iam/security-credentials/
→ nome da role, por exemplo app-ec2-role

http://169.254.169.254/latest/meta-data/iam/security-credentials/app-ec2-role
→ AccessKeyId, SecretAccessKey e Token temporários
```

De posse dessas credenciais, o atacante assume as permissões da instância na conta de nuvem. A versão IMDSv2 mitiga o problema ao exigir um token enviado por `PUT`, o que a torna inacessível a uma SSRF simples. O código Java a seguir, sem validação do destino, é vulnerável.

```java
@GetMapping("/buscar-imagem")
public ResponseEntity<byte[]> buscarImagem(@RequestParam String url) throws IOException {
    try (InputStream in = new URL(url).openStream()) {   // destino não validado
        return ResponseEntity.ok(in.readAllBytes());
    }
}
```

Uma requisição como `GET /buscar-imagem?url=http://169.254.169.254/latest/meta-data/` faz o servidor devolver dados internos na resposta. A distinção entre a SSRF simples e a cega reside no retorno da resposta: na simples, o conteúdo da requisição interna volta ao atacante; na cega, a resposta não retorna, mas o comportamento, como o tempo de resposta, o código de status ou o erro, permite inferir o resultado, eventualmente por um canal fora da banda, com um domínio que registra o acesso vindo do servidor. A varredura de portas internas vale-se dessa inferência: ao variar a porta na URL e observar o tempo ou o erro, o atacante mapeia as portas abertas.

```
url=http://10.0.0.12:22   → conexão recusada rápida, porta fechada
url=http://10.0.0.12:6379 → resposta ou erro distinto, porta aberta
```

## 3. Mitigação e boas práticas

A distinção entre lista de bloqueio e lista de permissão é central. A lista de bloqueio, que proíbe endereços como o de *loopback* e o de metadados, mostra-se frágil, pois admite contorno por representações alternativas de endereço IP, em formato decimal, octal ou hexadecimal, por IPv6, por domínios que resolvem para endereços internos, por redirecionamentos e por *DNS rebinding*. A lista de permissão, que autoriza apenas destinos explicitamente previstos, revela-se muito mais eficaz, por negar tudo por padrão e aceitar somente o conjunto conhecido de hosts.

Validar apenas o nome do host não basta, uma vez que esse nome pode passar na verificação e, no instante da conexão, resolver para um endereço interno sob controle do atacante. Por isso é necessário resolver o DNS, validar o endereço resultante, rejeitando os privados, de *loopback*, *link-local* e *multicast*, e empregar esse mesmo endereço na conexão, de modo a não abrir espaço entre a checagem e o uso. O *DNS rebinding* explora justamente essa brecha: o domínio responde um endereço público na validação e, segundos depois, graças a um TTL baixo, um endereço interno na conexão real. A defesa consiste em resolver uma única vez e fixar o endereço validado, além de bloquear respostas que apontem para faixas internas.

A defesa em profundidade acrescenta a segmentação de rede, de forma que a aplicação não tenha rota para serviços sensíveis, o firewall de saída, que bloqueia o acesso ao endereço de metadados e às faixas internas, o uso de IMDSv2, a desativação de redirecionamentos no cliente HTTP e de esquemas perigosos, e a autenticação entre serviços internos, que evita confiar apenas na origem de rede. A função a seguir valida uma URL externa antes de realizar a requisição.

```java
private static final Set<String> HOSTS_PERMITIDOS = Set.of(
    "api.parceiro.com", "cdn.exemplo.com");

public URL validarUrlExterna(String entrada) throws Exception {
    URI uri = new URI(entrada);
    String esquema = uri.getScheme();
    if (!"https".equalsIgnoreCase(esquema))                 // só HTTPS
        throw new SecurityException("esquema não permitido");

    String host = uri.getHost();
    if (host == null || !HOSTS_PERMITIDOS.contains(host))   // lista de permissão
        throw new SecurityException("host não autorizado");

    for (InetAddress ip : InetAddress.getAllByName(host)) { // valida todos os endereços
        if (ip.isLoopbackAddress() || ip.isAnyLocalAddress()
                || ip.isLinkLocalAddress() || ip.isSiteLocalAddress()
                || ip.isMulticastAddress())
            throw new SecurityException("destino interno bloqueado");
    }
    return uri.toURL();
}
```

No nível de rede, convém definir regras de saída que neguem o tráfego da aplicação para as faixas `10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `127.0.0.0/8` e `169.254.0.0/16`, liberando apenas os destinos da lista de permissão, bem como isolar o contêiner por políticas de rede e alocá-lo em uma sub-rede sem rota para os sistemas sensíveis.

## 4. Detecção e exploração com SSRFmap

O SSRFmap automatiza a exploração de uma SSRF já identificada. A ferramenta recebe uma requisição HTTP, por exemplo salva do Burp, e o parâmetro vulnerável, e aplica módulos que tentam acessar serviços internos a partir dele. Entre os serviços mapeáveis estão os metadados de nuvem, a varredura de portas, o Redis, o MySQL, o Docker, o SMTP e a leitura de arquivos. Os módulos mais ativos interagem e buscam provocar um efeito, como gravar no Redis, ao passo que os mais informativos apenas coletam dados, como os metadados e a varredura, o que exige cautela redobrada com os primeiros.

```bash
git clone https://github.com/swisskyrepo/SSRFmap && cd SSRFmap
pip install -r requirements.txt
# req.txt é a requisição capturada no Burp; url é o parâmetro vulnerável
python3 ssrfmap.py -r req.txt -p url -m readfiles,portscan,aws
```

A saída indica quais módulos obtiveram resposta, confirmando o alcance da SSRF: o módulo de varredura lista as portas internas que responderam, o módulo de nuvem tenta ler o endereço de metadados e o de leitura de arquivos tenta acessar caminhos locais. Quanto à ética, a ferramenta deve ser usada somente em ambiente próprio ou mediante autorização formal com escopo definido, jamais apontada ao endereço de metadados de uma conta de terceiros, com registro das ações e atenção aos módulos que gravam dados, observada a Lei 12.737/2012.

## 5. Estudo de caso: Capital One (2019)

No incidente da Capital One, uma SSRF em um WAF mal configurado, em uma aplicação hospedada na AWS, permitiu alcançar o endpoint de metadados na versão IMDSv1 e obter credenciais temporárias da *role* da instância. Contribuíram para o ataque as permissões excessivas atribuídas a essa *role*, o uso de IMDSv1 sem exigência de token, a falta de segmentação e de bloqueio de saída ao endereço de metadados e um monitoramento que não detectou o acesso anômalo ao armazenamento de objetos. Cerca de 100 milhões de clientes nos Estados Unidos e 6 milhões no Canadá tiveram dados comprometidos, incluindo informações de pedidos de cartão e, em parte, números de documento e de conta. A organização conduziu a investigação, notificou os afetados e recebeu multa de 80 milhões de dólares, além de acordos; entre as lições estão a migração para IMDSv2, a aplicação do menor privilégio às *roles*, o bloqueio de saída ao endereço de metadados e às faixas internas, e o monitoramento de uso incomum de credenciais.

Os requisitos de segurança que teriam evitado o ataque incluem a validação de URL por lista de permissão no serviço que realizava as requisições, a obrigatoriedade de IMDSv2, a *role* mínima, o bloqueio de saída e os alertas sobre volume anômalo de acesso. Como medidas pós-incidente, recomendam-se a rotação imediata de credenciais, a revisão de todas as *roles*, a varredura por SSRF em todos os serviços que buscam URLs, a caça a ameaças e exercícios de resposta a incidentes.

## 6. Atividade guiada: exploração e mitigação

No cenário de laboratório, a aplicação do item 2, com o endpoint `/buscar-imagem`, executa em `localhost:8080`, enquanto um serviço interno fictício responde em `localhost:9000/admin` e não deveria ser público. A reprodução da falha ocorre por uma requisição `GET /buscar-imagem?url=http://localhost:9000/admin`, cuja resposta traz o conteúdo do painel interno e comprova a vulnerabilidade. A automação, opcional, consiste em capturar a requisição no Burp e executar o módulo de varredura do SSRFmap para enumerar as portas internas. A mitigação aplica a função `validarUrlExterna` antes de abrir a conexão.

```java
@GetMapping("/buscar-imagem")
public ResponseEntity<byte[]> buscarImagem(@RequestParam String url) throws Exception {
    URL destino = validarUrlExterna(url);   // lista de permissão e checagem de endereço
    try (InputStream in = destino.openStream()) {
        return ResponseEntity.ok(in.readAllBytes());
    }
}
```

Após a correção, a requisição a `http://localhost:9000/admin` retorna erro de host não autorizado ou destino interno bloqueado, com código `400`, e somente os hosts da lista de permissão funcionam. O relatório da atividade deve registrar as capturas da requisição e da resposta antes e depois, o código em ambas as versões e a conclusão sobre por que a lista de permissão resolveu o problema, enquanto uma lista de bloqueio poderia ser contornada.

## 7. Conclusão

A SSRF consiste em fazer o servidor emitir requisições para destinos escolhidos pelo atacante, abusando de sua posição de confiança na rede interna e na nuvem. Sua periculosidade decorre de atravessar o firewall por dentro, alcançar serviços internos e metadados de nuvem e escalar de uma leitura simples até o roubo de credenciais e o comprometimento total da conta. A causa raiz está em confiar na URL fornecida pelo usuário, e a mitigação combina a lista de permissão de destinos, a resolução e validação do endereço e a fixação do endereço validado, somadas à defesa em profundidade por segmentação, firewall de saída, IMDSv2, bloqueio de esquemas e redirecionamentos e autenticação entre serviços.

O aspecto mais difícil na prática é lidar com as formas de contornar a validação, como as representações alternativas de endereço, os redirecionamentos e, em especial, o *DNS rebinding*, que exigem validar o endereço no instante exato da conexão e fixá-lo, em vez de verificar apenas o nome do host. Em arquiteturas extensas, garantir que todos os pontos que fazem requisições de saída utilizem a mesma função segura constitui, por si só, um desafio de governança.
