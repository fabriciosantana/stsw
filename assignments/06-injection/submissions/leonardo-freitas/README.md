# I2-06 — Injection: SQL Injection e Cross-Site Scripting (XSS)

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software — IDP  
**Atividade:** 06 — Resolver laboratórios de Injection  
**Ambientes:** OWASP Juice Shop e PortSwigger Web Security Academy  
**Branch de entrega:** `i2-06-injection-leonardo`

> **Situação da entrega:** os 17 procedimentos de laboratório foram documentados, com causa técnica, forma de reprodução e mitigação. **Os laboratórios externos não foram executados/verificados automaticamente neste trabalho e nenhum screenshot de conclusão foi produzido.** O registro de execução real deve ser acrescentado na pasta `evidencias/` após completar cada desafio. Consulte [EVIDENCIAS.md](EVIDENCIAS.md).

## 1. Objetivo e escopo

Esta atividade estuda duas famílias de vulnerabilidades:

- **SQL Injection (SQLi):** o sistema trata conteúdo controlado pelo usuário como parte de uma consulta SQL, o que permite alterar a lógica da consulta.
- **Cross-Site Scripting (XSS):** conteúdo controlado pelo usuário chega a uma saída de HTML/JavaScript ou a uma API perigosa do DOM sem a proteção apropriada e pode ser executado no navegador.

O foco de Engenharia de Software é estabelecer uma ligação entre **entrada → interpretação indevida → efeito → correção no código**. Os exemplos aqui se destinam **exclusivamente aos laboratórios didáticos indicados pelo professor e às instâncias locais autorizadas**.

### Cobertura da atividade

| Ambiente | Conteúdo | Quantidade |
|---|---|---:|
| OWASP Juice Shop | Score Board; login admin; credenciais; Bonus Payload; DOM XSS; Reflected XSS | 6 |
| PortSwigger | SQLi na cláusula WHERE; bypass de login | 2 |
| PortSwigger | Reflected, Stored e DOM XSS em diferentes contextos | 9 |
| **Total** | **Procedimentos documentados** | **17** |

**Legenda de evidências:** `PENDENTE` significa que há uma solução orientada no texto, **não** que o desafio tenha sido concluído. Os nomes de prints sugeridos estão em `EVIDENCIAS.md`.

## 2. Preparação do ambiente de laboratório

### 2.1 OWASP Juice Shop

Uma forma de executar em ambiente local controlado, se o Docker estiver instalado:

~~~bash
docker run --rm -p 3000:3000 bkimminich/juice-shop
~~~

Abra `http://localhost:3000`. Outra opção é utilizar uma instância da plataforma que você tenha autorização para acessar. O endereço `localhost` é apenas exemplo de execução local.

Para comprovar as atividades do Juice Shop, registre a notificação de desafio resolvido e/ou a atualização do Score Board, com título do desafio identificável.

### 2.2 PortSwigger Web Security Academy

Abra o **link do laboratório correspondente** na seção 4, clique em **Access the lab** e use apenas a instância criada para sua sessão. O domínio fornecido pela plataforma tem o formato `https://<id>.web-security-academy.net`. Para comprovar, capture a página indicando **Solved**.

**Importante:** não use os payloads em sites de terceiros, sistemas reais ou alvos sem autorização. Alguns desafios dependem de versão/contexto; se o resultado diferir, examine o HTML, a requisição e a resposta antes de ajustar o payload.

## 3. Parte 1 — OWASP Juice Shop

### JS-01 — Finding the Score Board

**Objetivo:** descobrir e acessar o painel de desafios que não está exposto no menu principal.

**Procedimento:** abra a rota `http://localhost:3000/#/score-board`. Para investigar a descoberta, procure a string `score-board` no código da SPA usando DevTools (F12 → Sources/Search).

**Explicação técnica:** uma rota existente não deixa de estar acessível por não haver um link visível. **Ocultar navegação não equivale a autorização.**

**Correção de engenharia:** controles de acesso devem existir no servidor para recursos protegidos; nunca dependa só da ausência de botões ou links. *Neste desafio o acesso ao painel é intencional; não há, necessariamente, uma falha de autorização a corrigir no produto.*

**Evidência esperada:** painel aberto ou notificação de desafio resolvido. **Status: PENDENTE.**

