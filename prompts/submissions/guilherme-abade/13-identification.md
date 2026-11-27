# Prompt 13: Identification and Authentication Failures (A07:2021)

**Aluno:** Guilherme Abade
**Referências:** [OWASP A07:2021](https://owasp.org/Top10/A07_2021-Identification_and_Authentication_Failures/) · [PortSwigger - Authentication](https://portswigger.net/web-security/authentication)

Os exemplos de código e os roteiros de teste com o Burp Suite destinam-se a laboratório próprio ou autorizado.

## 1. Introdução

A categoria *Identification and Authentication Failures* reúne as falhas que comprometem a confirmação da identidade do usuário e a gestão da sessão autenticada. Antes denominada *Broken Authentication*, passou do segundo para o sétimo lugar em 2021, em parte pela adoção de frameworks de autenticação mais sólidos.

Trata-se de um risco de alta gravidade porque a autenticação constitui a porta de entrada do sistema: quando ela falha, as demais proteções, como a autorização e a auditoria, perdem valor, já que o atacante passa a agir como um usuário legítimo. Em sistemas modernos, com múltiplos clientes e integração por SSO, a superfície de ataque é ampla.

Os mecanismos de identificação e de autenticação cumprem objetivos complementares. Na identificação, o usuário declara quem é, ao informar o login. Na autenticação, o sistema comprova essa identidade, por senha, token ou biometria. Em conjunto, buscam assegurar que apenas o titular acesse a conta, manter a sessão segura e permitir a atribuição de ações a identidades. Quando mal implementados, abrem caminho para o sequestro de contas, o acesso não autorizado, a fraude, o vazamento de dados, o uso indevido de privilégios e a perda de rastreabilidade.

Entre os principais problemas apontados pela OWASP estão as senhas fracas ou padrão, a tolerância a ataques de *credential stuffing* e de força bruta por ausência de limite de tentativas, a recuperação de senha insegura, o armazenamento deficiente de senhas, a ausência de autenticação de múltiplos fatores, a exposição de identificadores de sessão na URL, as sessões que não expiram e os tokens não invalidados no logout. A autenticação de múltiplos fatores é recomendada porque exige mais de um fator, de modo que, ainda que a senha vaze, o atacante continua a precisar do segundo fator, o que neutraliza o *credential stuffing* e a maioria dos ataques de senha.

## 2. Tipos de falhas comuns

Identificar e autenticar são operações distintas: identificar é afirmar a identidade, ao passo que autenticar é prová-la. Uma sem a outra não oferece proteção. Os fatores de autenticação classificam-se em três grupos. O fator de conhecimento corresponde a algo que o usuário sabe, como a senha, o PIN ou a resposta secreta. O fator de posse corresponde a algo que o usuário tem, como o celular com aplicativo de código temporário, o token físico ou a chave FIDO2. O fator de inerência corresponde a algo que o usuário é, como a impressão digital, a face ou a voz.

O *credential stuffing* vale-se de listas de usuário e senha vazadas de outros sites, testadas em massa na expectativa de reutilização de senhas; não se trata de adivinhação, mas de credenciais reais de outra origem. Sua mitigação combina a autenticação de múltiplos fatores, a detecção de login anômalo, a verificação contra bases de senhas vazadas e a limitação de taxa. O ataque de força bruta, por sua vez, testa muitas senhas para um mesmo usuário, ou, na variante de *password spraying*, uma senha comum contra muitos usuários, e é mitigado por limite de tentativas com bloqueio ou temporização progressiva, por CAPTCHA após sucessivas falhas, pela autenticação de múltiplos fatores e pela exigência de senhas fortes.

O controle de sessão exige atenção ao tempo de expiração. Após o login, o servidor emite um identificador de sessão ou um token, que deve ter expiração por inatividade e expiração absoluta, renovação do identificador após o login, para evitar a fixação de sessão, e invalidação no logout. Quando os tokens de sessão não são invalidados corretamente, o logout que apenas remove o cookie no cliente deixa o token ativo no servidor, de maneira que um token capturado continua a funcionar; o mesmo vale para tokens sem expiração curta ou sem lista de revogação.

## 3. Exemplos práticos de vulnerabilidades

Quando a aplicação apresenta respostas diferentes para um usuário válido e para um inválido, informando ora que o usuário não existe, ora que a senha está incorreta, o atacante enumera os usuários válidos pela comparação das respostas. A mitigação consiste em uma mensagem genérica única, acompanhada de tempo de resposta e código de status uniformes. A falta de limite de tentativas, por sua vez, viabiliza a força bruta e o *credential stuffing* em escala, mediante a automação de milhares de tentativas.

O código Java a seguir realiza a autenticação de forma insegura.

```java
public boolean autenticar(String usuario, String senha) {
    Usuario u = repo.buscar(usuario);
    // (1) a comparação de String com == avalia a referência, não o conteúdo
    // (2) a senha trafega em texto puro
    // (3) a comparação é vulnerável a ataque de tempo
    return u != null && u.getSenha() == senha;
}
```

O operador `==` compara referências de objeto, o que produz resultado imprevisível e inseguro, a senha permanece em texto puro e a comparação revela tempo. A versão corrigida armazena a senha como hash com sal e compara em tempo constante.

```java
public boolean autenticar(String usuario, String senha) {
    Usuario u = repo.buscar(usuario);
    if (u == null) {
        // compara mesmo sem usuário, para não revelar tempo e impedir a enumeração
        BCrypt.checkpw(senha, "$2a$12$................................................");
        return false;
    }
    return BCrypt.checkpw(senha, u.getSenhaHash());  // hash lento, com sal e tempo constante
}
```

A senha passa a ser armazenada como hash com sal, por bcrypt ou Argon2id, a verificação resiste a ataques de tempo, a mensagem de erro no controlador é genérica e uma verificação fictícia, quando o usuário não existe, iguala o tempo de resposta.

## 4. Testes de segurança em mecanismos de autenticação

A verificação de um sistema quanto a falhas de autenticação envolve testar a existência de limite de tentativas, comparar as respostas para usuários válidos e inválidos, examinar a expiração e a invalidação de sessão, avaliar a recuperação de senha quanto à possibilidade de pular etapas ou reutilizar tokens e verificar a renovação do identificador de sessão após o login. As técnicas aplicáveis abrangem a força bruta, o ataque de dicionário, o *password spraying*, o *credential stuffing* e a manipulação de parâmetros, além da análise de tempo de resposta.

O Burp Suite é relevante nesse contexto porque permite interceptar requisições no Proxy, repeti-las e variá-las no Repeater, automatizar ataques com cargas no Intruder e comparar respostas no Comparer, o que torna o teste sistemático. Um roteiro de força bruta, em laboratório autorizado, inicia com a interceptação da requisição de login no Proxy e seu envio ao Intruder. Em seguida, marca-se o campo de senha ou de usuário como posição de carga e carrega-se uma lista de palavras. Executado o ataque, ordenam-se os resultados pelo tamanho da resposta e pelo código de status, de modo que a resposta distinta, como um `302` em lugar de `200`, aponte o sucesso ou o usuário válido. Em laboratórios de enumeração de usuário, a comparação das mensagens identifica os usuários válidos, cuja senha pode então ser atacada. A detecção de falhas de sessão complementa esse trabalho: reenvia-se uma requisição autenticada com o token antigo após o logout, esperando-se um código `401`, mede-se a expiração por inatividade e confirma-se a renovação do identificador após o login.

## 5. Aplicação em cenários reais

Uma aplicação de comércio eletrônico que permite login sem limite de tentativas oferece riscos consideráveis. Expõe os usuários à tomada de contas e à fraude com cartões salvos, e a empresa ao dano reputacional, ao custo de estornos e ao possível descumprimento da LGPD. Os mecanismos de proteção adequados compreendem o limite de tentativas com bloqueio ou temporização, a autenticação de múltiplos fatores, o CAPTCHA após sucessivas falhas, a verificação contra senhas vazadas, a detecção de login anômalo por geolocalização ou dispositivo novo, o alerta ao usuário e a exigência de senha forte.

Em um cenário de *credential stuffing*, o atacante emprega credenciais vazadas de outro site e automatiza o login. A equipe de segurança detecta o ataque pelo pico de falhas de login provenientes de muitos endereços, pela taxa de sucesso baixa, porém não nula, e por origens incomuns. A resposta abrange a limitação por endereço e por conta, a exigência de autenticação de múltiplos fatores e a redefinição de senha nas contas atingidas, o alerta aos usuários e o cruzamento com bases de vazamento.

## 6. Conclusão

As falhas de autenticação permanecem comuns porque a autenticação é difícil de acertar em todos os seus detalhes, que vão da senha e da sessão à recuperação e à autenticação de múltiplos fatores, porque há muito código legado e porque a pressão por usabilidade conduz a atalhos inseguros. A relação entre usabilidade e segurança é tensa, pois quanto mais fácil o acesso, mais fácil também o ataque. O equilíbrio contemporâneo busca mecanismos de múltiplos fatores de baixa fricção, como notificações por push e chaves de acesso, associados à autenticação baseada em risco, que solicita um fator adicional apenas quando o risco é elevado, em lugar de regras que incomodam o usuário sem protegê-lo.

Um exemplo de decisão de design que comprometeu a segurança são as perguntas de segurança usadas na recuperação de senha. Concebidas para facilitar a vida do usuário que esquece a senha, baseiam-se em respostas com frequência públicas ou previsíveis, acessíveis em redes sociais, o que viabilizou diversas tomadas de conta. Ao priorizar a conveniência, o design abriu um vetor mais frágil que a própria senha. A recomendação atual substitui essas perguntas pela recuperação por e-mail ou mensagem, com token de uso único e tempo curto, idealmente associada à autenticação de múltiplos fatores.
