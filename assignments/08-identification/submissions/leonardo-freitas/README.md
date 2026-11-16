# I2-08 — Identification and Authentication Failures / Broken Access Control

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software — IDP  
**Enunciado:** [assignments/08-identification](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/08-identification/README.md)

## Escopo e estado da entrega

O enunciado enumera **16 atividades**: quatro desafios OWASP Juice Shop e doze laboratórios PortSwigger (três de autenticação e nove de controle de acesso). Este documento organiza o procedimento de reprodução, a falha de segurança e a correção de cada um. Os passos são um **roteiro baseado nas páginas oficiais**, não um registro de execução nesta sessão. Nenhuma das 16 instâncias foi iniciada ou marcada como resolvida por esta entrega.

As três imagens fornecidas pelo aluno estão em [`evidencias/`](./evidencias/). Elas são identificadas individualmente em [`EVIDENCIAS.md`](./EVIDENCIAS.md). Imagem de uma tela de sucesso ou de um relatório não equivale a confirmação independente do desafio; falta identificar instância, sessão e resultado verificável. O conteúdo também não cobre os doze laboratórios PortSwigger exigidos.

**Como concluir a comprovação:** executar cada desafio em uma instância própria do Juice Shop ou da PortSwigger Web Security Academy, registrar URL/identificador da instância, versão e passos efetivamente usados, salvar captura com o indicador *Solved* ou *Challenge Solved* e atualizar a tabela de evidências no mesmo PR.

## 1. OWASP Juice Shop

### JS-01 — Login com credenciais do administrador

