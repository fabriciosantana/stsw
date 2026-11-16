# P2-13 — OWASP Top Ten: Identification and Authentication Failures

**Aluno:** Leonardo Freitas (`leofreitasb`)  
**Disciplina:** Segurança e Teste de Software (STSW)  
**Roteiro:** [`prompts/13-identification.md`](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/13-identification.md)  
**Categoria estudada:** A07:2021, conforme a versão indicada no roteiro.  
**Data:** 16/11/2026.

## 1. Introdução

Identificação é a declaração de *quem* deseja acessar o sistema, por exemplo, um nome de usuário. Autenticação é a verificação dessa identidade por meio de um segredo, dispositivo ou característica biométrica. A categoria A07:2021 da OWASP reúne falhas nessas etapas e no gerenciamento da sessão autenticada. A identificação e a autenticação protegem a associação entre uma pessoa e sua conta; a sessão mantém essa associação entre requisições. Sem esses controles, um terceiro pode assumir contas e acessar dados ou operações em nome do titular. [1]

O risco é alto em sistemas modernos porque contas dão acesso a dados pessoais, compras, pagamentos e funções administrativas; credenciais vazadas de outros serviços podem ser reutilizadas em escala. Erros típicos são senhas padrão ou fracas, armazenamento em texto claro ou hash rápido, recuperação de conta frágil, mensagens que revelam usuários existentes, ausência de limitação de tentativas, MFA ausente ou ineficaz, sessão que não é renovada após login e token que permanece válido após logout ou inatividade. [1]

MFA exige um segundo fator independente, como uma chave de segurança ou aplicativo autenticador. Assim, o conhecimento da senha, isoladamente, tende a não bastar para o acesso. Ele reduz o impacto de reutilização e vazamento de senhas, embora não substitua controles contra phishing, abuso de sessão e recuperação de conta insegura. [1][2]

## 2. Falhas comuns e mecanismos

| Conceito | Exemplo | Risco ou observação |
|---|---|---|
| Conhecimento | Senha ou PIN | Pode ser adivinhado, reutilizado ou vazado. |
| Posse | Chave de segurança, aplicativo autenticador | Depende da segurança do dispositivo e do fluxo de recuperação. |
| Inerência | Biometria | A verificação e o armazenamento do modelo biométrico exigem proteção; biometria não pode ser trocada como uma senha. |

*Credential stuffing* usa pares de usuário e senha já obtidos em outros vazamentos e testa sua reutilização. Força bruta tenta várias combinações; um *dictionary attack* prioriza senhas prováveis. A defesa combina MFA, detecção de credenciais comprometidas, limitação progressiva de tentativas por conta e por origem, monitoramento e mensagens genéricas. Um bloqueio permanente acionável por qualquer terceiro pode virar negação de serviço, portanto a política precisa ser calibrada. [1][2][3]

O servidor deve criar um identificador de sessão novo e imprevisível após o login, transmiti-lo em cookie `Secure`, `HttpOnly` e com `SameSite` adequado, aplicar prazo por inatividade e prazo absoluto e invalidá-lo no servidor no logout. Se um token permanece válido, quem o capturou pode reproduzi-lo mesmo depois de o usuário clicar em “Sair”; apagar apenas o cookie no navegador não revoga uma cópia já roubada. Mudança de senha ou suspeita de comprometimento deve levar à revogação das sessões relevantes. [1][4]

## 3. Exemplos práticos

Em um formulário de login, respostas distintas como “usuário inexistente” e “senha incorreta” permitem enumerar contas válidas; diferença de código HTTP, tamanho da resposta ou tempo de processamento também pode revelar informação. Sem limite de tentativas, um atacante pode automatizar palpites de senha contra essas contas. A PortSwigger demonstra ambas as classes de falha em laboratório intencionalmente vulnerável. A resposta defensiva usa mensagem externa uniforme, observabilidade interna, MFA e limitação de tentativas. [2][3]

### Exemplo Java inseguro (ilustrativo; não executar em produção)

```java
boolean autenticar(String usuario, String senha) {
    return usuario.equals("admin") && senha == "Senha123";
}
```

`==` compara referências de objetos `String`, não seu conteúdo; uma senha correta pode ser rejeitada de maneira inesperada. `equals()` corrigiria **apenas** a comparação de conteúdo. O segredo embutido no código continuaria inseguro, assim como armazená-lo em texto claro. A correção adequada armazena um hash de senha adaptativo com sal exclusivo por conta e verifica o hash derivado da senha recebida. O exemplo executável [`AuthDemo.java`](P2-13/AuthDemo.java) usa PBKDF2-HMAC-SHA-256 com 600.000 iterações, sal aleatório de 16 bytes e comparação de bytes por `MessageDigest.isEqual`. PBKDF2 foi escolhido por estar disponível no Java padrão; a OWASP recomenda Argon2id como primeira opção quando disponível. [5]

O exemplo é uma demonstração isolada, não um serviço completo: ainda seriam necessários proteção contra tentativas automatizadas, MFA, recuperação segura, auditoria e um gerenciador de sessão do framework. Mesmo com mensagem uniforme, diferenças de tempo podem exigir cuidado adicional para evitar enumeração. [1][2]

## 4. Testes de segurança em autenticação

