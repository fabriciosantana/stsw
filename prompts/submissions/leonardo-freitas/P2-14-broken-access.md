# P2-14 — OWASP Top Ten: Broken Access Control

**Aluno:** Leonardo Freitas (`leofreitasb`)  
**Disciplina:** Segurança e Teste de Software (STSW)  
**Roteiro:** [`prompts/14-broken.md`](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/14-broken.md)  
**Categoria:** A01:2021, conforme o enunciado.  
**Data:** 16/11/2026.

## 1. Introdução

Controle de acesso é a aplicação de regras que impedem uma pessoa ou processo de executar ações fora de suas permissões. **Autenticação** verifica quem está fazendo a requisição; **autorização** decide se essa identidade pode executar aquela ação sobre aquele objeto. Uma conta autenticada não recebe, por isso, acesso automático às contas de outras pessoas nem a funções administrativas. Broken Access Control ocorre quando essa decisão falta, é feita no lugar errado ou pode ser contornada. [1][2]

Na classificação **OWASP Top 10 de 2021**, A01 subiu do quinto para o primeiro lugar. No conjunto de dados usado pela OWASP, 94% das aplicações foram testadas quanto a alguma forma de falha de controle de acesso; a categoria teve incidência média de 3,81% e mais de 318 mil ocorrências. Esses números explicam o posicionamento naquela edição, sem significar que 94% das aplicações eram vulneráveis. [1]

Os princípios são: negar por padrão, conceder o mínimo privilégio necessário, verificar permissão no servidor em **toda** requisição e para **cada objeto** acessado, manter a regra centralizada e testável, separar funções incompatíveis e registrar negações. A interface pode ocultar um botão, mas a autorização precisa ocorrer no backend; `userId`, URL, campos ocultos, cookies e cabeçalhos enviados pelo cliente não são provas de permissão. [1][2]

### Falhas comuns e impacto

| Falha | Exemplo e consequência |
|---|---|
| IDOR / autorização horizontal ausente | Trocar `userId=1` por `userId=2` e receber o perfil de outra pessoa. |
| Bypass de ACL por URL ou navegação forçada | Usuário comum solicita diretamente `/admin/relatorios` mesmo sem link na interface. |
| Funções sem verificação por método | `GET` é protegido, mas `POST`, `PUT` ou `DELETE` para o mesmo recurso passam sem autorização. |
| Confiança em token ou metadado manipulável | Aplicação aceita `role=admin` fornecido pelo cliente ou um JWT sem validação correta de assinatura, audiência e validade. |
| Acesso negado só na interface | Frontend esconde uma ação, mas a API aceita a requisição enviada diretamente. |
| Permissões excessivas ou padrão permissivo | Nova rota nasce pública ou um papel genérico recebe acesso amplo sem justificativa. |

O impacto depende do recurso: exfiltração ou alteração de dados pessoais, operações financeiras indevidas, exclusão de registros, escalada horizontal entre contas e vertical até funções administrativas. IDs difíceis de adivinhar ajudam a reduzir descoberta, mas não substituem a verificação de autorização. [1][2][3]

## 2. Exemplo de API insegura, correção e simulação da URL

O exemplo abaixo representa o defeito central, independentemente do framework HTTP usado:

```java
// INSEGURO: id vem de /api/profile?userId=2 e não há teste de titularidade.
Profile profile(int userId) {
    return repository.findById(userId);
}
```

Se o usuário 1 está autenticado e troca `userId=1` por `userId=2`, a aplicação retorna o objeto 2 sem verificar se ele pertence ao solicitante. Isso é **IDOR** e escalada horizontal: uma referência direta controlada pelo cliente dá acesso a objeto de outro usuário. A versão segura deriva a identidade de uma sessão validada no servidor e compara o titular do objeto antes de devolvê-lo. [1][3]

```java
// SEGURO: principal vem de uma sessão verificada no servidor.
Response profile(Principal principal, int userId) {
    if (principal == null) return Response.unauthorized(); // HTTP 401
    Profile profile = repository.findById(userId);
    if (profile == null) return Response.notFound();         // HTTP 404
    if (principal.id() != profile.ownerId() && !principal.isAdmin()) {
        return Response.forbidden();                         // HTTP 403
    }
    return Response.ok(profile);
}
```

O [`AccessControlLab.java`](P2-14/AccessControlLab.java) implementa os dois comportamentos em uma API HTTP **fictícia**, acessível somente em `127.0.0.1`. O endpoint intencionalmente inseguro `/insecure/profile?userId=2` retorna HTTP 200 para o usuário 1; o seguro `/secure/profile?userId=2` retorna HTTP 403. O código usa tokens aleatórios em memória apenas para montar as sessões do laboratório; não implementa login nem pretende ser um sistema de produção. Uma API real também exigiria TLS, expiração e revogação de sessão, auditoria e tratamento cuidadoso de dados. [1][2]

O comportamento esperado para tentativa de acesso a dados de outra pessoa é **negação no servidor**, sem conteúdo sensível na resposta. HTTP 403 é adequado quando a aplicação reconhece a identidade mas proíbe o acesso; alguns sistemas usam 404 para não confirmar a existência do recurso. A escolha deve ser consistente com a política do produto. HTTP 401 indica ausência de autenticação válida. [2]

## 3. Ferramentas, perfis e testes

Em aplicação de teste autorizada, o Burp Suite pode capturar no Proxy uma requisição feita pelo administrador. No Repeater, o testador troca a sessão pela de um usuário comum e repete a requisição; depois testa a mesma operação sem sessão, muda o ID de um objeto próprio para o de outro usuário e verifica métodos HTTP alternativos. A comparação deve examinar código HTTP, redirecionamentos e **corpo completo**, porque uma resposta de erro pode ainda vazar dados. O **Comparer** permite enviar duas respostas e destacar diferenças por palavras ou bytes; isso ajuda a perceber quando a resposta negada ainda contém campos sensíveis. Para muitos endpoints, a comparação de mapas do site sob duas sessões pode ampliar a cobertura. [3][4][5]