### JS-02 — SQL Injection: Login Admin

**Objetivo:** demonstrar autenticação comprometida por interpolação de entrada na consulta SQL.

**Onde testar:** formulário **Login** do Juice Shop local.

~~~text
Email: ' OR 1=1--
Senha: 123
~~~

**Por que funciona no desafio:** a aspa fecha a string SQL, o `OR 1=1` cria uma condição sempre verdadeira, e `--` comenta o restante da consulta, incluindo a condição de senha. Em versões/configurações do desafio, a primeira linha retornada permite login administrativo.

**Causa raiz:** construção de SQL pela concatenação de dados externos.

**Mitigação:** use **prepared statements**, autenticação com comparação de hash forte e privilégios mínimos. Não confie apenas em validação de campo.

**Evidência esperada:** desafio de login administrativo marcado como concluído. **Status: PENDENTE.**

### JS-03 — SQL Injection: User Credentials via UNION

**Objetivo:** ilustrar vazamento de informações pela combinação de consultas com `UNION`.

**Onde testar:** campo de busca de produtos do Juice Shop.

~~~sql
')) UNION SELECT id, email, password, '4','5','6','7','8','9' FROM Users--
~~~

**Explicação:** o prefixo fecha a expressão de busca da versão vulnerável; `UNION SELECT` combina registros de `Users` com os resultados da busca. Os campos artificiais `'4'` até `'9'` completam as **9 colunas** esperadas nessa variante de laboratório. O comentário `--` desconsidera o restante da consulta. A estrutura da consulta pode mudar entre versões.

**Causa raiz:** interpolação em SQL e acesso desnecessário a uma tabela com dados sensíveis.

**Mitigação:** consultas parametrizadas, menor privilégio de acesso do usuário do banco, exclusão de colunas sensíveis e armazenamento de senhas com **Argon2id** ou **bcrypt** e salt adequado. O desafio ilustra hashes fracos; nunca replique isso em produção.

**Evidência esperada:** notificação do desafio de credenciais; **não publique credenciais reais** em capturas de tela. **Status: PENDENTE.**

### JS-04 — Bonus Payload (DOM XSS)

**Objetivo:** demonstrar que HTML inserido numa busca pode virar elemento executável de página.

**Onde testar:** campo de busca de produtos.

~~~html
<iframe width="100%" height="166" scrolling="no" frameborder="no" allow="autoplay" src="https://w.soundcloud.com/player/?url=https%3A//api.soundcloud.com/tracks/771984076&color=%23ff5500&auto_play=true&hide_related=false&show_comments=true&show_user=true&show_reposts=false&show_teaser=true"></iframe>
~~~

**Explicação:** o texto da busca alcança uma inserção de HTML no DOM. Como o navegador interpreta tags injetadas, renderiza o player. O desafio depende do payload específico exigido pelo Juice Shop.

**Mitigação:** use inserção de texto (`textContent`) para conteúdo não confiável; quando HTML de usuário for requisito, aplique sanitização confiável e política restritiva de frames/CSP.

**Evidência esperada:** notificação **Bonus Payload**. **Status: PENDENTE.**

### JS-05 — DOM XSS

**Objetivo:** execução de código a partir de dados manipulados no DOM.

**Onde testar:** busca de produtos.

~~~html
<iframe src="javascript:alert(`xss`)">
~~~

**Explicação:** valores chegam a um *sink* inseguro como `innerHTML`. Uma tag `script` inserida por essa API geralmente não executa, mas o JavaScript pode ser acionado por outros elementos e atributos. A exploração é específica do ambiente vulnerável.

**Mitigação:** `textContent`, sanitização para HTML permitido, bloqueio de esquemas `javascript:` em URLs e CSP defensiva.

**Evidência esperada:** diálogo JavaScript e reconhecimento do desafio. **Status: PENDENTE.**

### JS-06 — Reflected XSS

**Objetivo:** inserir conteúdo na página de rastreamento por meio de um parâmetro da URL.

**Onde testar:** navegue na instância local usando a rota abaixo (faça URL-encoding quando necessário).

~~~text
http://localhost:3000/#/track-result?id=<iframe src="javascript:alert(`xss`)">
~~~

