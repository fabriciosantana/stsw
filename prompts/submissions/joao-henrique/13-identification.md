# Prompt 13: OWASP Top Ten: Identification and Authentication Failures (A07:2021)

Referências: [OWASP A07:2021](https://owasp.org/Top10/A07_2021-Identification_and_Authentication_Failures/), [PortSwigger – Authentication](https://portswigger.net/web-security/authentication)

## 1. Introdução

A categoria A07 reúne falhas na confirmação da identidade do usuário, na autenticação e no gerenciamento de sessão.

- **Por que é crítica:** se a autenticação falha, o atacante age como outro usuário (inclusive administrador) e todos os demais controles (autorização, auditoria) deixam de fazer sentido.
- **Objetivos dos mecanismos:** *identificação* (quem diz ser), *autenticação* (provar que é essa pessoa) e *gerenciamento de sessão* (manter essa prova de forma segura enquanto o usuário usa o sistema).
- **Se mal implementados:** tomada de contas, fraude, vazamento de dados, escalada de privilégios, responsabilização errada nos logs.
- **Problemas comuns:** senhas fracas ou padrão (`admin/admin`), permitir senhas vazadas, armazenar senhas em texto claro ou com hash fraco, ausência de MFA, recuperação de senha insegura (perguntas secretas), ID de sessão na URL, sessão que não expira nem é invalidada no logout, sem limite de tentativas, mensagens que revelam se o usuário existe.
- **Por que MFA é recomendado:** mesmo que a senha vaze ou seja adivinhada, o atacante precisa de um segundo fator (algo que o usuário tem ou é). Bloqueia a maior parte de credential stuffing, força bruta e phishing simples.

## 2. Tipos de falhas comuns

- **Identificação x autenticação:** identificação é declarar quem se é (login, e-mail); autenticação é comprovar (senha, token, biometria).
- **Fatores:** conhecimento (senha, PIN), posse (celular, token, chave física), inerência (biometria).
- **Credential stuffing:** o atacante usa pares usuário/senha vazados de outros sites, apostando que o usuário reutiliza a senha. É automatizado e em grande escala, com baixa taxa de acerto por tentativa, mas alto retorno no total.
- **Força bruta:** tentar muitas senhas (todas as combinações ou dicionário) para uma conta. Mitigação: limite de tentativas, bloqueio temporário/progressivo, CAPTCHA, MFA, detecção de IPs/padrões anômalos e senhas fortes.
- **Controle de sessão e expiração:** a sessão deve ter ID aleatório e longo, regenerado após o login, com timeout por inatividade e duração máxima. Sem expiração, uma sessão roubada vale indefinidamente.
- **Token não invalidado:** após logout, troca de senha ou desativação do usuário, o token roubado continua funcionando, permitindo sequestro de sessão prolongado.

## 3. Exemplos práticos de vulnerabilidades

- **Respostas diferentes para usuário válido e inválido (enumeração de usuários):** "usuário não existe" x "senha incorreta" (ou diferença em tamanho/tempo de resposta) deixa o atacante montar uma lista de contas válidas e focar nelas. Correção: mensagem genérica ("usuário ou senha inválidos") e tempo de resposta uniforme.
- **Sem limite de tentativas:** o atacante roda milhares de combinações (Burp Intruder, Hydra) até acertar; também permite credential stuffing em massa.
- **Código vulnerável (Java):**

```java
public boolean login(String usuario, String senhaDigitada) {
    Usuario u = repo.buscarPorLogin(usuario);
    if (u == null) return false;
    return u.getSenha() == senhaDigitada;   // vulnerável
}
```

Problemas: `==` compara referência de objetos e não o conteúdo (comportamento errado); senha armazenada em texto claro; comparação não é em tempo constante; sem limite de tentativas.

- **Correção segura:**

```java
public boolean login(String usuario, String senhaDigitada) {
    if (limitador.bloqueado(usuario)) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
    Usuario u = repo.buscarPorLogin(usuario);
    String hash = (u != null) ? u.getSenhaHash() : HASH_FALSO;   // mesmo custo para usuário inexistente
    boolean ok = passwordEncoder.matches(senhaDigitada, hash)    // BCrypt/Argon2 (compara em tempo constante)
                 && u != null;
    if (!ok) limitador.registrarFalha(usuario);
    return ok;
}
```

## 4. Testes de segurança em autenticação

- **Técnicas:** força bruta e ataque de dicionário, credential stuffing, teste de credenciais padrão, enumeração de usuários, manipulação de parâmetros (ex.: `role=admin`, pular a etapa de 2FA acessando a URL final), teste da recuperação de senha, análise de cookies/tokens.
- **Importância do Burp Suite:** intercepta e repete requisições (Proxy/Repeater), automatiza tentativas (Intruder), compara respostas (Comparer) e analisa a aleatoriedade de tokens (Sequencer).
- **Passo a passo de força bruta no Burp (em laboratório):**
  1. Ativar o proxy e capturar o POST de login.
  2. Enviar ao Intruder e marcar o campo senha como posição do payload.
  3. Carregar a lista de senhas (ex.: SecLists `cirt-default-passwords.txt`).
  4. Iniciar o ataque e ordenar os resultados por status/tamanho/tempo da resposta.
  5. A resposta que difere das demais (ex.: 302 ou tamanho diferente) indica a senha correta.
  6. Registrar e reportar a falta de limite de tentativas.
- **Expiração e logout:** logar, copiar o cookie/token, fazer logout e reenviar uma requisição autenticada com o cookie antigo: se ainda funciona, o logout é inefetivo. Deixar a sessão inativa além do tempo previsto e ver se continua válida.

## 5. Cenário real: e-commerce sem limite de tentativas

- **Riscos:** tomada de contas, compras fraudulentas, vazamento de dados pessoais e de cartões salvos, dano à reputação e responsabilização pela LGPD.
- **Proteções:** limite de tentativas por conta e por IP com atraso progressivo, CAPTCHA após falhas, MFA (obrigatório em operações sensíveis), checagem de senhas vazadas (Have I Been Pwned), notificação de login em novo dispositivo, WAF/detecção de bots.
- **Simulação de credential stuffing:** o atacante pega uma lista vazada de 1 milhão de pares e-mail/senha e testa em `/login` com bots distribuídos em muitos IPs; algumas centenas de contas são abertas e ele faz compras com cartões salvos.
- **Detecção:** pico de falhas de login e de requisições a `/login`, muitos usuários diferentes tentados por IP (ou o mesmo IP/User-Agent), taxa de sucesso baixa e constante, logins com sucesso de localizações incomuns, aumento de reclamações.
- **Resposta:** bloquear IPs/ASNs e aplicar rate limit, exigir redefinição de senha e MFA das contas afetadas, invalidar sessões, notificar usuários e a ANPD se houver dados pessoais envolvidos, revisar logs e corrigir a causa (limites, MFA).

## 6. Conclusão

- **Por que ainda são comuns:** autenticação parece simples e muita gente implementa "do zero"; há dependência de comportamento humano (senhas fracas e reutilizadas); sistemas legados; o MFA aumenta o atrito, então muitos o deixam opcional; recuperação de conta é difícil de projetar bem.
- **Usabilidade x segurança:** controles mais fortes (senhas longas, MFA, expiração curta) incomodam o usuário, e controles muito fracos facilitam ataques. O objetivo é um equilíbrio: senhas longas sem regras de composição absurdas, MFA com bom UX (passkeys/biometria), "lembrar dispositivo".
- **Exemplo em que a conveniência comprometeu a segurança:** as "perguntas secretas" para recuperar a senha (nome da mãe, cidade natal), fáceis de lembrar para o usuário mas também fáceis de descobrir por redes sociais; e a mensagem "este e-mail não está cadastrado", útil ao usuário mas que permite enumerar contas.
