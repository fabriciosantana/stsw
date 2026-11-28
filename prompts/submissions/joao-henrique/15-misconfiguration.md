# Prompt 15: OWASP Top Ten: Security Misconfiguration (A05:2021)

Referência: [OWASP A05:2021](https://owasp.org/Top10/A05_2021-Security_Misconfiguration/)

> Nota: as partes de "executar no Juice Shop" descrevem o procedimento e o que se espera observar; não incluí capturas de saída reais neste documento.

## 1. Introdução

Security Misconfiguration é quando um componente (aplicação, framework, servidor, banco, nuvem) está configurado de forma insegura, incompleta ou com padrões inseguros mantidos.

- **Exemplos comuns:** contas e senhas padrão ativas (`admin/admin`), recursos de exemplo/depuração habilitados, listagem de diretórios, mensagens de erro com stack trace, cabeçalhos de segurança ausentes (CSP, HSTS, X-Content-Type-Options), permissões excessivas em buckets de nuvem, portas/serviços desnecessários, XML parsers com entidades externas (XXE) habilitadas, software sem hardening.
- **Por que padrões são um risco:** são públicos e iguais para todos; ferramentas automáticas testam justamente `admin/admin`, `/admin`, `/.git`, `/actuator` etc. Padrões priorizam a facilidade de instalação, não a segurança.
- **Impactos:** acesso administrativo, vazamento de dados e de código-fonte, execução de código, escalada de privilégios, negação de serviço e comprometimento total do ambiente.
- **Software x servidor x framework:** *software/aplicação* — flags de debug, contas padrão, funcionalidades não usadas; *servidor* (Apache, Nginx, SO, nuvem) — diretórios listados, TLS fraco, serviços e portas abertos, permissões de arquivos; *framework* — modo desenvolvimento em produção, endpoints de gerenciamento expostos (Spring Actuator, Django `DEBUG=True`), CORS aberto.
- **Gravidade/posição:** subiu da 6ª (2017) para a 5ª posição em 2021 (incluiu XXE, antiga A04); 90% das aplicações testadas apresentaram algum tipo de má configuração. É alta porque é fácil de explorar, ampla e de grande impacto, e cresce com a complexidade (nuvem, containers, IaC).

## 2. Exemplos práticos

Configuração insegura (Spring Boot `application.properties`):

```properties
management.endpoints.web.exposure.include=*
management.endpoint.env.show-values=ALWAYS
server.error.include-stacktrace=always
spring.h2.console.enabled=true
```

- **O que está errado:** expõe todos os endpoints do Actuator (`/actuator/env`, `/heapdump`), mostra valores de variáveis (senhas, chaves), devolve stack trace ao usuário e habilita o console do banco H2.
- **Exploração:** o atacante acessa `/actuator/env` e `/actuator/heapdump` e obtém credenciais do banco/chaves de API; os stack traces revelam versões e a estrutura do código; o console H2 permite executar SQL.
- **Configuração segura:**

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=never
server.error.include-stacktrace=never
spring.h2.console.enabled=false
```

mais autenticação e rede separada para a porta de gerenciamento (`management.server.port`).

- **Cenário: diretórios sensíveis sem autenticação:** o servidor com `autoindex on` em `/backup/` mostra `dump.sql` e `.env`; qualquer pessoa baixa o banco. Correção: remover os arquivos do diretório público, `autoindex off`, bloquear `/.git`, `/.env`, `*.bak` e exigir autenticação.
- **Verificar para evitar exposição de endpoints administrativos:** exigem autenticação forte/MFA e autorização por papel; não ficam acessíveis pela internet (VPN, allow list de IP, rede de gerenciamento); credenciais padrão trocadas; painéis de exemplo e de depuração removidos; inventário de rotas revisado; testes automatizados para garantir que retornam 401/403.

## 3. Atividade prática (Burp Suite e Juice Shop)

- **O que o Burp (Scanner/Repeater/Spider) detecta:** cabeçalhos de segurança ausentes, versões reveladas (`Server`, `X-Powered-By`), listagem de diretórios, arquivos de backup e de configuração acessíveis, métodos HTTP perigosos (TRACE), cookies sem `Secure`/`HttpOnly`, mensagens de erro verbosas, CORS permissivo.
- **Teste no Juice Shop (instância local):**
  1. Configurar o navegador para usar o proxy do Burp e navegar pelo Juice Shop com o proxy ativo.
  2. Em *Target > Site map*, procurar diretórios expostos: o Juice Shop publica `/ftp` com listagem de arquivos (ex.: `acquisitions.md`, backups) sem autenticação.
  3. Provocar um erro (por exemplo, enviar um valor inválido em um parâmetro da API ou um JSON malformado) e analisar a resposta: o tratamento de erros é inconsistente e devolve detalhes técnicos (desafio "Error Handling").
  4. Pedir `/api-docs` / `/rest/...` e procurar interfaces antigas não desativadas (desafio "Deprecated Interface": upload de XML/B2B).
  5. No Repeater, verificar os cabeçalhos de resposta (CORS `Access-Control-Allow-Origin: *`, ausência de CSP/HSTS).
- **Vulnerabilidade encontrada e correção:** diretório `/ftp` público, erros detalhados, interface B2B legada ligada, CORS aberto. Correções: remover/proteger o `/ftp`, tratamento de erros centralizado com mensagem genérica, desativar a interface legada, restringir CORS às origens necessárias e adicionar cabeçalhos de segurança.

## 4. Aplicação em cenários reais

Empresa com painel de administração exposto sem autenticação e com credenciais padrão:

- **Consequências:** qualquer pessoa entra como administrador, altera ou apaga dados, cria usuários, instala backdoors e usa o servidor para atacar outros alvos; há vazamento de dados pessoais, extorsão (ransomware), multas (LGPD) e perda de confiança.
- **Políticas e hardening que evitariam:** baseline de configuração segura (CIS Benchmarks), processo de hardening repetível e automatizado (IaC, imagens douradas), troca obrigatória de credenciais padrão, revisão de configuração antes do deploy, varreduras periódicas, segregação de ambientes e menor privilégio.
- **Checklist para servidores web:**
  - [ ] Credenciais padrão removidas/trocadas; MFA nos painéis.
  - [ ] Painéis administrativos fora da internet pública.
  - [ ] Listagem de diretórios desativada; `.git`, `.env`, backups e arquivos temporários bloqueados.
  - [ ] Banner/versão ocultos (`ServerTokens Prod`, `server_tokens off`).
  - [ ] Apenas TLS 1.2+ com cifras fortes e HSTS.
  - [ ] Cabeçalhos: CSP, X-Content-Type-Options, X-Frame-Options, Referrer-Policy.
  - [ ] Modo debug desligado; erros genéricos para o usuário.
  - [ ] Métodos HTTP desnecessários desativados (TRACE, PUT, DELETE).
  - [ ] Serviços/portas/módulos sem uso desativados; firewall com negação padrão.
  - [ ] Sistema e pacotes atualizados; permissões mínimas dos arquivos e do usuário do serviço.
  - [ ] Logs ativados e monitorados.
- **Caso público real: Capital One (2019)** — além do SSRF, um WAF mal configurado e uma função IAM com permissões excessivas permitiram o acesso a dados de 100 milhões de clientes. Outro exemplo clássico: buckets S3 abertos ao público em vários incidentes (ex.: dados de eleitores nos EUA, 2017, ~198 milhões de registros), causados só por permissão mal configurada.
- **Lições:** configuração é uma superfície de ataque; automatizar e auditar (CSPM, scanners), aplicar menor privilégio, não depender de padrões, revisar permissões de nuvem e testar de fora como atacante.

## 5. Conclusão

- **Medidas de mitigação:** processo de hardening repetível; ambientes (dev/teste/prod) configurados de forma idêntica, mas com credenciais diferentes; plataforma mínima (sem recursos não usados); revisão de configurações em cada release; automação de verificação de configuração (IaC scanning, CSPM); cabeçalhos de segurança; segmentação.
- **O que eu verificaria numa revisão:** credenciais e contas padrão; endpoints de administração/gerenciamento e de debug; tratamento de erros e logs; cabeçalhos e TLS; CORS; permissões de arquivos, de nuvem e de banco; portas e serviços abertos; versões e patches; segredos no código/ambiente; e se a configuração está versionada e igual entre ambientes.
