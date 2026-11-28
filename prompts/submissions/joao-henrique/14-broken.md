# Prompt 14: OWASP Top Ten: Broken Access Control (A01:2021)

Referências: [OWASP A01:2021](https://owasp.org/Top10/A01_2021-Broken_Access_Control/), [OWASP Broken Access Control](https://owasp.org/www-community/Broken_Access_Control), [PortSwigger – Access control](https://portswigger.net/web-security/access-control)

## 1. Introdução

Broken Access Control é a falha em fazer valer as restrições sobre o que cada usuário pode fazer: usuários acessam dados ou funções fora das suas permissões.

- **Por que é #1 em 2021:** foi encontrada em 94% das aplicações testadas (maior incidência, mais de 318 mil ocorrências), subindo da 5ª posição em 2017. É um erro fácil de cometer porque o controle é lógica de negócio, que cada endpoint precisa implementar e não é resolvida só por framework ou scanner.
- **Princípios que devem ser seguidos:** negar por padrão (deny by default), privilégio mínimo, verificar a autorização no servidor em toda requisição (nunca confiar no cliente/UI), controle centralizado e reutilizado, checar a propriedade dos registros (ownership), registrar tentativas negadas, expirar/invalidar tokens.
- **Autenticação x autorização:** autenticação confirma *quem* é o usuário; autorização decide *o que* ele pode fazer. Estar logado não significa poder acessar qualquer coisa.
- **Exemplos comuns de falhas:**
  1. **IDOR:** `GET /api/pedidos/1002`; trocar o ID dá o pedido de outro usuário.
  2. **Bypass de ACL por manipulação de URL:** acessar `/admin` diretamente, mesmo sem o link na interface (*forced browsing*), ou alterar parâmetros (`?admin=true`, `role=admin`).
  3. **Forja/alteração de token:** editar o JWT (`alg: none`, mudar `role`) ou cookie para ganhar privilégios.
  4. **Escalada de privilégios:** usuário comum faz ações de admin (por exemplo, `PUT /api/usuarios/me` com `"role":"admin"` — mass assignment), ou ação sem login ser tratada como se fosse feita por um usuário.
  5. **Métodos HTTP não restritos:** a página bloqueia o GET mas o POST/PUT/DELETE no mesmo recurso está aberto.
  6. **CORS mal configurado:** permitir origens não confiáveis com credenciais.
- **Impacto:** vazamento de dados de outros usuários, alteração ou exclusão de dados, escalada de privilégios, acesso a funções administrativas e tomada de controle total do sistema.

## 2. Exemplos práticos

API vulnerável a IDOR (Java/Spring):

```java
@GetMapping("/api/pedidos/{id}")
public Pedido buscar(@PathVariable Long id) {
    return pedidoRepo.findById(id).orElseThrow();   // não verifica quem é o dono
}
```

- **Onde está a falha:** o endpoint só recebe o ID e devolve o registro, sem comparar o dono do pedido com o usuário autenticado. Qualquer usuário logado (ou até não logado) acessa dados sensíveis trocando o ID.
- **É IDOR?** Sim. É uma referência direta a um objeto (o ID do pedido) exposta ao cliente sem verificação de autorização no servidor.
- **Versão segura:**

```java
@GetMapping("/api/pedidos/{id}")
public Pedido buscar(@PathVariable Long id, Authentication auth) {
    Pedido p = pedidoRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    if (!p.getUsuarioLogin().equals(auth.getName()) && !admin) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);   // ou 404, para não revelar a existência
    }
    return p;
}
```

Melhor ainda: filtrar na consulta (`findByIdAndUsuarioLogin(id, auth.getName())`), de modo que o registro de outro usuário nunca seja carregado. Também usar IDs não sequenciais (UUID) ajuda, mas não substitui a verificação.

- **Simulação de bypass:** logado como `userId=1`, o atacante troca a requisição `GET /api/usuarios/perfil?userId=1` por `userId=2`.
  - *Comportamento inseguro:* a aplicação devolve o perfil do usuário 2.
  - *Comportamento esperado:* a aplicação ignora o parâmetro e usa o usuário da sessão, ou responde `403 Forbidden` (e registra a tentativa no log de segurança).

## 3. Atividades guiadas com ferramentas

- **Burp Suite para identificar falhas de controle de acesso:** capturar as requisições feitas por um usuário e reenviar com outra identidade (cookie/token de outro usuário ou sem autenticação), observando se a resposta muda.
- **Métodos de teste de permissões:** (1) repetir requisições de um usuário privilegiado com a sessão de um usuário comum; (2) trocar IDs e parâmetros (IDOR); (3) forced browsing em URLs administrativas; (4) alterar o método HTTP; (5) testar cabeçalhos como `X-Original-URL`; (6) extensões do Burp como Autorize, que repete todas as requisições com outra sessão e aponta as diferenças.
- **Burp Comparer:** compara byte a byte (ou por palavras) duas respostas, por exemplo a do admin e a do usuário comum no mesmo endpoint. Se forem iguais, o acesso não está sendo bloqueado.
- **Experimento com dois usuários (laboratório):**
  - Usuário `admin` (admin) e `ana` (comum). Logar como `ana` e chamar `GET /admin/usuarios`, `DELETE /api/usuarios/5` e `GET /api/pedidos/<id do admin>`.
  - Registrar requisição e resposta de cada uma. Resultado esperado numa aplicação segura: todas `403`. Numa vulnerável, as respostas `200` com dados indicam o que foi possível acessar sem autorização.
- **Teste automatizado em Gherkin:**

```gherkin
# language: pt
Funcionalidade: Isolamento de dados entre usuários

  Cenário: Usuário não acessa o pedido de outro usuário
    Dado que o usuário "ana" está autenticada
    E o pedido 1002 pertence ao usuário "bruno"
    Quando "ana" faz GET em "/api/pedidos/1002"
    Então o status da resposta deve ser 403
    E o corpo da resposta não deve conter dados do pedido

  Cenário: Usuário comum não acessa área administrativa
    Dado que o usuário "ana" tem o perfil "USER"
    Quando "ana" faz GET em "/admin/usuarios"
    Então o status da resposta deve ser 403
```

## 4. Aplicação em cenários reais

- **Incidente: Optus (Austrália, 2022).** Uma API pública, sem autenticação, devolvia dados de clientes a partir de um identificador sequencial. O atacante apenas percorreu os IDs e coletou dados de cerca de 9,8 milhões de clientes (nomes, datas de nascimento, endereços, números de passaporte e carteira de motorista).
- **Natureza da falha:** ausência de validação de autorização/autenticação (a API não verificava quem pedia e usava IDs previsíveis), ou seja, é uma combinação de IDOR e API exposta por configuração.
- **Impacto:** exposição massiva de dados pessoais, risco de fraude de identidade, necessidade de trocar documentos de milhões de pessoas, processos e multas, e danos à reputação.
- **Estratégia de defesa em camadas:**
  1. *Design:* modelagem de ameaças e matriz de permissões; negar por padrão.
  2. *Aplicação:* controle de acesso centralizado (filtros/anotações como `@PreAuthorize`), verificação de propriedade em cada consulta, IDs não previsíveis, rate limiting.
  3. *API gateway/infra:* autenticação obrigatória, segmentação, WAF, bloqueio de acesso direto a APIs internas.
  4. *Dados:* mínimo necessário nas respostas, criptografia, mascaramento.
  5. *Operação:* logs de acesso negado, alertas para varredura de IDs, testes de penetração periódicos.
- **Práticas para backend:** testes automatizados de autorização para cada endpoint, revisar mass assignment (DTOs com campos permitidos), não confiar em dados do cliente, usar bibliotecas de autorização do framework. **Frontend:** esconder botões é só UX, nunca segurança; não colocar regras de acesso apenas no JS; tratar respostas 401/403 corretamente.

## 5. Conclusão

- **O que é e como identificar:** é a falha em fazer valer permissões. Identifica-se tentando acessar recursos e ações com uma conta sem permissão (ou sem conta), trocando IDs/parâmetros/métodos e procurando respostas `200` com dados indevidos.
- **Sinais de vulnerabilidade:** IDs sequenciais expostos, endpoints sem verificação de dono, páginas admin acessíveis por URL, `role` ou `isAdmin` vindo do cliente, falta de testes de autorização, mesmo conteúdo para perfis diferentes, ausência de logs de acesso negado.
- **Por que é difícil em sistemas grandes:** são muitos endpoints, papéis e regras de negócio específicas; o controle costuma ser implementado de forma manual e espalhada; mudanças esquecem de atualizar as regras; não existe uma ferramenta automática que saiba qual a regra "correta" para o negócio, então depende de requisitos claros e de testes.
