# P2-15 — OWASP Top Ten: Security Misconfiguration

**Aluno:** Leonardo Freitas (`leofreitasb`)  
**Disciplina:** Segurança e Teste de Software (STSW)  
**Roteiro:** [`prompts/15-misconfiguration.md`](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/15-misconfiguration.md)  
**Categoria estudada:** A05:2021, conforme o roteiro.  
**Data:** 24/11/2026.  
**Escopo desta entrega:** análise teórica. Os procedimentos com Burp Suite e OWASP Juice Shop abaixo são propostos, mas não foram executados; não há resultados experimentais nem conclusão de laboratório.

## 1. Introdução à vulnerabilidade Security Misconfiguration

**O que é.** Segundo a OWASP, ocorre *Security Misconfiguration* quando algum componente da aplicação ou de sua infraestrutura mantém configurações que enfraquecem a proteção prevista: permissões de nuvem abertas, recursos desnecessários ativos, contas padrão, erros detalhados, cabeçalhos de segurança ausentes ou parâmetros inseguros em servidores e frameworks. A falha pode nascer na instalação, na implantação ou em uma mudança posterior. Não se limita ao código da aplicação. [1]

**Exemplos comuns.** Diretórios com listagem habilitada; arquivos de backup ou `.env` publicados; painel administrativo acessível sem autenticação; conta de fábrica com senha conhecida; modo de depuração ligado em produção; resposta que mostra *stack trace*, caminhos internos ou versões; armazenamento em nuvem com leitura pública; CORS excessivamente permissivo; serviços e portas não utilizados expostos; e políticas de cookies ou cabeçalhos de segurança inadequadas. Uma configuração aberta pode também agravar outras falhas, mas nem toda ausência de cabeçalho, por si só, prova uma exploração. [1]

**Por que padrões inseguros são perigosos.** Um valor padrão é replicado em muitas instalações e pode ser conhecido por terceiros. Se ele permite acesso amplo, um sistema recém-publicado já começa vulnerável. Senhas padrão, contas de demonstração e permissões amplas reduzem o esforço necessário para descobrir dados ou assumir funções. Mesmo quando a configuração inicial era segura, copiar parâmetros de desenvolvimento para produção ou deixar configurações antigas após uma atualização pode reabrir o risco. [1]

**Impactos possíveis.** A consequência depende do recurso exposto: leitura e vazamento de dados pessoais, alteração ou exclusão de informações, acesso administrativo, interrupção do serviço, uso indevido da infraestrutura e dano reputacional. Mensagens de erro detalhadas podem fornecer pistas para ataques posteriores. A exposição de dados não demonstra, sozinha, que alguém os exfiltrou: isso exige evidência adicional, como registros e investigação. [1]

**Software, servidor e framework.** A distinção indica onde a configuração é aplicada, embora as camadas possam interagir:

| Camada | Exemplo de falha | Controle esperado |
|---|---|---|
| Software/aplicação | Rota administrativa publicada sem verificação de identidade e papel; modo de depuração ativo | Negar por padrão, autenticar e autorizar no backend, desativar debug em produção |
| Servidor ou infraestrutura | Nginx lista diretórios; porta de banco aberta à internet; bucket público | Restringir rotas, portas e permissões de rede e armazenamento |
| Framework ou plataforma | Endpoints de diagnóstico expostos; mensagens detalhadas e recursos opcionais ativados | Usar perfil de produção, expor apenas recursos necessários e revisar parâmetros de segurança |

**Gravidade e posição no Top 10.** No **OWASP Top 10:2021**, a categoria é **A05**, quinta posição, após ocupar a sexta na edição anterior. Essa posição representa uma **categoria de risco**, não uma nota universal para toda ocorrência: a gravidade de cada achado depende da exposição, dos dados e da possibilidade de abuso. Na base citada pela OWASP, 90% das aplicações foram *testadas* quanto a alguma configuração incorreta; a incidência média foi de 4,51%, com 208.387 ocorrências de CWEs mapeadas. Isso **não** quer dizer que 90% das aplicações estavam vulneráveis. A frequência e a amplitude das camadas configuráveis ajudam a explicar sua presença no Top 10. [1]