[Desafio oficial](https://pwning.owasp-juice.shop/companion-guide/latest/part2/broken-authentication.html#_log_in_with_the_administrators_user_credentials_without_previously_changing_them_or_applying_sql_injection)

1. Na instância de treinamento, identificar a conta administrativa conforme pistas e documentação do próprio desafio.
2. Testar senhas candidatas na função de login, sem usar SQL Injection nem alterar a senha previamente. Limitar as tentativas à instância autorizada; o enunciado aponta uma [lista de senhas de referência](https://github.com/danielmiessler/SecLists/blob/master/Passwords/cirt-default-passwords.txt).
3. Confirmar a identidade autenticada e o aviso do desafio no Score Board. Anotar a versão da aplicação e o método realmente empregado.

**Falha:** credencial previsível e proteção insuficiente contra adivinhação de senha. **Correção:** senha única e forte, segundo fator, limitação de tentativas e alertas. **Estado:** não verificado nesta entrega; a imagem `1.jpg` apenas apresenta um aviso visual de sucesso.

### JS-02 — Acessar a área administrativa

[Desafio oficial](https://pwning.owasp-juice.shop/companion-guide/latest/part2/broken-access-control.html#_access_the_administration_section_of_the_store)

1. Em sessão de usuário comum, identificar a rota de administração pelas pistas da aplicação e tentar acessá-la diretamente.
2. Confirmar se o servidor permite o acesso e se o Score Board registra a resolução.

**Falha:** autorização implementada apenas na navegação/interface, sem validação de papel no servidor. **Correção:** verificar permissão em toda requisição administrativa, independentemente de como a rota foi alcançada. **Estado:** não verificado.

### JS-03 — Remover avaliações de cinco estrelas

[Desafio oficial](https://pwning.owasp-juice.shop/companion-guide/latest/part2/broken-access-control.html#_get_rid_of_all_5_star_customer_feedback)

1. Na instância de treinamento, localizar a função de administração de avaliações e observar a requisição de remoção.
2. Testar se a operação é autorizada para o usuário atual e, somente no laboratório, remover as avaliações exigidas pelo desafio.
3. Conferir o Score Board e registrar as operações realmente efetuadas.

**Falha:** operação sensível sem controle de autorização suficiente. **Correção:** negar mutações a usuários sem papel adequado e registrar auditoria. **Estado:** não verificado.

### JS-04 — Visualizar a cesta de outro usuário

[Desafio oficial](https://pwning.owasp-juice.shop/companion-guide/latest/part2/broken-access-control.html#_view_another_users_shopping_basket)

1. Autenticar-se na instância própria e observar a requisição que carrega a cesta.
2. Alterar apenas o identificador da cesta na instância de laboratório e verificar se o servidor devolve dados de outro usuário.
3. Confirmar a resolução no Score Board, sem incluir dados pessoais reais nas evidências.

**Falha:** referência direta a objeto sem conferir seu proprietário. **Correção:** vincular a cesta à identidade da sessão no servidor, com testes para acesso cruzado. **Estado:** não verificado.

## 2. PortSwigger — Authentication

Todos os passos abaixo se aplicam **somente a instâncias oficiais iniciadas pelo aluno**. Cada laboratório possui dados e URLs próprios; não se deve transportar cookies, tokens ou senhas entre instâncias.

### PS-A01 — Username enumeration via different responses

[Laboratório oficial](https://portswigger.net/web-security/authentication/password-based/lab-username-enumeration-via-different-responses)

Enviar tentativas de login para `POST /login` pelo Burp Intruder, variando primeiro `username`; comparar as mensagens de erro para distinguir usuário inexistente de senha incorreta. Com o usuário identificado, testar a lista de senhas fornecida pelo laboratório e verificar a resposta de sucesso; acessar *My account* e capturar o banner *Solved*. **Falha:** respostas diferentes revelam a existência da conta, facilitando força bruta. **Correção:** erro uniforme, limitação de tentativas e MFA. **Estado:** não verificado.

### PS-A02 — 2FA simple by-pass

[Laboratório oficial](https://portswigger.net/web-security/authentication/multi-factor/lab-2fa-simple-bypass)

Examinar o fluxo de login de dois fatores com a conta de teste, observando a URL da página de conta após autenticação. Iniciar login com as credenciais do alvo fornecidas pelo laboratório e, durante a etapa do código, tentar navegar diretamente para a página autenticada. Confirmar o banner *Solved* se a aplicação aceitar o acesso. **Falha:** a sessão recebe privilégio antes da conclusão do segundo fator. **Correção:** marcar sessão como plenamente autenticada apenas após validar o código e exigir esse estado em todas as rotas protegidas. **Estado:** não verificado.

### PS-A03 — Password reset broken logic

[Laboratório oficial](https://portswigger.net/web-security/authentication/other-mechanisms/lab-password-reset-broken-logic)

Solicitar redefinição para a própria conta de teste, abrir o e-mail do laboratório e inspecionar no Burp a requisição final de redefinição. Verificar se o token é validado no envio do formulário e se o campo `username` pode ser alterado. Na instância oficial, demonstrar a troca indevida da senha da conta alvo indicada pelo laboratório, acessar *My account* e registrar *Solved*. **Falha:** vínculo insuficiente entre token de redefinição, usuário e operação. **Correção:** token aleatório de uso único, prazo curto, validação no envio e conta derivada exclusivamente do token. **Estado:** não verificado.

## 3. PortSwigger — Broken Access Control

### PS-C01 — Unprotected admin functionality

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-unprotected-admin-functionality)

Inspecionar `robots.txt` na instância do laboratório, localizar a rota administrativa anunciada, acessá-la sem autorização e realizar apenas a ação pedida pelo enunciado da PortSwigger. Conferir *Solved*. **Falha:** rota de administração sem autorização de servidor. **Correção:** checagem de papel em toda ação administrativa; `robots.txt` não é mecanismo de proteção. **Estado:** não verificado.

### PS-C02 — Unprotected admin functionality with unpredictable URL

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-unprotected-admin-functionality-with-unpredictable-url)

Examinar o código-fonte/JavaScript entregue ao navegador da instância para encontrar a URL administrativa aparentemente imprevisível, acessá-la e completar a ação do laboratório. **Falha:** confiar no segredo da URL para proteger uma função. **Correção:** autorização no servidor, mesmo para caminhos difíceis de adivinhar. **Estado:** não verificado.

### PS-C03 — User role controlled by request parameter

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-role-controlled-by-request-parameter)

Observar no Burp a requisição à área administrativa e o parâmetro/cookie de papel. Alterar seu valor apenas na instância de treinamento, verificar se a rota é liberada e concluir a ação requerida. **Falha:** o servidor confia em atributo de autorização controlado pelo cliente. **Correção:** papel armazenado e validado no servidor; cookies devem conter somente identificadores de sessão protegidos. **Estado:** não verificado.

### PS-C04 — User role can be modified in user profile

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-role-can-be-modified-in-user-profile)

Autenticar-se com a conta de teste, interceptar a atualização de perfil e verificar se o corpo da requisição aceita atributo de papel além dos campos editáveis. Na instância oficial, testar a alteração do papel, acessar a administração e concluir o objetivo. **Falha:** atribuição em massa de campo privilegiado. **Correção:** lista explícita de campos editáveis e separação de operações de perfil e privilégios. **Estado:** não verificado.

### PS-C05 — User ID controlled by request parameter

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-id-controlled-by-request-parameter)

Entrar com a conta de teste e comparar `My account` antes e depois de trocar o parâmetro `id` por outro usuário indicado no laboratório. Registrar o acesso cruzado e a condição *Solved*. **Falha:** IDOR/BOLA por não vincular o parâmetro à sessão. **Correção:** conferir autorização no objeto consultado. **Estado:** não verificado.

### PS-C06 — User ID controlled by request parameter, with unpredictable user IDs

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-id-controlled-by-request-parameter-with-unpredictable-user-ids)

Encontrar, no conteúdo público da instância, o identificador não previsível do usuário alvo; aplicá-lo ao parâmetro da página de conta e conferir se o servidor permite ver o recurso. **Falha:** identificador difícil de adivinhar não substitui autorização. **Correção:** validar posse/permissão do objeto em cada requisição. **Estado:** não verificado.

### PS-C07 — User ID controlled by request parameter with data leakage in redirect

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-id-controlled-by-request-parameter-with-data-leakage-in-redirect)

Interceptar a resposta HTTP da página de conta ao consultar outro `id`; mesmo que haja redirecionamento, verificar se o corpo da resposta contém dados que deveriam ser privados. Seguir apenas a tarefa da instância e confirmar *Solved*. **Falha:** dados são emitidos antes da autorização/redirecionamento. **Correção:** autorizar antes de carregar ou serializar a resposta. **Estado:** não verificado.

### PS-C08 — User ID controlled by request parameter with password disclosure

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-user-id-controlled-by-request-parameter-with-password-disclosure)

