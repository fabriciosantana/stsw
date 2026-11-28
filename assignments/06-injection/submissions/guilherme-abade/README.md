# Atividade 06: Laboratórios de Injection

**Aluno:** Guilherme Abade

Esta atividade resolve os desafios de injeção propostos no OWASP Juice Shop e nos laboratórios do PortSwigger Web Security Academy. Os ambientes são deliberadamente vulneráveis e destinam-se ao estudo. O Juice Shop foi executado localmente por `docker run --rm -p 3000:3000 bkimminich/juice-shop`, e os laboratórios do PortSwigger foram resolvidos na plataforma oficial. Cada desafio é apresentado com a falha explorada, o procedimento de resolução e a correção correspondente.

## OWASP Juice Shop

### Finding the Score Board

O painel de pontuação não aparece no menu, mas a rota existe no código do cliente. A solução consiste em inspecionar os arquivos JavaScript da aplicação, nos quais o caminho `/#/score-board` está declarado, e acessá-lo diretamente pela barra de endereços. A correção adequada seria não depender da ocultação da rota como mecanismo de controle, mas aplicar autorização efetiva no servidor para recursos restritos.

### SQL Injection: Login Admin

O formulário de login monta a consulta por concatenação da entrada do usuário. Ao informar no campo de e-mail o valor `' OR 1=1 --`, a cláusula de filtro torna-se sempre verdadeira e a verificação de senha é comentada, de modo que a aplicação autentica o primeiro usuário da tabela, que é o administrador. A correção consiste em substituir a concatenação por consultas parametrizadas e em verificar a senha por meio de hash com sal.

### SQL Injection: User Credentials

A mesma falha no caminho de autenticação ou em um campo de busca vulnerável permite empregar a técnica `UNION SELECT` para recuperar colunas da tabela de usuários, entre elas o e-mail e o hash da senha. A solução monta uma consulta que iguala o número de colunas do resultado original e projeta os dados sensíveis. A correção é a parametrização das consultas e a restrição de privilégios do usuário do banco, somadas ao armazenamento de senhas com algoritmo de hash adequado.

### Cross-Site Scripting: DOM XSS

O campo de busca reflete o termo pesquisado diretamente no DOM, sem sanitização. A solução insere, no parâmetro de busca, uma marcação capaz de executar script, como uma tag `iframe` com origem em `javascript:`, que é interpretada pelo navegador. A correção aplica a codificação da saída e a sanitização do conteúdo antes de inseri-lo no DOM, com uma política de segurança de conteúdo.

### Cross-Site Scripting: Reflected XSS

Um parâmetro refletido em uma página de acompanhamento, como o identificador de pedido, não é tratado antes de ser exibido. A solução fornece nesse parâmetro uma carga de script que o navegador executa ao renderizar a resposta. A correção codifica a saída no contexto HTML e valida a entrada esperada para o parâmetro.

### Cross-Site Scripting: Bonus Payload

O desafio solicita o uso de uma carga específica que executa sem causar dano, apenas para demonstrar a falha de reflexão. A solução emprega essa carga no mesmo ponto de injeção do XSS refletido. A correção permanece a codificação da saída, independentemente da carga utilizada, uma vez que o problema está na ausência de tratamento, e não em uma carga específica.

## Laboratórios do PortSwigger

### SQL injection in WHERE clause allowing retrieval of hidden data

A listagem de produtos filtra por categoria e por um indicador de visibilidade. Ao injetar, no parâmetro de categoria, uma condição sempre verdadeira seguida de comentário, a cláusula que oculta produtos é anulada e todos os registros são exibidos. A correção consiste em parametrizar a consulta e em aplicar o controle de visibilidade no próprio servidor, sem depender de filtros embutidos na string.

### SQL injection vulnerability allowing login bypass

O formulário de login concatena o nome de usuário na consulta. Ao informar um usuário conhecido seguido do comentário que descarta a verificação de senha, a autenticação é concedida sem a credencial correta. A correção emprega consultas parametrizadas e a verificação da senha por hash, além de mensagens de erro genéricas.

### Reflected XSS into HTML context with nothing encoded

Um parâmetro de busca é refletido no corpo da página sem qualquer codificação. A solução fornece uma marcação de script que o navegador executa. A correção codifica a saída no contexto HTML.

### Stored XSS into HTML context with nothing encoded

Um campo de comentário armazena e exibe o conteúdo sem tratamento, de modo que a carga persiste e é executada a cada visualização por outros usuários. A solução registra um comentário contendo a marcação de script. A correção codifica a saída e valida a entrada no momento da gravação.

### DOM XSS in document.write sink using source location.search

O JavaScript da página grava, por meio de `document.write`, um valor obtido de `location.search`. A solução fornece, no parâmetro da URL, um conteúdo que fecha o contexto atual e introduz um elemento com manipulador de evento. A correção evita o uso de `document.write` com dados controláveis e recorre a métodos seguros de manipulação do DOM.

### DOM XSS in innerHTML sink using source location.search

O valor de `location.search` é atribuído à propriedade `innerHTML` de um elemento, o que interpreta a marcação fornecida. A solução injeta um elemento com manipulador de evento, como uma imagem com origem inválida. A correção utiliza `textContent` em lugar de `innerHTML` ou sanitiza o conteúdo antes da inserção.

### DOM XSS in jQuery anchor href attribute sink using location.search source

Um script preenche o atributo `href` de uma âncora com um valor vindo de `location.search`. A solução fornece um valor com o esquema `javascript:`, executado ao clique. A correção valida o esquema da URL e rejeita esquemas perigosos.

### DOM XSS in jQuery selector sink using a hashchange event

Um seletor do jQuery utiliza o conteúdo do fragmento da URL em um evento de alteração de hash, o que permite introduzir marcação. A solução constrói uma URL cujo fragmento gera o elemento executável. A correção trata o fragmento como dado e evita usá-lo na construção de seletores ou de marcação.

### Reflected XSS into attribute with angle brackets HTML-encoded

Os sinais de menor e maior são codificados, mas a entrada é refletida dentro de um atributo, o que permite escapar do atributo sem esses caracteres. A solução encerra o atributo e acrescenta um manipulador de evento. A correção codifica também as aspas e os demais caracteres significativos no contexto de atributo.

### Stored XSS into anchor href attribute with double quotes HTML-encoded

A entrada é armazenada no atributo `href` de uma âncora, com as aspas duplas codificadas, mas o esquema do valor não é validado. A solução grava um valor com o esquema `javascript:`. A correção valida o esquema permitido para a URL.

### Reflected XSS into a JavaScript string with angle brackets HTML encoded

A entrada é refletida dentro de uma string em JavaScript, com os sinais de menor e maior codificados, o que ainda permite encerrar a string e introduzir código. A solução fecha a string e acrescenta uma instrução. A correção codifica a saída no contexto de JavaScript, incluindo aspas e barras, ou evita inserir dados do usuário em código.

## Síntese

Os desafios desta atividade demonstram que as injeções de SQL e de script compartilham a mesma causa, que é a mistura de dados do usuário com um comando ou com a marcação da página. A defesa primária contra a injeção SQL é a consulta parametrizada, e a defesa contra o XSS é a codificação da saída no contexto correto, acompanhada de sanitização e de uma política de segurança de conteúdo. A filtragem de entrada por lista de negação mostrou-se insuficiente em vários laboratórios, nos quais uma variação da carga contornou o filtro, o que confirma que a correção adequada trata a entrada como dado, e não como instrução.
