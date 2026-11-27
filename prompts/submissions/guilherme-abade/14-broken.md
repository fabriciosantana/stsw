# Prompt 14: Broken Access Control (A01:2021)

**Aluno:** Guilherme Abade
**Referências:** [OWASP A01:2021](https://owasp.org/Top10/A01_2021-Broken_Access_Control/) · [OWASP - Broken Access Control](https://owasp.org/www-community/Broken_Access_Control) · [PortSwigger - Access Control](https://portswigger.net/web-security/access-control)

Os códigos, o teste em Gherkin e o roteiro com o Burp Suite destinam-se a laboratório próprio ou autorizado.

## 1. Introdução

A falha de *Broken Access Control* ocorre quando a aplicação não impõe corretamente as restrições sobre o que um usuário autenticado pode fazer. Em consequência, o usuário consegue agir fora das permissões concedidas, ao ler, alterar ou apagar dados de terceiros ou ao acessar funções administrativas.

A categoria ocupa o primeiro lugar do Top 10 de 2021 porque 94% das aplicações testadas apresentaram alguma forma da falha, com mais de 318 mil ocorrências, o maior volume da pesquisa; antes em quinto lugar, ascendeu ao topo. Sua frequência explica-se pelo fato de as regras de autorização constituírem lógica de negócio dispersa pelo código, verificada com frequência de modo incompleto.

As aplicações deveriam observar alguns princípios de controle de acesso: negar por padrão, de forma que tudo seja proibido exceto o explicitamente liberado; aplicar o menor privilégio, concedendo a cada papel apenas o necessário; verificar no servidor a cada requisição, sem confiar no cliente; centralizar e reutilizar o controle, em vez de repetir regras isoladas em cada endpoint; e validar a propriedade do recurso, confirmando que o dado pertence a quem o solicita. A autenticação e a autorização não se confundem: a primeira responde quem é o usuário, enquanto a segunda define o que ele pode fazer. A *Broken Access Control* é uma falha de autorização, pois o usuário está autenticado, mas acessa o que não deveria.

Entre os exemplos mais comuns figuram a referência direta insegura a objeto, ou IDOR, em que se troca um identificador na URL para acessar o recurso de outro usuário; o contorno de controle por manipulação de URL, que alcança uma área administrativa sem passar por verificação; a elevação de privilégio por parâmetro, quando o servidor confia em um campo como `role` enviado pelo cliente; a forja de token, quando a assinatura não é verificada; e a navegação forçada a páginas autenticadas. O impacto potencial abrange a exfiltração de dados de outros usuários, a escalada de privilégio, tanto horizontal, entre usuários do mesmo nível, quanto vertical, até o perfil administrativo, o acesso a funções de administração e a alteração ou remoção de dados.

## 2. Exemplos práticos de falhas

O endpoint a seguir apresenta uma falha de IDOR, pois não valida o proprietário do recurso.

```java
@GetMapping("/api/faturas/{id}")
public Fatura verFatura(@PathVariable Long id, Principal principal) {
    return faturaRepo.findById(id).orElseThrow();   // não valida o dono
}
```

Qualquer usuário autenticado lê qualquer fatura ao trocar o identificador, o que caracteriza o IDOR, já que o endpoint usa uma referência direta ao identificador do banco sem verificar se o recurso pertence ao solicitante. A substituição de `/api/faturas/501` por `/api/faturas/502` devolve a fatura de outro cliente. A versão segura valida a propriedade e responde `404` para não revelar a existência do recurso.

```java
@GetMapping("/api/faturas/{id}")
public ResponseEntity<Fatura> verFatura(@PathVariable Long id, Principal principal) {
    Fatura f = faturaRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (!f.getDono().getLogin().equals(principal.getName())) {   // valida a propriedade
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    return ResponseEntity.ok(f);
}
```

Uma solução ainda melhor consulta o recurso já filtrando pelo proprietário, por meio de `findByIdAndDonoLogin(id, principal.getName())`, o que elimina a possibilidade de omitir a verificação. O contorno por parâmetro na URL, como em `GET /perfil?userId=2` para um usuário cujo identificador é `1`, revela outro aspecto: a aplicação deve ignorar o `userId` informado pelo cliente e derivar a identidade da sessão no servidor, de modo que a tentativa de acessar outro usuário resulte em `404` ou `403`.

## 3. Atividades guiadas com ferramentas e testes

O Burp Suite apoia a identificação de falhas de controle de acesso. O procedimento inicia com o login de dois usuários, um administrador e um comum, e a captura de suas sessões. A seguir, repete-se uma requisição do administrador utilizando o token do usuário comum, no Repeater; se funcionar, há falha vertical. Para o IDOR, varia-se o identificador no Intruder e observam-se respostas de sucesso com dados de terceiros. O Comparer, por sua vez, confronta a resposta autorizada do administrador com a não autorizada do usuário comum, revelando se o conteúdo sensível vazou mesmo quando o código de status parece idêntico.

Entre os métodos para testar permissões estão a troca de token entre papéis, a remoção do token, a alteração do campo de papel e dos identificadores, o teste de métodos HTTP alternativos e a navegação forçada a URLs administrativas. Ao simular o perfil comum contra endpoints de administração, registra-se o que respondeu `200`, indicando acesso indevido, e o que respondeu `403`, indicando o comportamento correto; em uma aplicação segura, todas as tentativas do usuário comum em rotas administrativas retornam `403` ou `404`. O teste a seguir, em Gherkin, valida que um usuário não acessa dados de outro.

```gherkin
Funcionalidade: Controle de acesso a faturas
  Para proteger dados financeiros
  Como sistema
  Quero impedir que um usuário acesse faturas de outro

  Cenário: usuário não acessa fatura de outro usuário
    Dado que o usuário "ana" está autenticado
    E existe a fatura 502 pertencente ao usuário "bruno"
    Quando "ana" faz GET em "/api/faturas/502"
    Então o status da resposta deve ser 404
    E o corpo não deve conter dados da fatura 502

  Cenário: usuário comum não acessa área administrativa
    Dado que o usuário "ana" tem papel "COMUM"
    Quando "ana" faz GET em "/admin/usuarios"
    Então o status da resposta deve ser 403
```

## 4. Aplicação em cenários reais

Um caso ilustrativo é o do sistema Informed Visibility do serviço postal dos Estados Unidos, em 2018, em que uma API permitia a qualquer usuário autenticado consultar e alterar dados de conta de cerca de 60 milhões de usuários mediante a simples variação de parâmetros, por ausência de verificação de autorização. A falha decorreu de lógica mal implementada e da falta de validação de propriedade, e não de configuração de servidor.

A estratégia de defesa em camadas combina a negação por padrão no gateway ou no framework, a autorização centralizada em vez de checagens isoladas, a validação da propriedade do recurso no servidor por meio de consultas filtradas pelo dono, o uso de identificadores não previsíveis como defesa adicional, que não dispensa a verificação, os testes automatizados de autorização no pipeline e o registro com alertas de acesso negado e de padrões anômalos. No que toca às práticas de desenvolvimento, o frontend oculta botões por conveniência, mas toda decisão efetiva cabe ao backend, que nunca deve confiar em campos do cliente para determinar o papel ou a identidade; a revisão de código focada em autorização e a limitação de taxa contra a enumeração completam o conjunto.

## 5. Conclusão

A *Broken Access Control* é a falha de autorização pela qual um usuário faz o que não deveria, e se identifica ao testar cada endpoint com usuários de papéis distintos, ao variar identificadores e parâmetros e ao verificar se o servidor valida tanto a identidade quanto a propriedade. Entre os sinais de vulnerabilidade estão a autorização verificada apenas no frontend, os identificadores sequenciais expostos, os endpoints que aceitam o papel ou o identificador vindos do cliente, a ausência de verificação de proprietário nas consultas e a resposta idêntica para recursos próprios e de terceiros.

A dificuldade de garantir o controle em sistemas grandes decorre de a autorização ser uma lógica de negócio transversal, espalhada por muitos endpoints, papéis e regras contextuais relativas ao dono, à organização e ao estado do recurso. Basta um único ponto sem verificação para abrir a brecha, e cada nova funcionalidade acrescenta superfícies. Por essa razão, recomenda-se centralizar o controle de acesso, negar por padrão e cobrir a autorização com testes automatizados, em lugar de confiar na disciplina de verificar manualmente cada rota.
