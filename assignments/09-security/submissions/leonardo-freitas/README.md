# I2-09 — Security Misconfiguration, Vulnerable and Outdated Components e Security Logging

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Enunciado:** [assignments/09-security/README.md](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/09-security/README.md)  
**Data de preparação do roteiro:** 24/11/2026

## Escopo e estado desta entrega

O enunciado exige **oito desafios práticos**: quatro do OWASP Juice Shop e quatro da PortSwigger Web Security Academy. Este material contém as **instruções reproduzíveis**, as causas e as medidas corretivas para cada desafio, verificadas contra as referências oficiais. **Não representa oito execuções já realizadas:** não houve acesso à instância local do Juice Shop nem às instâncias autenticadas da PortSwigger nesta sessão. Nenhuma captura de tela de conclusão foi fornecida para esta atividade.

**Situação de execução:** 0/8 laboratórios comprovados. Consulte [EVIDENCIAS.md](./EVIDENCIAS.md) antes de considerar a atividade pronta para envio.

> **Escopo ético:** executar apenas no Juice Shop próprio/laboratório autorizado e nas instâncias iniciadas pela sua conta na Web Security Academy. Não aplicar estes testes a sistemas de terceiros.

## Preparação do ambiente

1. Iniciar o **OWASP Juice Shop** local e confirmar que `http://localhost:3000` responde (conforme o método de instalação utilizado pelo aluno). Se necessário, uma opção é usar uma instalação autorizada com Docker: `docker run --rm -p 3000:3000 bkimminich/juice-shop`.
2. Abrir o navegador com **DevTools** (abas Network e Console) para acompanhar respostas e erros. Se usar o Burp Proxy, configurar o navegador para que as solicitações sejam registradas em **Proxy > HTTP history**.
3. Acessar cada lab pela página indicada pela disciplina, iniciar uma instância própria da PortSwigger, abrir o lab no navegador do Burp e usar **Repeater** para testar requisições.
4. Ao concluir cada desafio, registrar **uma captura real** mostrando o estado *Challenge Solved* ou *Solved*, preferencialmente junto à URL/nome do laboratório; adicionar a captura à pasta `evidencias/` e atualizar `EVIDENCIAS.md`.

---

## Parte A — OWASP Juice Shop: Security Misconfiguration

### JS-01 — Error Handling (provocar erro mal tratado)

**Objetivo do professor:** “Provoke an error that is neither very gracefully nor consistently handled”.

**Passos de reprodução:**
1. Abrir a instância Juice Shop local.
2. Fazer uma requisição para `http://localhost:3000/rest/qwertz` ou usar outra entrada malformada em formulário de laboratório.
3. Inspecionar a resposta no navegador/Burp e o **Console** do DevTools. A solução oficial cita a exposição de uma página de erro/stack trace ou detalhes indevidos, dependendo da versão.
4. Conferir se o Score Board marca o desafio **Error Handling** como concluído.
5. Salvar a imagem `evidencias/01-error-handling.png` e anotar o status HTTP efetivamente observado.

**Causa:** tratamento genérico de exceções devolve informações internas ao cliente. **Correção:** resposta padronizada sem stack trace ou queries, logs completos somente no servidor e testes para entradas inválidas.