**Explicação:** o identificador do pedido é retirado da URL e refletido no HTML da página sem neutralização adequada; o payload pode ser executado quando a rota é acessada.

**Mitigação:** tratar identificadores como dados; validar formato quando apropriado e inserir respostas em contextos de texto seguro em vez de HTML.

**Evidência esperada:** indicação de **Reflected XSS** no Score Board. **Status: PENDENTE.**

## 4. Parte 2 — PortSwigger Web Security Academy

**Observação:** cada laboratório deve ser aberto pelo seu link oficial. As sequências abaixo são exemplos de resolução fornecidos como guia; é necessário confirmar o selo **Solved** na sua instância para declarar conclusão.

### PS-01 — SQLi em WHERE: revelar dados ocultos

**Laboratório:** [SQL injection vulnerability in WHERE clause allowing retrieval of hidden data](https://portswigger.net/web-security/sql-injection/lab-retrieve-hidden-data)

**Ponto de entrada:** parâmetro `category` da página de produtos.

~~~text
/filter?category=Gifts'+OR+1=1--
~~~

**Efeito esperado:** a condição `OR 1=1` torna o filtro verdadeiro e o comentário remove a restrição de produtos lançados (`released = 1`). Produtos antes ocultos aparecem.

**Correção:** parametrizar o filtro e aplicar autorização/regra de visibilidade no back-end. **Status: PENDENTE.**

### PS-02 — SQLi: bypass de login

**Laboratório:** [SQL injection vulnerability allowing login bypass](https://portswigger.net/web-security/sql-injection/lab-login-bypass)

**Ponto de entrada:** formulário de login.

~~~text
Username: administrator'--
Password: teste
~~~

**Efeito esperado:** o comentário SQL neutraliza o trecho de validação de senha numa consulta vulnerável. **Correção:** consulta parametrizada, fluxo seguro de autenticação, verificação de hash e autenticação multifator quando adequada. **Status: PENDENTE.**

### PS-03 — Reflected XSS em HTML sem encoding

**Laboratório:** [Reflected XSS into HTML context with nothing encoded](https://portswigger.net/web-security/cross-site-scripting/reflected/lab-html-context-nothing-encoded)

**Ponto de entrada:** caixa de pesquisa.

~~~html
<script>alert(1)</script>
~~~

**Efeito esperado:** o valor pesquisado é devolvido como HTML executável. **Correção:** escape contextual na saída HTML e preferência por APIs seguras de texto. **Status: PENDENTE.**

### PS-04 — Stored XSS em comentários

**Laboratório:** [Stored XSS into HTML context with nothing encoded](https://portswigger.net/web-security/cross-site-scripting/stored/lab-html-context-nothing-encoded)

**Ponto de entrada:** comentário do blog do laboratório, com demais campos válidos.

~~~html
<script>alert(1)</script>
~~~

**Efeito esperado:** o script é armazenado e renderizado quando o post é acessado. **Correção:** sanitização/escaping na saída dos comentários, proteção de templates e CSP como defesa adicional. **Status: PENDENTE.**

### PS-05 — DOM XSS em `document.write`/`location.search`

**Laboratório:** [DOM XSS in document.write sink using source location.search](https://portswigger.net/web-security/cross-site-scripting/dom-based/lab-document-write-sink)

**Ponto de entrada:** busca.

~~~html
"><svg onload=alert(1)>
~~~

**Efeito esperado:** o prefixo fecha o valor do atributo/elemento gerado e injeta um novo elemento com evento executável. **Correção:** não construir HTML com `document.write` e valores não confiáveis; use DOM APIs seguras. **Status: PENDENTE.**

### PS-06 — DOM XSS em `innerHTML`/`location.search`

**Laboratório:** [DOM XSS in innerHTML sink using source location.search](https://portswigger.net/web-security/cross-site-scripting/dom-based/lab-innerhtml-sink)

**Ponto de entrada:** busca.

~~~html
<img src=1 onerror=alert(1)>
~~~

**Efeito esperado:** a imagem falha ao carregar e o evento `onerror` dispara. **Correção:** usar `textContent` para texto e sanitização robusta se HTML for indispensável. **Status: PENDENTE.**

### PS-07 — DOM XSS em `href` configurado por jQuery

**Laboratório:** [DOM XSS in jQuery anchor href attribute sink using location.search source](https://portswigger.net/web-security/cross-site-scripting/dom-based/lab-jquery-href-attribute-sink)

**Ponto de entrada:** parâmetro `returnPath` da página de feedback.

~~~text
?returnPath=javascript:alert(document.cookie)
~~~

**Efeito esperado:** o valor não validado vira `href` do link de retorno. No laboratório, clique no link para disparar o código. **Correção:** validar o destino contra allow-list de caminhos e protocolos; não aceitar `javascript:`. **Status: PENDENTE.**

### PS-08 — DOM XSS em seletor jQuery com `hashchange`

**Laboratório:** [DOM XSS in jQuery selector sink using a hashchange event](https://portswigger.net/web-security/cross-site-scripting/dom-based/lab-jquery-selector-hash-change-event)

**Ponto de entrada:** hash da URL, processado indevidamente pelo seletor jQuery. O exercício utiliza o *exploit server* fornecido pela própria plataforma.

~~~html
<iframe src="https://SEU-ID.web-security-academy.net/#" onload="this.src+='<img src=x onerror=print()>'"></iframe>
~~~

**Procedimento:** substitua `SEU-ID` pelo subdomínio da **sua instância do lab**, salve no exploit server e utilize a função de entrega simulada à vítima do laboratório.

**Efeito esperado:** um `hashchange` leva o seletor inseguro a interpretar conteúdo como HTML. **Correção:** jamais passar hash não confiável para seletor/gerador de HTML; use seletores constantes e APIs seguras. **Status: PENDENTE.**

### PS-09 — Reflected XSS em atributo com `< >` codificados

**Laboratório:** [Reflected XSS into attribute with angle brackets HTML-encoded](https://portswigger.net/web-security/cross-site-scripting/contexts/lab-attribute-angle-brackets-html-encoded)

**Ponto de entrada:** valor da busca retornado em atributo HTML.

~~~text
"onmouseover="alert(1)
~~~

**Efeito esperado:** a aspa fecha o atributo original e inclui um novo manipulador de evento; mover o cursor sobre o elemento executa o payload. **Correção:** codificação de atributos HTML e templates seguros, além de evitar handlers inline. **Status: PENDENTE.**

### PS-10 — Stored XSS em `href` de comentário

**Laboratório:** [Stored XSS into anchor href attribute with double quotes HTML-encoded](https://portswigger.net/web-security/cross-site-scripting/contexts/lab-href-attribute-double-quotes-html-encoded)

**Ponto de entrada:** campo **Website** do comentário de um blog.

~~~text
javascript:alert(1)
~~~

**Efeito esperado:** o nome do autor vira link com esquema `javascript:`; ao clicar, executa script. **Correção:** allow-list de protocolos (`https:` / `http:`), parse robusto e encoding contextual. **Status: PENDENTE.**

### PS-11 — Reflected XSS em string JavaScript

**Laboratório:** [Reflected XSS into a JavaScript string with angle brackets HTML encoded](https://portswigger.net/web-security/cross-site-scripting/contexts/lab-javascript-string-angle-brackets-html-encoded)

**Ponto de entrada:** busca refletida dentro de uma string JavaScript.

~~~text
'-alert(1)-'
~~~

**Efeito esperado:** as aspas rompem o contexto da string e a expressão resultante executa `alert(1)` como efeito colateral. **Correção:** não concatenar dados em blocos JavaScript; serializar de modo seguro e mover os dados para um contexto de dados, com tratamento adequado. **Status: PENDENTE.**

## 5. Visão de Engenharia de Software — como prevenir

| Falha | Causa | Correção prioritária | Defesa complementar |
|---|---|---|---|
| SQLi | Dados interpolados em SQL | **Prepared statements** / binding real de parâmetros | Menor privilégio, allow-list de identificadores, logs de erro seguros |
| XSS HTML | Entrada exibida como HTML executável | **Encoding na saída** adequado ao contexto | Sanitização de HTML permitido e CSP |
| DOM XSS | APIs perigosas com dados de URL | Usar `textContent`, `createElement` e outras APIs seguras | Trusted Types e CSP quando aplicável |
| XSS em `href` | URL com esquema executável | Validar protocolos e destino | Bloquear `javascript:`, CSP |
| Senhas frágeis | Hash rápido/sem salt | **Argon2id**, scrypt ou bcrypt com salt | Rate limiting, MFA, monitoramento |

### 5.1 Exemplo SQL: incorreto e corrigido

~~~java
// INCORRETO: nunca construir SQL com dados não confiáveis.
String sqlVulneravel = "SELECT id FROM users WHERE email = '" + email + "'";

// CORRETO: o email é vinculado como parâmetro, e não vira código SQL.
PreparedStatement stmt = connection.prepareStatement(
    "SELECT id FROM users WHERE email = ?"
);
stmt.setString(1, email);
~~~

### 5.2 Exemplo XSS: incorreto e corrigido

~~~javascript
// INCORRETO: interpreta a entrada como HTML.
resultado.innerHTML = termoPesquisado;

// CORRETO: adiciona o conteúdo como texto.
resultado.textContent = termoPesquisado;
~~~

Se for inevitável aceitar HTML rico do usuário, **sanitize** com biblioteca consolidada (por exemplo DOMPurify) e ajuste a política CSP. Encodings são **dependentes do contexto**: texto HTML, atributo HTML, URL e JavaScript não seguem exatamente as mesmas regras.

### 5.3 Exemplo executável de prevenção em Java

O arquivo [src/InjectionPreventionDemo.java](src/InjectionPreventionDemo.java) contém duas demonstrações executáveis de defesa contra XSS (**escape de texto HTML** e **allow-list de URLs**) e um método com `PreparedStatement` para mostrar a parametrização em JDBC. O pequeno teste local em [src/InjectionPreventionDemoTest.java](src/InjectionPreventionDemoTest.java) **não substitui a execução dos 17 laboratórios externos**.

Para compilar e validar o exemplo de prevenção:

~~~bash
cd assignments/06-injection/submissions/leonardo-freitas
javac -d out src/InjectionPreventionDemo.java src/InjectionPreventionDemoTest.java
java -cp out InjectionPreventionDemoTest
~~~

## 6. Evidências, validação e entrega

1. Realizar cada desafio na instância **local/autorizada** ou no laboratório PortSwigger fornecido à sua sessão.
2. Confirmar a notificação de solução do Juice Shop ou o banner **Solved** no PortSwigger.
3. Salvar os prints conforme [EVIDENCIAS.md](EVIDENCIAS.md), preferencialmente sem dados pessoais/tokens nas imagens.
4. Atualizar o campo **Status** no checklist, usando `CONCLUÍDO` somente para cenários efetivamente verificados.
5. Enviar o **link do Pull Request** no ambiente virtual da disciplina, conforme [orientações gerais](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/README.md).

### Limitações honestas desta entrega

- O anexo de referência orientou a escolha de payloads, mas nenhum *lab* foi acessado por uma sessão autenticada ou instância local neste trabalho.
- A existência de instruções, código de mitigação e resultados esperados **não comprova** a resolução dos desafios.
- Não há capturas de tela fabricadas ou declarações de que 17/17 foram concluídos.
- O código seguro apresentado é independente dos projetos Juice Shop e PortSwigger; não modifica nenhum dos dois.

## 7. Referências

- [Enunciado I2-06 — Segurança e Teste de Software](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/06-injection/README.md)
- [OWASP Juice Shop — Companion Guide: Score Board](https://pwning.owasp-juice.shop/companion-guide/latest/part2/score-board.html)
- [OWASP Juice Shop — Companion Guide: Injection](https://pwning.owasp-juice.shop/companion-guide/latest/part2/injection.html)
- [OWASP Juice Shop — Companion Guide: XSS](https://pwning.owasp-juice.shop/companion-guide/latest/part2/xss.html)
- [PortSwigger Web Security Academy: SQL Injection](https://portswigger.net/web-security/sql-injection)
- [PortSwigger Web Security Academy: XSS](https://portswigger.net/web-security/cross-site-scripting)
- [OWASP SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html)
- [OWASP XSS Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)

**Conclusão:** SQLi acontece quando dados viram comando SQL; XSS acontece quando dados de fontes não confiáveis são interpretados como código no navegador. A prioridade do engenheiro não é memorizar payloads, mas **projetar fronteiras corretas entre dados e código** e provar isso por meio de testes verificáveis.