## 2. Exemplos práticos de Security Misconfiguration

**Exemplo de configuração insegura.** O trecho ilustrativo abaixo publica uma pasta de backups e permite listar seu conteúdo. O nome do domínio e os caminhos são fictícios; não se trata de um servidor real.

```nginx
server {
    listen 443 ssl;
    server_name exemplo.test;
    # Certificados TLS e demais diretivas omitidos para focar na falha.

    location /backups/ {
        alias /srv/empresa/backups/;
        autoindex on;
    }
}
```

**O erro.** `autoindex on` deixa o índice de `/backups/` visível, e `alias` torna os arquivos daquela pasta acessíveis pelo servidor web. Se houver cópias de banco ou configurações, uma pessoa sem sessão pode descobrir nomes de arquivos e solicitar seus conteúdos. O nome “backups” não fornece proteção. [1]

**Exploração possível, em cenário hipotético.** Uma requisição `GET /backups/` poderia retornar uma listagem; outra requisição a um arquivo nela indicado poderia baixar dados. A existência da listagem não garante que arquivos sensíveis estejam presentes, mas já evidencia exposição desnecessária. O risco seria confirmado pelo conteúdo e pelas permissões efetivas observadas em um ambiente autorizado. [1]

**Configuração corrigida.** Backups devem ficar fora de qualquer raiz ou `alias` servido na web, com acesso restrito no armazenamento. A regra abaixo bloqueia a URL antiga; ela precisa ser combinada com a remoção de outras rotas que possam apontar para o mesmo diretório.

```nginx
server {
    listen 443 ssl;
    server_name exemplo.test;
    # Certificados TLS e demais diretivas omitidos para focar na correção.

    location ^~ /backups/ {
        return 404;
    }
}
```

**Diretórios sensíveis sem autenticação.** Imagine uma publicação acidental de `/admin/`, `/backups/` e `/config/`. Um visitante anônimo encontra um índice em `/backups/`, baixa uma cópia de banco e obtém dados pessoais. Em `/admin/`, a interface aparece sem login; isso é um sinal de alerta, mas a verificação precisa alcançar as **operações do servidor**, pois uma página estática pode carregar enquanto a API corretamente retorna 401 ou 403. Da mesma forma, esconder um link de menu não protege um endpoint. O cenário é uma simulação conceitual, sem requisições reais. [1]

**O que verificar em endpoints administrativos.** Inventariar URLs e métodos HTTP administrativos; confirmar autenticação e autorização no backend para cada operação; testar requisições sem sessão, com sessão de usuário comum e com administrador; conferir respostas e conteúdo retornado, inclusive APIs chamadas pela interface; revisar credenciais padrão, MFA e privilégios; limitar o acesso de rede quando cabível; remover endpoints antigos, arquivos de backup e páginas de exemplo; e monitorar tentativas negadas. Respostas 401/403 devem ocorrer antes de qualquer alteração de estado ou divulgação de dados. Controle de acesso correto exige também regras de autorização, tema relacionado à A01:2021. [1]

## 3. Atividade prática de ataques e correções — procedimento teórico

**Como o Burp Suite poderia ser usado.** Em uma instância de teste própria ou autorizada, o procedimento seria: (1) configurar o navegador para passar pelo **Proxy** do Burp e limitar o escopo à instância de teste; (2) navegar pelas páginas e observar no **HTTP history** requisições, respostas, códigos de status, cookies e cabeçalhos; (3) enviar uma requisição pertinente ao **Repeater**; (4) repetir a mesma requisição sem sessão e com uma conta de privilégio menor; (5) comparar código, cabeçalhos e corpo da resposta; (6) registrar o comportamento e a configuração responsável antes de propor uma correção. Repetir uma rota conhecida é suficiente para verificar o controle; uma busca indiscriminada de caminhos não é necessária para este exercício. Um HTTP 200 isolado não comprova acesso indevido se apenas a página inicial foi entregue e a API negou os dados. [1][2]