**Referência:** [Guia oficial, seção Error Handling](https://pwning.owasp-juice.shop/companion-guide/latest/part2/security-misconfiguration.html) e [soluções](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html).

### JS-02 — Deprecated Interface (interface B2B desativada indevidamente)

**Objetivo do professor:** “Use a deprecated B2B interface that was not properly shut down”.

**Passos de reprodução:**
1. Entrar no Juice Shop usando uma conta de teste.
2. Abrir **Contact Us > Complain?** (formulário de reclamação com anexo).
3. Conferir as restrições de arquivos na interface. No DevTools, buscar a lista de MIME types do upload no JavaScript da aplicação; a solução oficial mostra que tipos XML também eram aceitos pelo cliente.
4. Preparar um arquivo XML simples local com menos de 100 KB, como `<invoice><id>1</id></invoice>`, e selecioná-lo no diálogo de arquivos, usando filtro `*.xml` se necessário.
5. Preencher a mensagem da reclamação e enviar. Verificar no **Network** a resposta da rota antiga, descrita no guia como **410 Gone**.
6. Confirmar no Score Board a conclusão **Deprecated Interface**; salvar `evidencias/02-deprecated-interface.png`.

**Causa:** a interface antiga permaneceu funcional/atingível apesar de ter sido descontinuada. **Correção:** remover endpoint, validação e roteamento legados no servidor (não só ocultar opções no frontend), testar caminhos antigos após a desativação.

**Referência:** [solução oficial — Deprecated Interface](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html).

---

## Parte B — OWASP Juice Shop: Vulnerable and Outdated Components

### JS-03 — Frontend Typosquatting

**Objetivo do professor:** “Inform the shop about a typosquatting imposter that dug itself deep into the frontend”.

**Passos de reprodução:**
1. Navegar para `http://localhost:3000/3rdpartylicenses.txt`, que lista dependências de frontend na versão descrita no guia oficial.
2. Procurar pelo pacote **`ngy-cookie`**, semelhante ao legítimo `ngx-cookie`.
3. Na aplicação, abrir **Contact Us** (`/#/contact`), preencher o comentário informando o nome exato **`ngy-cookie`**, responder ao CAPTCHA e enviar.
4. Confirmar **Frontend Typosquatting** no Score Board e salvar `evidencias/03-frontend-typosquatting.png`.

**Causa:** dependência indevida com nome muito parecido com o legítimo. **Correção:** revisar lockfiles e origem de pacotes, aplicar allowlist/revisão de dependências, usar SCA e reforçar o processo de revisão de PR.

**Referência:** [guia oficial — Vulnerable Components](https://pwning.owasp-juice.shop/companion-guide/latest/part2/vulnerable-components.html) e [solução](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html).

### JS-04 — Vulnerable Library

**Objetivo do professor:** “Inform the shop about a vulnerable library it is using”.

**Passos de reprodução:**
1. Localizar o backup `package.json.bak` no ambiente didático do Juice Shop (desafio relacionado ao backup esquecido) e examinar as dependências declaradas.
2. Comparar o pacote e a versão com avisos de segurança. A solução oficial de v20.3.0 aceita **`sanitize-html` e `1.4.2`** ou **`express-jwt` e `0.1.3`**.
3. Acessar `http://localhost:3000/#/contact` e enviar comentário informando, por exemplo, `sanitize-html 1.4.2: versão com vulnerabilidade conhecida; revisar e atualizar a dependência`.
4. Confirmar **Vulnerable Library** no Score Board e salvar `evidencias/04-vulnerable-library.png`.

**Causa:** biblioteca de terceiros em versão com vulnerabilidade documentada. **Correção:** atualizar para versão corrigida, executar análise SCA/CI e avaliar impacto transitivo. A aceitação de uma resposta no desafio **não substitui** uma varredura da versão realmente instalada.

**Referência:** [solução oficial — Vulnerable Library](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html) e [advisory GHSA-3j7m-hmh3-9jmp](https://github.com/advisories/GHSA-3j7m-hmh3-9jmp).

---

## Parte C — PortSwigger/Burp Suite: Information Disclosure

> Os dados secretos e resultados variam conforme a **instância do laboratório**. Não copiar valores de outras sessões ou inventar captura de conclusão.

### PS-01 — Information disclosure in error messages

**Objetivo:** obter a versão do framework exposta por mensagem de erro detalhada.

1. [Abrir o laboratório oficial](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-in-error-messages) e iniciar sua instância.
2. Abrir a página de algum produto e capturar `GET /product?productId=1` (o ID pode variar).
3. Enviar a requisição para o **Burp Repeater**.
4. Substituir o valor de `productId` por texto em vez de inteiro, como `productId="example"`.
5. Observar o stack trace, identificar a versão do Apache Struts 2 (o walkthrough oficial aponta **2.3.31**) e usar **Submit solution** no laboratório.
6. Capturar a tela de **Solved** em `evidencias/05-infoleak-error.png`.

**Falha:** exceção detalhada revela versão de framework. **Mitigação:** tratamento seguro de erros, validação de parâmetros, patches e supressão de stack traces públicos.

### PS-02 — Information disclosure on debug page

**Objetivo:** encontrar e enviar o valor de `SECRET_KEY` divulgado em página de debug.

1. [Abrir o laboratório oficial](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-on-debug-page).
2. No Burp **Target > Site map**, inspecionar comentários HTML; a solução oficial encontra um link para `/cgi-bin/phpinfo.php`.
3. Abrir a página via Repeater, buscar `SECRET_KEY` e registrar o problema sem publicar o valor da chave.
4. Usar **Submit solution** com o valor **da sua própria instância**.
5. Salvar `evidencias/06-infoleak-debug.png`, ocultando quaisquer credenciais na captura.

**Falha:** página de diagnóstico exposta publicamente. **Mitigação:** remover páginas de debug de produção, restringir acesso, desabilitar exposição de variáveis de ambiente.

### PS-03 — Source code disclosure via backup files

**Objetivo:** encontrar o segredo de banco em arquivo de backup acessível pela web.

1. [Abrir o laboratório oficial](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-via-backup-files).
2. Requisitar `/robots.txt`; a solução oficial indica o diretório `/backup`.
3. Acessar `/backup/ProductTemplate.java.bak` e identificar no código a senha de conexão com o banco.
4. Enviar a senha **da instância** por **Submit solution** e confirmar **Solved**.
5. Salvar `evidencias/07-infoleak-backup.png`, ocultando o valor literal da senha.

**Falha:** backups com código-fonte e credenciais publicados no webroot. **Mitigação:** retirar artefatos de backup da pasta pública, rotacionar segredos e usar secret manager.

### PS-04 — Authentication bypass via information disclosure

**Objetivo:** identificar header interno exposto por TRACE e demonstrar bypass na instância didática.

1. [Abrir o laboratório oficial](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-authentication-bypass).
2. No Burp Repeater, testar `GET /admin` e examinar a resposta de restrição a administradores/localhost.
3. Alterar o método para `TRACE /admin`; a solução oficial mostra que a resposta revela o header **`X-Custom-IP-Authorization`**.
4. Nas requisições **somente da instância do laboratório**, adicionar `X-Custom-IP-Authorization: 127.0.0.1` (no Repeater ou em **Proxy > Match and replace**).
5. Abrir `/admin` com o header, acessar a interface didática e excluir o usuário de laboratório **`carlos`**, conforme a condição de conclusão definida pela PortSwigger.
6. Confirmar **Solved** e salvar `evidencias/08-auth-bypass-infoleak.png`.

**Falha:** confiança em cabeçalho de origem que o cliente consegue controlar. **Mitigação:** confiar somente em informações de origem definidas e verificadas pelo proxy reverso, aplicar autenticação e autorização no servidor, restringir TRACE.

---

## Critérios de conclusão e envio

Apenas considerar a I2-09 integralmente concluída se:
- [ ] Os **4 desafios Juice Shop** estiverem marcados como resolvidos no Score Board da instalação usada.
- [ ] Os **4 labs PortSwigger** estiverem marcados **Solved** na conta/instâncias utilizadas.
- [ ] As **8 evidências originais** tiverem sido salvas na pasta `evidencias/` e vinculadas no documento `EVIDENCIAS.md`.
- [ ] Os resultados reais, versões, respostas HTTP e observações tiverem sido registrados sem segredos sensíveis.
- [ ] O Pull Request contiver apenas os arquivos da I2-09, tiver sido revisado e seu link submetido no AVA.

**Observação:** este README foi preparado como roteiro técnico com base em fontes oficiais, não como um relatório de execução bem-sucedida. Sua leitura não comprova os oito desafios.

## Fontes oficiais

- [Enunciado da I2-09 — STSW](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/09-security/README.md)
- [Juice Shop — Security Misconfiguration](https://pwning.owasp-juice.shop/companion-guide/latest/part2/security-misconfiguration.html)
- [Juice Shop — Vulnerable Components](https://pwning.owasp-juice.shop/companion-guide/latest/part2/vulnerable-components.html)
- [Juice Shop — Challenge solutions (release latest consultada em 24/11/2026)](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html)
- [PortSwigger — Error messages](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-in-error-messages)
- [PortSwigger — Debug page](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-on-debug-page)
- [PortSwigger — Backup files](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-via-backup-files)
- [PortSwigger — Authentication bypass](https://portswigger.net/web-security/information-disclosure/exploiting/lab-infoleak-authentication-bypass)