Consultar a página de conta do usuário indicado no laboratório com o parâmetro `id` alterado e examinar a resposta/HTML do formulário de senha. Se a senha for exposta, usar somente a conta fictícia do laboratório para completar o objetivo e confirmar *Solved*. **Falha:** autenticação e autorização falhas somadas à exposição de segredo no HTML. **Correção:** nunca devolver senha armazenada, usar hash resistente e conferir proprietário da conta. **Estado:** não verificado.

### PS-C09 — Insecure direct object references

[Laboratório oficial](https://portswigger.net/web-security/access-control/lab-insecure-direct-object-references)

Observar na instância o identificador usado no download de transcrições do chat; testar uma referência pertencente a outra conversa e verificar se o conteúdo é entregue sem autorização. Seguir o objetivo oficial e capturar *Solved*. **Falha:** acesso direto a arquivo de outra pessoa sem checagem de proprietário. **Correção:** autorização por objeto também em downloads, com IDs não previsíveis apenas como defesa adicional. **Estado:** não verificado.

## 4. Evidência e conclusão

O procedimento documentado cobre todos os itens do enunciado, porém **não comprova a resolução prática**. Os arquivos de imagem fornecidos foram mantidos sem alteração. O status de cada desafio permanece **não verificado** até haver execução observável e evidência vinculada à instância. A tabela completa e os critérios de comprovação estão em [`EVIDENCIAS.md`](./EVIDENCIAS.md).

As falhas se agrupam em dois controles essenciais: autenticar a identidade antes de conceder acesso e autorizar cada recurso/operação no servidor. Senhas com hash (tema da imagem `3.jpg`) são recomendadas, mas **não resolvem sozinhas** o bypass de 2FA, os problemas de redefinição ou o acesso indevido a objetos de outros usuários.