Em **ambiente próprio ou laboratório autorizado**, o testador deve mapear login, cadastro, recuperação de senha, MFA, API e logout; criar contas de teste; registrar respostas para usuário inexistente, senha errada e login correto; e comparar códigos HTTP, conteúdo, tamanho e tempos. Depois, verificar limitação de tentativas com uma lista pequena e controlada, manipulação de parâmetros e resistência a reutilização de credenciais. O Burp Suite facilita capturar a requisição no Proxy, repeti-la com Repeater e variar campos no Intruder. A observação deve incluir a aplicação e os logs do servidor, sem expor senhas ou tokens no relatório. [2][3][6]

### Passo a passo para teste controlado com Burp Suite

1. Abrir uma **instância própria** de um laboratório de autenticação da Web Security Academy ou um ambiente local isolado e obter uma conta de teste.
2. Acessar o login no navegador do Burp, enviar uma tentativa inválida e localizar o `POST /login` em **Proxy → HTTP history**.
3. Enviar a requisição ao **Intruder**, marcar somente o campo da senha com `§...§` e selecionar **Sniper**. Manter o usuário de teste fixo.
4. Inserir uma lista **curta** de senhas fictícias autorizadas, limitar a taxa e o número de requisições e iniciar o teste.
5. Comparar status, tamanho, redirecionamento e mensagem das respostas; verificar se há atraso progressivo, bloqueio temporário, MFA ou alerta. Confirmar qualquer resultado aparente com uma única requisição no Repeater e interromper o teste.
6. Registrar apenas o comportamento observado, o escopo e os limites do teste. Não usar o procedimento em contas ou serviços de terceiros sem autorização. [6]

Para testar sessões, guardar o identificador de uma sessão de teste, efetuar logout e tentar uma única reutilização na área autenticada: o esperado é rejeição pelo servidor. Repetir após o prazo de inatividade e após o prazo absoluto, verificando que ambos são aplicados no servidor. Testar também se o identificador muda após autenticação e se páginas privadas devolvem cabeçalhos de cache apropriados. O guia da OWASP recomenda observar logout e tentativas de repetição de artefatos de autenticação. [4][7]

**Execução real nesta entrega:** o código Java local foi compilado e executado; os resultados estão em [`teste-local.txt`](P2-13/evidencias/teste-local.txt). Nenhum teste Burp, laboratório PortSwigger ou instância Juice Shop foi executado nesta P2-13. O passo a passo acima é um protocolo proposto, não evidência de exploração realizada.

## 5. Cenário de e-commerce (simulação conceitual)

Uma loja que aceita tentativas ilimitadas pode sofrer tomada de contas, fraude em compras, exposição de endereços e dados pessoais, além de custo operacional com contestação e atendimento. A proteção recomendada inclui MFA para acesso e operações sensíveis, senhas não comprometidas, limitação adaptativa de tentativas, detecção de padrões anômalos, autenticação reforçada em alterações críticas e sessões revogáveis. [1][2]

**Simulação hipotética:** um atacante obtém pares de credenciais de um vazamento externo e testa uma amostra contra o login da loja. Uma combinação reutilizada funciona; a conta passa a apresentar login de dispositivo/local incomum e alteração de endereço de entrega. A equipe detecta aumento de falhas distribuídas, seguida de alguns sucessos anômalos. Responde bloqueando a campanha com limitação de taxa, revogando sessões das contas afetadas, exigindo redefinição de senha e MFA, examinando alterações e compras, notificando os titulares e preservando logs sem registrar segredos. Esse incidente **não foi observado nem executado**; descreve um plano de detecção e resposta. [1][2]

## 6. Conclusão

Falhas persistem porque os fluxos de login, recuperação, MFA e sessão são interdependentes e mudam com novos dispositivos, APIs e requisitos de usabilidade. Escolhas de conveniência precisam ser avaliadas pelo risco: permitir login apenas com senha reutilizável, sem MFA e sem limitação de tentativas, reduz atrito imediato, mas abre caminho para credential stuffing. Uma opção mais equilibrada é MFA resistente a phishing quando possível, com desafios adicionais proporcionais ao risco e recuperação de conta protegida. [1][2]

## Evidências e limites

- **Comprovado nesta entrega:** compilação e execução local do exemplo Java, com as verificações e a saída registradas no arquivo de evidência.
- **Não comprovado:** execução de Burp, PortSwigger, Juice Shop ou incidentes reais.
- **Imagens anteriores:** as três capturas compartilhadas para **I2-08** tratam daquela atividade e não comprovam os exercícios desta **P2-13**; por isso não foram incorporadas aqui.
- O enunciado contém duas cópias idênticas da seção “Exemplos Práticos de Vulnerabilidades”; suas perguntas foram respondidas uma vez na seção 3.

## Fontes

1. [OWASP Top 10:2021 — A07 Identification and Authentication Failures](https://top10.owasp.org/2021/A07_2021-Identification_and_Authentication_Failures/).
2. [OWASP — Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html).
3. [PortSwigger — Authentication vulnerabilities](https://portswigger.net/web-security/authentication) e [password-based login](https://portswigger.net/web-security/authentication/password-based).
4. [OWASP — Session Management Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html).
5. [OWASP — Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html).
6. [PortSwigger — Getting started with Burp Intruder](https://portswigger.net/burp/documentation/desktop/tools/intruder/getting-started).
7. [OWASP WSTG — Testing for Logout Functionality](https://wstg.owasp.org/latest/4-Web_Application_Security_Testing/06-Session_Management/06-Logout_Functionality/).

