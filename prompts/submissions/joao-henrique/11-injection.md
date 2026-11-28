# Prompt 11: OWASP Top Ten: Injection (A03:2021)

Referências: [OWASP A03:2021](https://owasp.org/Top10/A03_2021-Injection/), [WSTG – SQL Injection](https://owasp.org/www-project-web-security-testing-guide/latest/4-Web_Application_Security_Testing/07-Input_Validation_Testing/05-Testing_for_SQL_Injection), [sqlmap](https://sqlmap.org), [XSStrike](https://github.com/s0md3v/XSStrike)

> Nota: os comandos das ferramentas (sqlmap e XSStrike) mostrados abaixo devem ser executados somente em ambientes de laboratório próprios (Juice Shop, DVWA, WebGoat). Os trechos de código são exemplos didáticos.

## 1. Introdução à vulnerabilidade

Injection ocorre quando dados não confiáveis do usuário são enviados a um interpretador (SQL, shell, LDAP etc.) como parte de um comando ou consulta, e o interpretador não consegue separar o que é código do que é dado.

- **Por que continua no Top 10:** a causa (concatenar entrada em comandos) é um erro de programação muito comum, aparece em código legado, em ORMs usados com consultas nativas e em novos interpretadores (NoSQL, GraphQL, templates). Em 2021 foi para a 3ª posição (junto com o XSS) e 94% das aplicações testadas tinham alguma forma de injeção.
- **"Dados interpretados como comandos":** se o código monta `"... WHERE nome = '" + entrada + "'"` e a entrada é `' OR 1=1 --`, o `'` fecha a string e o resto vira parte da instrução SQL. A entrada deixou de ser dado e passou a ser código.
- **Impactos:** exfiltração de dados (dump de tabelas), alteração/destruição de dados, bypass de autenticação, negação de serviço e, em casos graves, execução remota de comandos no servidor.
- **Tipos:** SQL, NoSQL, LDAP, OS command, XPath, ORM/HQL, expression language (EL/SpEL), template injection (SSTI), e também XSS (que é injeção de script no navegador).

**Ciclo de um ataque de SQL Injection:**

```
Atacante          Aplicação                    Banco de dados
   | entrada maliciosa |                              |
   |  ' OR 1=1 --  --> | monta SQL por concatenação   |
   |                   | SELECT * FROM users          |
   |                   |  WHERE nome='' OR 1=1 --'  ->| executa
   |                   | <------ todas as linhas ---- |
   | <-- dados / login sem senha                      |
```

## 2. Entendendo a injeção SQL

- **Como a entrada afeta a consulta:** quando é concatenada, o atacante altera a estrutura (condições, UNION, comentários) e não só o valor.
- **Vetores de entrada testáveis:** parâmetros de GET e POST, campos de formulário, cabeçalhos (`User-Agent`, `Referer`, `X-Forwarded-For`), cookies, corpo JSON e parâmetros de rota.
- **Tipos de ataque:**
  - *Tautológico:* torna a condição sempre verdadeira (`' OR '1'='1`).
  - *Piggy-backed (consultas empilhadas):* anexa outro comando (`; DROP TABLE users --`).
  - *Blind (cego):* a página não mostra o resultado; o atacante deduz verdadeiro/falso pela resposta (boolean-based).
  - *Time-based:* usa atrasos (`SLEEP(5)`) para inferir informação.
  - *UNION/error-based:* usa `UNION SELECT` ou mensagens de erro do banco para trazer dados.

Código vulnerável em Java (concatenação com `Statement`):

```java
String sql = "SELECT * FROM usuarios WHERE login = '" + login
           + "' AND senha = '" + senha + "'";
Statement st = conexao.createStatement();
ResultSet rs = st.executeQuery(sql);
```

Com a entrada `' OR 1=1 --` no campo login, a consulta vira:

```sql
SELECT * FROM usuarios WHERE login = '' OR 1=1 --' AND senha = ''
```

`OR 1=1` é sempre verdadeiro e `--` comenta o resto: a consulta devolve todos os usuários e o login é aceito sem senha (normalmente como o primeiro usuário, geralmente o admin).

## 3. Prevenção com PreparedStatement

- **Por que funciona:** a consulta é enviada ao banco com marcadores `?` e compilada antes; os valores são enviados depois, separados, como dados. Mesmo que o valor contenha `'` ou `OR`, ele nunca é interpretado como SQL.
- **Validação x parametrização:** validar é uma camada extra (restringe formato/tamanho/tipo), mas listas de bloqueio falham. A parametrização resolve a causa raiz. Devem ser usadas juntas.
- **ORM elimina o risco?** Não totalmente. Se o código usa consultas nativas ou JPQL/HQL com concatenação (`"from User where name='" + n + "'"`), continua vulnerável. O ORM só protege quando usa parâmetros nomeados.

Login seguro:

```java
String sql = "SELECT id FROM usuarios WHERE login = ? AND senha_hash = ?";
try (PreparedStatement ps = conexao.prepareStatement(sql)) {
    ps.setString(1, login);
    ps.setString(2, hash(senha));
    try (ResultSet rs = ps.executeQuery()) {
        return rs.next();
    }
}
```

**Simulação:** com `Statement`, a entrada `' OR 1=1 --` devolve linhas e o login passa. Com `PreparedStatement`, a mesma entrada é procurada literalmente como o login `' OR 1=1 --`, não existe, e o resultado é vazio (login negado).

## 4. Técnicas de teste para SQL Injection

- **Sinais:** erro de sintaxe SQL na resposta, status 500, mudança de comportamento ao inserir `'` ou `"`, diferença de resposta entre `AND 1=1` e `AND 1=2`, atraso quando se injeta `SLEEP`.
- **Técnicas:** tautologias (`' OR 'a'='a`), `UNION SELECT` (descobrir o número de colunas com `ORDER BY n` ou `UNION SELECT null,null`), time-based (`' OR SLEEP(5) --`).
- **Ferramentas:** sqlmap, Burp Suite (Repeater/Intruder/Scanner), OWASP ZAP.

Rota vulnerável (buscar usuário por ID):

```java
@GetMapping("/usuario")
public String buscar(@RequestParam String id) throws SQLException {
    String sql = "SELECT nome FROM usuarios WHERE id = " + id; // vulnerável
    ResultSet rs = conexao.createStatement().executeQuery(sql);
    return rs.next() ? rs.getString("nome") : "não encontrado";
}
```

Entradas de teste e o que se espera:

| Entrada | Efeito |
|---|---|
| `1` | Retorna o usuário 1 |
| `1 OR 1=1` | Retorna o primeiro usuário da tabela (a condição vale para todos) |
| `' OR 'a'='a` | Quebra a sintaxe numérica ou vira tautologia, conforme o tipo da coluna |
| `1 UNION SELECT version()` | Mostra a versão do banco (precisa bater o número de colunas) |

Correção:

```java
PreparedStatement ps = conexao.prepareStatement("SELECT nome FROM usuarios WHERE id = ?");
ps.setInt(1, Integer.parseInt(id)); // também valida o tipo
```

Depois da correção as mesmas entradas deixam de funcionar (`Integer.parseInt` rejeita o que não for número e o parâmetro nunca vira SQL).

## 5. Testando com sqlmap

- **Detecção automática:** o sqlmap injeta payloads de cada técnica nos parâmetros, compara as respostas (conteúdo, código, tempo) com a resposta original e conclui se o parâmetro é injetável e qual o banco usado.
- **Técnicas:** *boolean-based* (compara respostas para condições verdadeiras e falsas), *error-based* (extrai dados de mensagens de erro), *time-based* (mede atraso), além de UNION query e stacked queries (`--technique=BEUSTQ`).
- **Parâmetros:** `-u` (URL), `--data` (corpo POST), `--cookie`, `-H` (cabeçalhos), `-p` (parâmetro específico), `--batch` (respostas padrão).
- **Fluxo no laboratório:** subir o endpoint vulnerável do item 4, rodar `sqlmap -u "http://localhost:8080/usuario?id=1" --technique=B` e depois `--technique=T`, e comparar. Após trocar por `PreparedStatement` o sqlmap reporta que os parâmetros não parecem injetáveis.

## 6. Casos reais e impactos

**Caso: TalkTalk (Reino Unido, 2015).**
- *Vetor:* SQL injection em páginas web antigas e sem manutenção (herdadas de uma aquisição), exploradas com uma ferramenta automatizada (sqlmap).
- *Dados expostos:* dados pessoais de cerca de 157 mil clientes e informações bancárias de milhares.
- *Medidas ausentes:* inventário e atualização de sistemas legados, revisão de código/SAST, testes de segurança, consultas parametrizadas e monitoramento.
- *Resposta e impacto:* multa de £400 mil do ICO, perda de dezenas de milhares de clientes, custo estimado em dezenas de milhões de libras e dano forte à reputação (a empresa também demorou a comunicar os clientes).
- *Prevenção segundo a OWASP:* consultas parametrizadas, validação de entrada (allow list), privilégio mínimo no banco e escaping como último recurso.

## 7. Boas práticas e checklist OWASP

- [ ] Usar sempre `PreparedStatement` ou ORM com parâmetros nomeados.
- [ ] Validar a entrada com allow list (tipo, tamanho, formato).
- [ ] Banco com usuário de privilégio mínimo (sem `DROP`, sem acesso a outras tabelas).
- [ ] Não expor mensagens de erro do banco ao usuário.
- [ ] Testar com SAST/DAST e revisão de código.

```java
// 1) PreparedStatement
PreparedStatement ps = c.prepareStatement("SELECT * FROM produtos WHERE id = ?");
ps.setInt(1, id);

// 2) Validação (Regex)
if (!login.matches("^[a-zA-Z0-9_]{3,20}$")) {
    throw new IllegalArgumentException("login inválido");
}

// 3) Erro sem expor estrutura
try {
    return repo.buscar(id);
} catch (SQLException e) {
    log.error("falha ao buscar produto", e);                   // detalhe só no log
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno");
}
```

## 8. SQLMap — testes automatizados

- **O que é:** ferramenta open source em Python que detecta e explora SQL injection automaticamente, e pode assumir o banco.
- **Tipos de teste:** boolean-based blind, time-based blind, error-based, UNION query, stacked queries e out-of-band.
- **Diferença para outras ferramentas:** é especializada em SQLi (identifica o SGBD, extrai dados, lê arquivos, executa comandos), enquanto scanners genéricos cobrem várias classes de falhas com menos profundidade.
- **Comando:** `sqlmap -u "http://exemplo.com/item?id=1"`: `-u` indica a URL alvo; o sqlmap testa o parâmetro `id` e relata se é injetável.
- **Parâmetros importantes:** `--level` (1–5, quantos pontos/cabeçalhos testar), `--risk` (1–3, agressividade dos payloads), `--batch` (não pergunta), `--dbs`, `--dump`, `--technique`, `--tamper`, `--threads`.

## 9. Detecção de vulnerabilidades com sqlmap

- **Como testa:** envia variações e compara. Boolean: `AND 1=1` x `AND 1=2`; error: provoca erro e lê a mensagem; time: `SLEEP(5)` e mede o tempo da resposta.
- **Sinais analisados:** diferença de conteúdo/tamanho, código de status, mensagens de erro do SGBD e tempo de resposta.
- **`--level` e `--risk`:** `--level` aumenta a quantidade de pontos testados (cookies, headers, mais payloads); `--risk` permite payloads mais perigosos (`OR`-based podem alterar dados num UPDATE). O comando `--risk=3 --level=5` é o mais exaustivo e mais barulhento.
- **Endpoint:** o do item 4 em Spring Boot com JDBC; comando `sqlmap -u "http://localhost:8080/produto?id=2" --batch --risk=3 --level=5`.
- **Correção:** mesma do item 3 (`PreparedStatement` com `setInt`), depois rodar de novo e confirmar que não há injeção.

## 10. Extração de dados com sqlmap

- **Listar:** `--dbs` (bancos), `-D banco --tables` (tabelas), `-D banco -T tabela --columns` (colunas).
- **Extrair:** `--dump` baixa as linhas. Pode quebrar hashes de senha com dicionário.

```bash
sqlmap -u "<URL>" --dbs
sqlmap -u "<URL>" -D banco --tables
sqlmap -u "<URL>" -D banco -T tabela --dump
```

- **Risco organizacional:** se a conta do banco da aplicação tem privilégio excessivo (DBA, acesso a todos os schemas, `FILE`), uma única injeção permite ler tudo, escrever arquivos e até executar comandos. Daí a importância do privilégio mínimo.

## 11. Proteções, bypasses e evasão

- **`--tamper`:** scripts que transformam o payload antes do envio (trocar espaços por comentários, variar maiúsculas/minúsculas, codificar) para passar por filtros e WAFs.
- **Técnicas contra filtros simples:** variar caixa (`SeLeCt`), comentários inline (`UN/**/ION`), codificação URL/dupla, `BETWEEN` no lugar de `>`.
- **Ataque direto x com evasão:** o direto envia o payload padrão e é barrado por regras simples; com evasão o payload é alterado para ter o mesmo efeito, mas não casar com a assinatura do filtro.
- **Uso:** `sqlmap -u "<URL>" --tamper=between,randomcase`. Comparando as respostas, sem tamper o filtro devolve bloqueio (403/erro) e com tamper o payload chega ao banco. A lição: filtros e WAFs são camada de defesa, não correção; a correção é a parametrização.

## 12. Autenticação, cookies e sessões

- **`--cookie`:** envia o cookie da sessão para testar páginas que exigem login; `-H` envia cabeçalhos personalizados (ex.: `Authorization: Bearer ...`).
- **Formulários:** `--data="usuario=admin&senha=123"` testa os parâmetros do POST; é possível combinar `--data` com `--cookie`.

```bash
sqlmap -u "http://localhost:8080/api/pedidos?id=1" --cookie="token=<JWT>" --batch
sqlmap -u "http://localhost:8080/login" --data="usuario=admin&senha=123" --batch
```

## 13. Integração com desenvolvimento seguro

- **Testes automatizados:** rodar o sqlmap contra endpoints de um ambiente de homologação (nunca produção) como etapa de DAST na pipeline.
- **CI/CD:** a pipeline executa o sqlmap com `--batch` e falha o build (e alerta no chat/e-mail) se houver parâmetro injetável.
- **Cuidados éticos e legais:** só testar sistemas próprios ou com autorização por escrito; evitar `--dump` em dados reais; respeitar o escopo e a LGPD.
- **Rotina para endpoints internos:** ambiente de homologação com dados fictícios, lista de endpoints versionada no repositório, execução noturna e relatório.

```bash
#!/usr/bin/env bash
# varredura periódica (executar só em homologação)
set -euo pipefail
while read -r url; do
  sqlmap -u "$url" --batch --level=2 --risk=1 --output-dir=./relatorios \
    | tee -a varredura.log
done < endpoints.txt
grep -q "is vulnerable" varredura.log && { echo "SQLi encontrada"; exit 1; } || exit 0
```

## 14. XSStrike — introdução

- **O que é:** scanner de XSS em Python que analisa a resposta e gera payloads de acordo com o contexto onde a entrada é refletida.
- **Diferença para XSSer e Burp Scanner:** em vez de só enviar listas fixas, usa análise do contexto (dentro de atributo, de script, de tag) e heurísticas; o Burp é um scanner geral e comercial, e o XSSer usa mais força bruta de payloads.
- **Tipos:** refletido e DOM (com análise de JavaScript); o XSS armazenado é mais difícil de detectar de forma automática porque exige verificar outra página.
- **Execução:** `python3 xsstrike.py -u "http://example.com?q=teste"`. O menu/opções incluem `--crawl`, `--params`, `--fuzzer`, `--skip-dom`, `--headers`, `--data`.

## 15. Funcionamento interno e heurísticas

- **Fuzzing inteligente:** primeiro envia um valor-sentinela e vê onde ele aparece e quais caracteres (`<`, `>`, `"`, `'`) sobrevivem; depois gera só os payloads que funcionariam naquele contexto.
- **Payloads baseados em contexto:** se a entrada cai dentro de um atributo entre aspas, o payload fecha a aspa; se cai em JavaScript, fecha a string; se cai no HTML, abre uma tag.
- **Análise de JavaScript para DOM XSS:** o XSStrike procura fontes (`location.hash`, `location.search`) e sinks (`innerHTML`, `document.write`, `eval`) no JS da página.
- **Uso:** `python3 xsstrike.py -u "http://localhost:8080/pesquisa?q=teste"`; as opções `--params` e `--crawl` descobrem parâmetros e páginas adicionais.

## 16. XSS refletido

- **O que é:** o valor enviado na requisição é devolvido na resposta sem tratamento, e o navegador o executa como script. Precisa que a vítima abra um link malicioso.
- **Efeitos:** roubo de cookies/sessão (se não houver HttpOnly), ações em nome do usuário, redirecionamento, phishing dentro do próprio site.
- **Automação:** o XSStrike confirma que o valor é refletido, identifica o contexto, gera payloads e confere na resposta se ele sobreviveu intacto.
- **Aplicação simulada:**

```java
@GetMapping("/pesquisa")
@ResponseBody
public String pesquisa(@RequestParam String q) {
    return "<h1>Resultados para: " + q + "</h1>";   // vulnerável: sem escape
}
```

Payload esperado como vulnerável: `<script>alert(1)</script>` ou `<svg onload=alert(1)>`. Correção: escapar na saída (`HtmlUtils.htmlEscape(q)` ou usar Thymeleaf com `th:text`) e definir `Content-Security-Policy`.

## 17. DOM-based XSS

- **Diferença:** a falha está no JavaScript do cliente: o servidor pode nem refletir o valor. A fonte (URL, hash) chega a um sink perigoso no navegador.
- **Análise:** estática (lê o JS procurando fonte→sink) e dinâmica (executa a página e observa se o payload alcança o sink).
- **Falhas típicas:** `innerHTML`, `document.write`, `eval`, `setTimeout(string)`, jQuery `$(location.hash)`.

```html
<script>document.write(location.hash.substring(1));</script>
```

Teste: `python3 xsstrike.py -u "http://localhost/teste.html#xss"`. Para o payload `#<img src=x onerror=alert(1)>` o navegador executa o script. Correção: usar `textContent` em vez de `innerHTML`/`document.write` e sanitizar com uma biblioteca como DOMPurify.

## 18. Crawling e enumeração de parâmetros

- **`--crawl`:** o XSStrike segue os links a partir da URL inicial e testa cada formulário/parâmetro encontrado, descobrindo múltiplas URLs exploráveis.
- **`--params`:** procura parâmetros ocultos (que a página não mostra) testando uma lista de nomes comuns e vendo se a resposta muda.
- **Uso:** `python3 xsstrike.py -u "http://localhost:8080" --crawl` e, para uma página específica, `python3 xsstrike.py -u "http://localhost:8080/pagina" --params`.

## 19. Bypass de filtros e WAFs

- **Payloads polimórficos:** o mesmo efeito escrito de várias formas (`<ScRiPt>`, `<svg/onload=...>`, entidades HTML, codificação), de modo que nenhuma assinatura fixa os pegue.
- **Como a ferramenta usa:** `--fuzzer` envia muitas variações e registra quais passam pelo filtro; ela monta o payload que sobrevive.
- **Por que funciona contra filtros mal configurados:** filtros por lista de bloqueio (ex.: remover `<script>`) deixam passar outras tags e eventos (`onerror`, `onload`).
- **Teste:** `python3 xsstrike.py -u "http://localhost:8080/pesquisa?q=teste" --fuzzer` em um campo com filtragem simples; o resultado mostra quais caracteres/tags passam. A defesa real é o escape de saída conforme o contexto e a CSP, não o filtro.

## 20. Uso ético e responsável

- **Contextos permitidos:** laboratórios próprios, ambientes de treino (DVWA, WebGoat, bWAPP, Juice Shop), pentest com contrato/autorização escrita e programas de bug bounty dentro do escopo.
- **Práticas a evitar:** testar sites de terceiros sem autorização (crime pela Lei 12.737/2012, "Carolina Dieckmann"), acessar ou divulgar dados reais, causar indisponibilidade, usar em produção sem janela combinada.
- **Ambiente controlado:** subir o DVWA/WebGoat/Juice Shop em Docker na máquina local, sem exposição à internet, executar a ferramenta, anotar as vulnerabilidades encontradas, corrigir no código e repetir o teste.

```bash
docker run --rm -p 8081:80 vulnerables/web-dvwa     # DVWA local
python3 xsstrike.py -u "http://localhost:8081/vulnerabilities/xss_r/?name=teste" --headers "Cookie: PHPSESSID=...; security=low"
```