### Execução local com perfis diferentes

Foram criados no laboratório três perfis fictícios: `usuario-1` e `usuario-2` (comuns), e `admin`. A tabela resume as requisições de fato enviadas ao servidor local pelo programa. O arquivo [`resultado-local.txt`](P2-14/evidencias/resultado-local.txt) contém as respostas registradas na execução.

| Perfil | Requisição | Resultado observado |
|---|---|---|
| `usuario-1` | `GET /secure/profile?userId=1` | 200, próprio perfil |
| `usuario-1` | `GET /insecure/profile?userId=2` | **200, perfil alheio exposto no endpoint vulnerável** |
| `usuario-1` | `GET /secure/profile?userId=2` | 403, acesso negado |
| `usuario-2` | `GET /secure/profile?userId=2` | 200, próprio perfil |
| `usuario-1` | `GET /secure/admin/report` | 403, acesso administrativo negado |
| `admin` | `GET /secure/admin/report` | 200, relatório fictício |
| sem sessão | `GET /secure/profile?userId=1` | 401, autenticação exigida |
| `admin` | `GET /secure/profile?userId=2` | 200, acesso autorizado pela política do laboratório |

**Resultado:** o perfil comum não acessou o endpoint administrativo nem o perfil de outro usuário na versão segura. O acesso indevido ocorreu somente no endpoint criado intencionalmente como vulnerável. Não houve acesso a dados reais. O cenário Gherkin em [`access-control.feature`](P2-14/access-control.feature) descreve a política esperada; ele **não foi executado em Cucumber**. As oito verificações equivalentes foram executadas pelo programa Java.

**Burp Suite não foi executado nesta P2-14.** Os passos com a ferramenta são um roteiro de teste, não evidência de captura. As três imagens recebidas para I2-08 também não comprovam este experimento e não foram incluídas.

## 4. Caso real e defesa em camadas

**First American Title Insurance / EaglePro (2019).** Segundo a ordem de consentimento do Departamento de Serviços Financeiros do Estado de Nova York (NYSDFS), links do sistema permitiam acesso sem login a documentos de transações imobiliárias e não expiravam. Alterar o ID numérico de um documento na URL possibilitava visualizar documentos não relacionados. A ordem registra que um jornalista **alegou** exposição potencial de 885 milhões de documentos; isso não equivale à comprovação de que todos foram acessados ou exfiltrados. Entre os dados potencialmente acessíveis havia números de previdência social, carteiras de motorista, informações bancárias e fiscais. A autoridade concluiu que faltaram controles de acesso suficientes e que a classificação de risco e o acompanhamento de uma descoberta anterior em teste de penetração foram inadequados. A empresa desativou o acesso externo ao sistema após ser informada do problema e celebrou acordo com multa de US$ 1 milhão. [6]

Uma defesa em camadas para sistema semelhante inclui: identificar titular e finalidade de cada documento; negar por padrão; verificar autorização por documento em **cada** solicitação e em todos os métodos; usar links compartilháveis com escopo e prazo curtos, quando necessários, sem confiar no ID; isolar e classificar dados sensíveis; limitar exportação em massa; monitorar acessos anômalos; revisar permissões e testar regressões de autorização. Para o backend, a equipe deve centralizar políticas, testar combinação de papel, ação e objeto e registrar negações sem gravar segredos. Para o frontend, deve apresentar somente ações permitidas para a experiência do usuário, porém deixar a decisão final no servidor. Revisões de código e testes automatizados devem acompanhar mudanças em rotas e modelos de dados. [1][2]

## 5. Conclusão

Broken Access Control é a falha em impedir que uma identidade execute uma ação fora de sua política de acesso. Sinais de risco são recursos recuperáveis pela troca de IDs, rotas administrativas acessíveis por URL direta, diferenças entre proteção da interface e da API, e resultados divergentes entre métodos HTTP. Identifica-se a falha testando, com contas autorizadas, a matriz **sujeito × ação × recurso** e comparando resultados. Em sistemas grandes, papéis, relações entre usuários e objetos, serviços e endpoints mudam em ritmos diferentes; uma regra esquecida em uma rota é suficiente para criar exposição. A política deve ser explícita, aplicada no servidor e testada continuamente. [1][2][3]

## Evidências e limites

- **Executado:** laboratório HTTP local em Java, limitado ao endereço de loopback, com 8/8 verificações de resposta.
- **Documentado, mas não executado:** cenário Gherkin em Cucumber e testes com Burp Suite.
- **Não utilizado como evidência:** capturas da atividade I2-08, laboratórios externos e qualquer dado de terceiros.

## Fontes

1. [OWASP Top 10:2021 — A01 Broken Access Control](https://top10.owasp.org/2021/A01_2021-Broken_Access_Control/).
2. [OWASP — Authorization Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html).
3. [PortSwigger — Access control vulnerabilities and privilege escalation](https://portswigger.net/web-security/access-control).
4. [PortSwigger — Testing for privilege escalation with Burp Suite](https://portswigger.net/burp/documentation/desktop/testing-workflow/vulnerabilities/access-controls/privilege-escalation).
5. [PortSwigger — Burp Comparer](https://portswigger.net/burp/documentation/desktop/tools/comparer).
6. [NYSDFS — Consent Order, First American Title Insurance Company, 27/11/2023](https://www.dfs.ny.gov/system/files/documents/2024/10/ea20231127_co_first_american.pdf).