**Tipos de configuração incorreta que essa inspeção pode revelar.** Listagem de diretórios, arquivos de backup públicos, painel ou API sem exigência de sessão, resposta com *stack trace* ou versão interna, cookies sem atributos adequados, cabeçalhos de segurança ausentes e política CORS ampla. O Burp mostra o comportamento HTTP; a causa precisa ser confirmada no servidor, na aplicação ou no processo de implantação. [1][2]

**Teste proposto no OWASP Juice Shop.** O roteiro da disciplina menciona Juice Shop, e o guia oficial apresenta o desafio **Error Handling** na categoria Security Misconfiguration. Em uma instância local de treinamento, seria possível abrir o Proxy/HTTP history, enviar uma entrada inválida a um formulário ou modificar um parâmetro de uma requisição capturada, repetir no Repeater e comparar a resposta com uma requisição válida. O objetivo seria identificar se a aplicação revela detalhes internos ou trata erros de maneira inconsistente. O guia explica que entradas incorretas ou parâmetros alterados podem provocar mensagens excessivamente informativas e, às vezes, aparecer apenas no console do navegador. [3]

**Análise da vulnerabilidade, sem resultado alegado.** Se a resposta de teste revelasse *stack trace*, caminho do servidor, consulta, nome de componente ou outro detalhe interno, o achado seria exposição de informação por tratamento de erros inadequado. Esses detalhes poderiam orientar ataques posteriores, embora não bastem para provar comprometimento. Aqui há apenas a análise do comportamento **esperado como possibilidade** a partir do guia; Burp Suite e Juice Shop **não foram executados nesta atividade**, portanto não se afirma que o desafio foi concluído nem que um erro específico foi observado. O desafio prático da I2-09 permanece separado. [3]

**Correções propostas.** Tratar exceções no servidor e devolver ao cliente uma mensagem genérica e código HTTP apropriado; registrar detalhes técnicos somente em logs com acesso restrito, sem segredos nem dados pessoais desnecessários; desabilitar debug e páginas de diagnóstico em produção; revisar os endpoints e recursos antigos; e criar testes de regressão para entradas inválidas e rotas administrativas. Após a alteração, repetir as mesmas requisições em ambiente de teste e verificar tanto a resposta pública quanto os logs. [1][2][3]

## 4. Aplicação em cenários reais

**Painel sem autenticação e credenciais padrão.** Se um painel administrativo estiver aberto, um terceiro poderá consultar dados, alterar configurações, criar contas, desativar controles ou interromper operações. Caso o painel peça login, mas aceite credenciais de fábrica, a proteção também é insuficiente. Consequências plausíveis incluem vazamento de informações, fraude, indisponibilidade, custos de resposta ao incidente e perda de confiança. A extensão real dependeria das permissões desse painel e dos registros da exploração. [1]

**Políticas e hardening.** Adotar configuração segura como padrão e revisão por pares antes de implantar; inventariar serviços e contas; remover recursos não usados; exigir troca de credenciais iniciais, senhas fortes e MFA para administração; aplicar menor privilégio e segmentação de rede; manter parâmetros versionados e automatizados por ambiente; verificar desvios de configuração periodicamente; e estabelecer dono, prazo e procedimento para correção. Testes automáticos e auditorias devem confirmar que o estado implantado corresponde à política, pois uma documentação correta não corrige uma configuração em produção. [1]

**Checklist de segurança para servidor web** — aplicar conforme a tecnologia e o uso real:

- [ ] Publicar apenas portas, serviços, módulos e rotas necessários; remover aplicações de exemplo e interfaces obsoletas.
- [ ] Manter backups, segredos, arquivos de ambiente e logs fora das pastas servidas; desativar listagem de diretórios.
- [ ] Exigir autenticação e autorização no backend em todos os métodos de rotas administrativas; restringir acesso de rede quando apropriado.
- [ ] Remover contas de fábrica e credenciais padrão; usar MFA e menor privilégio nas contas administrativas.
- [ ] Usar TLS e redirecionamento HTTPS; revisar certificados e protocolos suportados.
- [ ] Configurar cookies de sessão com `Secure`, `HttpOnly` e `SameSite` adequados ao fluxo; definir cabeçalhos como HSTS, `X-Content-Type-Options` e CSP conforme a aplicação.
- [ ] Desativar debug, páginas de diagnóstico e erros detalhados para clientes; manter logs úteis sob acesso restrito.
- [ ] Revisar permissões de arquivos, banco, armazenamento em nuvem e regras de firewall; negar acesso público não justificado.
- [ ] Atualizar servidor, framework e dependências; revisar configurações após upgrades.
- [ ] Automatizar verificação de configuração, testes de acesso e detecção de desvios; registrar mudanças e revisar logs de tentativas negadas.

**Caso público: exposição de dados da Verizon em 2017.** A UpGuard documentou um bucket Amazon S3 administrado pela fornecedora NICE Systems que permitia acesso público a registros de atendimento de clientes da Verizon, incluindo nomes, endereços, detalhes de conta e, em parte dos registros, PINs não mascarados. A UpGuard estimou **até 14 milhões** de clientes potencialmente afetados; a própria reportagem registra que a Verizon indicou **6 milhões**. A descoberta ocorreu em junho de 2017, e o acesso foi posteriormente fechado. O caso demonstra uma **exposição confirmada por configuração incorreta**, mas as fontes citadas não comprovam que terceiros maliciosos tenham baixado os dados. Não seria correto apresentar exfiltração como fato. [4]

**Lições do incidente.** Permissões de armazenamento devem ser privadas por padrão e verificadas continuamente; terceiros que tratam dados precisam seguir os mesmos controles contratuais e técnicos; logs e cópias não devem conter mais dados sensíveis que o necessário; informações de autenticação, como PINs, precisam de proteção especial; e alertas de exposição devem ter resposta rápida. A possibilidade de acesso anônimo a um repositório já constitui um problema relevante, mesmo sem evidência pública de uso criminoso. [1][4]

## 5. Conclusão

**Síntese.** Security Misconfiguration é o risco criado por parâmetros inseguros ou recursos desnecessariamente expostos em aplicação, servidor, framework e nuvem. Seus efeitos vão de vazamento de informação em mensagens de erro até acesso administrativo e exposição de bases de dados. O problema é evitável por um processo repetível de configuração segura, inventário, revisão e verificação em cada implantação. [1]

**Medidas de mitigação.** Negar acesso por padrão, aplicar menor privilégio, retirar recursos e contas não utilizados, proteger backups e dados em nuvem, exigir autenticação e autorização reais, configurar TLS e cabeçalhos, esconder detalhes técnicos do cliente, atualizar componentes e automatizar testes de configuração e regressão. [1]

**Prioridades em uma revisão de sistema.** Eu começaria por rotas administrativas e APIs, contas padrão, permissões públicas de arquivos e nuvem, exposição de portas e diretórios, segredos e backups, debug e mensagens de erro, configuração de sessão e TLS, versões e recursos antigos, além da diferença entre a configuração aprovada e a efetivamente implantada. A prioridade de correção deve considerar sensibilidade dos dados, alcance pela internet e facilidade de exploração. [1]

## Referências

1. OWASP. [A05:2021 — Security Misconfiguration](https://top10.owasp.org/2021/A05_2021-Security_Misconfiguration/).
2. OWASP. [Web Security Testing Guide — Configuration and Deployment Management](https://wstg.owasp.org/latest/06-Configuration_and_Deployment_Management/).
3. OWASP Juice Shop. [Companion Guide — Security Misconfiguration](https://pwning.owasp-juice.shop/companion-guide/latest/part2/security-misconfiguration.html).
4. UpGuard. [Cloud Leak: How a Verizon Partner Exposed Millions of Customer Accounts](https://www.upguard.com/labs/data-leaks/verizon-cloud-leak), 2017.

