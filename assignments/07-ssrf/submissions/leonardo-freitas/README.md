# I2-07 — Server-Side Request Forgery (SSRF)

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software — IDP  
**Repositório da disciplina:** [`fabriciosantana/stsw`](https://github.com/fabriciosantana/stsw)  
**Enunciado:** [`assignments/07-ssrf/README.md`](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/07-ssrf/README.md)  
**Ambientes:** [PortSwigger Web Security Academy](https://portswigger.net/web-security/ssrf) e [OWASP Juice Shop](https://owasp.org/www-project-juice-shop/)

> **Estado desta entrega:** o roteiro técnico e os exemplos de prevenção foram produzidos; o código defensivo passou nos testes locais. **Os laboratórios online não foram executados ou comprovados por esta entrega.** Screenshots de laboratórios resolvidos deverão ser adicionados ao diretório `evidencias/` após execução. Este texto diferencia *procedimento documentado* de *resultado observado*.

## 1. Objetivo e escopo

O objetivo da atividade é compreender SSRF por meio de laboratórios propositalmente vulneráveis, identificar o fluxo de uma requisição controlada pelo usuário e propor medidas corretivas. O [enunciado oficial](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/07-ssrf/README.md) **especifica quatro labs PortSwigger** e inclui o título "Resolver desafios do Juice Shop", sem listar um desafio nominal nessa seção. Para contemplar também essa parte, foi incluído o desafio **SSRF** da OWASP Juice Shop, identificado em sua documentação oficial. Portanto, estão descritos **4 labs explicitamente exigidos + 1 desafio Juice Shop de referência**.

### Matriz de desafios e evidências

| ID | Ambiente | Desafio | Técnica central | Evidência de conclusão | Estado |
|---|---|---|---|---|---|
| PS-01 | PortSwigger | Basic SSRF against the local server | Acesso ao próprio servidor | `evidencias/ps-01-solved.png` | **PENDENTE** |
| PS-02 | PortSwigger | Basic SSRF against another back-end system | Descoberta de servidor interno | `evidencias/ps-02-solved.png` | **PENDENTE** |
| PS-03 | PortSwigger | SSRF with blacklist-based input filter | Bypass de blacklist e de filtro de caminho | `evidencias/ps-03-solved.png` | **PENDENTE** |
| PS-04 | PortSwigger | SSRF with filter bypass via open redirection | Redirecionamento aberto como pivô | `evidencias/ps-04-solved.png` | **PENDENTE** |
| JS-01 | Juice Shop | SSRF — Request a hidden resource on server through server | Acesso a recurso interno via servidor | `evidencias/js-01-solved.png` | **PENDENTE** |

**Ambiente de execução permitido:** somente instâncias de laboratório de propriedade do aluno, OWASP Juice Shop em instalação deliberadamente vulnerável, e instâncias oficiais da PortSwigger. Os valores internos abaixo são exemplos **dos próprios laboratórios**, não orientações para testar sistemas reais sem autorização.

## 2. Fundamentação — o que é SSRF?

**SSRF (Server-Side Request Forgery)** acontece quando uma aplicação no servidor executa uma requisição a um destino escolhido, total ou parcialmente, por um usuário e **não controla suficientemente esse destino**. Por exemplo, uma loja oferece a ação "verificar estoque" e pede ao servidor para buscar `stockApi`, recebido na requisição. Se o valor puder apontar para `localhost`, IPs internos ou endpoints de metadados, o atacante transforma o servidor em um intermediário para acessar sistemas que o navegador externo não alcança.

```text
Usuário → POST /product/stock (stockApi=<URL controlada>)
        → aplicação vulnerável faz GET <URL controlada>
        → serviço interno responde à aplicação
        → resposta / efeito aparece no laboratório
```

**Riscos:** acesso indevido à intranet, consultas a serviços internos e metadados de nuvem, descoberta de portas, vazamento de informações e, nos cenários específicos deste exercício, ações na interface administrativa da aplicação.

Não confundir SSRF com **CSRF**: SSRF induz uma **requisição do servidor**; CSRF induz um **navegador autenticado** a executar uma ação não desejada.

## 3. Laboratórios oficiais PortSwigger

### PS-01 — Basic SSRF against the local server

**Fonte oficial:** https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-localhost

- **Objetivo:** acessar uma interface administrativa interna e remover o usuário `carlos`, conforme o desafio.
- **Ponto de entrada:** parâmetro `stockApi` na ação *Check stock*.
- **Causa-raiz:** o servidor aceita uma URL externa sem bloquear o próprio host/loopback e pode acessar uma rota inacessível ao cliente.

**Procedimento de resolução na instância oficial:**

1. Iniciar uma instância do laboratório na PortSwigger. Tentar abrir `/admin` no navegador e observar a restrição de acesso direto.
2. Abrir um produto, clicar em **Check stock** e enviar a requisição interceptada pelo Burp Proxy para o **Repeater**.
3. Substituir o valor de `stockApi` por `http://localhost/admin` e verificar se o conteúdo administrativo aparece na resposta.
4. Alterar para `http://localhost/admin/delete?username=carlos` e enviar; conferir se o laboratório marca **Solved**.
5. Capturar o banner **Solved** com identificação da instância para `evidencias/ps-01-solved.png`.

**Por que funciona:** `localhost` representa o servidor no contexto onde o back-end executa, não o computador do navegador. A requisição sai do servidor, onde a interface administrativa confia incorretamente na origem local.

**Correção sugerida:** não aceitar destino arbitrário; limitar a checagem de estoque a um serviço previamente configurado; negar loopback, redes privadas e link-local; usar autenticação na área administrativa mesmo para chamadas locais.

**Resultado comprovado nesta entrega:** **não executado**. Os passos acima descrevem o resultado esperado informado pela PortSwigger.

---

### PS-02 — Basic SSRF against another back-end system

**Fonte oficial:** https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-backend-system

- **Objetivo:** identificar um servidor administrativo na rede de laboratório `192.168.0.0/24`, porta `8080`, e remover `carlos`.
- **Ponto de entrada:** `stockApi`.
- **Causa-raiz:** a aplicação pode consultar livremente endereços privados na rede a que está conectada.

**Procedimento de resolução na instância oficial:**

1. Abrir um produto e interceptar **Check stock** com Burp Proxy.
2. Enviar a requisição ao **Burp Intruder**. Ajustar `stockApi` para `http://192.168.0.1:8080/admin`.
3. Marcar somente o último octeto como posição de payload: `192.168.0.§1§`. Em **Payloads**, escolher números de `1` a `255`, com passo `1`.
4. Iniciar o teste **somente no laboratório**. Examinar códigos de status e comprimentos de resposta; encontrar a resposta distinta com status `200`, conforme a solução oficial.
5. Usar o IP encontrado no Repeater e acessar `/admin/delete?username=carlos`; confirmar o banner **Solved** e salvar `evidencias/ps-02-solved.png`.

**Por que funciona:** o navegador do usuário não enxerga o segmento privado, mas o servidor da loja enxerga. O back-end age como ponte para a rede interna.

**Correção sugerida:** restringir a comunicação de saída (egress) por serviço e porta; bloquear intervalos privados, loopback e link-local; não confiar em controles de interface para restringir os destinos.

**Resultado comprovado nesta entrega:** **não executado**; nenhum IP interno do laboratório foi observado ou inventado.

---

### PS-03 — SSRF with blacklist-based input filter

**Fonte oficial:** https://portswigger.net/web-security/ssrf/lab-ssrf-with-blacklist-filter

- **Objetivo:** contornar duas regras de bloqueio insuficientes e acessar a área de administração local.
- **Ponto de entrada:** `stockApi`.
- **Causa-raiz:** uma *blacklist* por string ignora interpretações equivalentes do endereço e decodificação em diferentes camadas.

**Procedimento de resolução na instância oficial:**

1. Interceptar **Check stock** e copiar para Burp Repeater.
2. Testar `http://127.0.0.1/` e observar o bloqueio.
3. Testar `http://127.1/`, representação alternativa de loopback que contorna a primeira regra do laboratório.
4. Testar `http://127.1/admin` e observar que `/admin` é bloqueado pela segunda regra.
5. Substituir a letra `a` por codificação dupla na rota: `http://127.1/%2561dmin`. Como o parâmetro trafega em um formulário URL-encoded, assegurar que o nível de encoding esteja correto no Burp.
6. Aplicar a mesma ideia à rota `/admin/delete?username=carlos`, confirmar **Solved** e salvar `evidencias/ps-03-solved.png`.

**Por que funciona:** representações de IP diferentes e múltiplas etapas de decodificação impedem que uma blacklist textual cubra todas as variantes. O destino efetivo é interpretado de modo diferente do texto validado.

**Correção sugerida:** política positiva de destinos, normalização com parser confiável, rejeição de IPs especiais após resolução, e validação do **destino efetivo** após cada transformação; não usar blacklist textual isolada.

**Resultado comprovado nesta entrega:** **não executado**.

---

### PS-04 — SSRF with filter bypass via open redirection vulnerability

**Fonte oficial:** https://portswigger.net/web-security/ssrf/lab-ssrf-filter-bypass-via-open-redirection

- **Objetivo:** explorar um redirecionamento aberto para alcançar a área administrativa em `192.168.0.12:8080`.
- **Ponto de entrada:** `stockApi`, com pivô no endpoint `nextProduct`.
- **Causa-raiz:** o cliente de HTTP do back-end segue redirecionamentos sem validar novamente o destino da nova URL.

**Procedimento de resolução na instância oficial:**

1. Interceptar **Check stock** e enviar ao Repeater.
2. Confirmar que o parâmetro `stockApi` recusa URLs diretamente apontadas para outros hosts.
3. Observar a ação **next product**; seu parâmetro `path` controla o cabeçalho `Location` e permite redirecionamento externo.
4. Usar, no parâmetro `stockApi`, uma URL da própria loja cujo caminho seja `/product/nextProduct?path=http://192.168.0.12:8080/admin`. Dependendo da formatação do corpo, aplicar o encoding necessário para preservar o `?` e outros caracteres.
5. Confirmar a página administrativa no resultado do redirecionamento.
6. Trocar a URL final por `http://192.168.0.12:8080/admin/delete?username=carlos`; confirmar **Solved** e salvar `evidencias/ps-04-solved.png`.

**Por que funciona:** a primeira URL passa na validação porque pertence ao domínio permitido. Porém, ao receber HTTP 3xx, o cliente segue para a intranet sem revalidar o novo destino.

**Correção sugerida:** desabilitar redirecionamentos automáticos em clientes que consomem URLs controladas; se redirecionamentos forem indispensáveis, revalidar cada salto e aplicar regras de egress; corrigir o redirecionamento aberto com allowlist de destinos.

**Resultado comprovado nesta entrega:** **não executado**.

## 4. OWASP Juice Shop (menção complementar do enunciado)

### JS-01 — SSRF: Request a hidden resource on server through server

**Documentação oficial:** https://help.owasp-juice.shop/part2/broken-access-control.html  
**Catálogo de desafios:** https://juice-shop.github.io/tab_challenges.html

O enunciado oficial da atividade 07 cita o Juice Shop sem enumerar qual desafio resolver. O catálogo da OWASP inclui o desafio **SSRF**, no grupo *Broken Access Control*, de dificuldade elevada (6 estrelas). A descrição é: solicitar pelo servidor um recurso oculto do próprio servidor.

**Roteiro de investigação (não apresentado como solução observada):**

1. Executar uma instância do OWASP Juice Shop própria para treinamento (por exemplo, via Docker), e abrir a rota `/#/score-board` para identificar o desafio.
2. Ler a descrição e pistas oficiais; em particular, a documentação aponta uma relação com a análise de um artefato de outro desafio (SSTi), de modo que uma reprodução pode exigir etapas prévias e engenharia reversa.
3. Identificar, apenas nessa instância, o endpoint que aceita destino de requisição no servidor, seu formato e possíveis alvos internos; registrar hipóteses e respostas observadas.
4. Confirmar a notificação de sucesso do **Score Board**, se obtida, e arquivar a captura em `evidencias/js-01-solved.png`.
5. Documentar versão da aplicação, pré-requisitos e comandos exatos efetivamente usados. Sem essa observação, não é correto inventar um payload universal.

**Risco e correção:** uma aplicação que aceita que URLs arbitrárias sejam buscadas pelo servidor pode acessar serviços que deveriam estar isolados. A recomendação é limitar destinos a um conjunto aprovado e bloquear acessos a redes internas/metadados, além de monitorar as requisições de saída.

**Resultado comprovado nesta entrega:** **não executado**. O roteiro deixa claro que a OWASP não apresenta um payload único independente do cenário e dos pré-requisitos.

## 5. Medidas de prevenção — visão de Engenharia de Software

| Falha observável nos labs | Recomendação de engenharia | Como verificar |
|---|---|---|
| Usuário define o host em `stockApi` | Substituir URL livre por um identificador de produto e resolver destino internamente | A aplicação não aceita URLs do cliente |
| Loopback e IPs privados permitidos | Política de saída por host/IP/porta; bloqueio de loopback, privadas e link-local | Testes para `localhost`, `127.1`, `192.168.*`, `169.254.*` |
| Bypass com encoding e IP alternativo | Parsing canônico; validar a URL já interpretada, não apenas substrings | Casos de teste com representação e codificação adversarial |
| Redirecionamento para destino proibido | `followRedirects(NEVER)` ou revalidar **cada** salto | Teste com resposta 302 de domínio permitido para IP privado |
| DNS que muda entre validação e conexão | Pinning da resolução/conexão e política de egress de rede | Testes de DNS rebinding e logs de destino real |
| Administração baseada em "origem local" | Autorização real e autenticação em toda rota administrativa | Testes de autorização para cada papel |
| Falta de rastreabilidade | Registrar destino, motivo da negação, identidade e código da resposta | Logs e alertas de egress suspeito |

**Regra de projeto:** a defesa mais segura é **não receber uma URL arbitrária do usuário**. O cliente envia apenas um `productId`; o back-end constrói a URL para um serviço de estoque previamente autorizado.

### Exemplo de arquitetura segura

```text
Cliente envia productId=42
           |
           v
Controller valida identificador
           |
           v
StockService constrói https://stock-api.example.test/stock/42
           |
           v
SsrfGuard valida scheme/host/porta/path
           |
           v
HttpClient sem redirects automáticos
           |
           v
Política de egress + proteção contra DNS rebinding na infraestrutura
```

## 6. Código Java executável (sem bibliotecas externas)

Este repositório inclui a **demonstração defensiva**, distinta da exploração dos labs:

- [`src/SsrfGuard.java`](./src/SsrfGuard.java) — valida um destino HTTPS de estoque previamente aprovado. Rejeita hosts divergentes, URL de loopback, paths inesperados, query, userinfo, portas não previstas e representações ambíguas.
- [`src/SafeStockClient.java`](./src/SafeStockClient.java) — aplica a validação e constrói cliente com redirects desligados e timeout.
- [`src/SsrfGuardTest.java`](./src/SsrfGuardTest.java) — suíte autocontida de **26 testes de aprovação/rejeição**. **Não** executa rede, PortSwigger ou Juice Shop.

**Requisitos:** JDK 17 ou superior. Executar a partir desta pasta:

```bash
javac -d out src/SsrfGuard.java src/SafeStockClient.java src/SsrfGuardTest.java
java -cp out SsrfGuardTest
```

**Resultado local verificado em ambiente Java 21:**

```text
OK: 26 verificacoes da politica SSRF passaram.
```

**Limitação técnica importante:** validar host e bloquear redirects em código é necessário, porém **não prova proteção completa contra DNS rebinding**. Clientes HTTP comuns resolvem nomes durante a conexão e podem usar um IP diferente do validado anteriormente. Sistemas reais devem fixar/verificar o destino efetivo e implementar política de saída na infraestrutura. O código Java é **didático**, não um componente pronto para produção.

## 7. Evidências a serem coletadas

A comprovação real de laboratório só existe quando a plataforma informa **Solved** (PortSwigger) ou **challenge solved** (OWASP Juice Shop). O relatório não inclui capturas artificiais, e o estado *PENDENTE* não deve ser confundido com aprovação.

Consulte [`EVIDENCIAS.md`](./EVIDENCIAS.md). Depois de resolver cada desafio, guarde os prints no subdiretório `evidencias/` com os nomes previstos na matriz, registre o endereço identificador de cada laboratório e comite no **mesmo PR**, sem criar um segundo Pull Request.

## 8. Conclusão

Os quatro cenários da PortSwigger demonstram que SSRF não se limita a uma URL maliciosa: envolve a **confiança indevida em requisições emitidas pelo servidor**. Permitir loopback, intranet, IP alternativo ou seguir redirecionamentos sem revalidar o destino contorna proteções superficiais. O desafio Juice Shop amplia a investigação para cenários com pré-requisitos mais complexos.

A principal lição para desenvolvimento seguro é controlar de forma positiva **quais recursos o back-end pode acessar**, além de proteger redes internas e autenticar operações sensíveis. O código desta entrega exemplifica essa abordagem e fornece verificações reproduzíveis, mas **a execução dos cinco desafios de laboratório permanece pendente de comprovação**.

## 9. Referências primárias

1. [Enunciado I2-07 — STSW](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/07-ssrf/README.md)
2. [PortSwigger — Server-side request forgery](https://portswigger.net/web-security/ssrf)
3. [PortSwigger — Lab Basic SSRF against localhost](https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-localhost)
4. [PortSwigger — Lab against another back-end system](https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-backend-system)
5. [PortSwigger — Lab with blacklist filter](https://portswigger.net/web-security/ssrf/lab-ssrf-with-blacklist-filter)
6. [PortSwigger — Lab with open redirect bypass](https://portswigger.net/web-security/ssrf/lab-ssrf-filter-bypass-via-open-redirection)
7. [OWASP Juice Shop — Challenge Catalog](https://juice-shop.github.io/tab_challenges.html)
8. [OWASP Juice Shop — Companion Guide, SSRF](https://help.owasp-juice.shop/part2/broken-access-control.html)