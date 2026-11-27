# Prompt 18: Security Logging and Monitoring Failures (A09:2021)

**Aluno:** Guilherme Abade
**Referência:** [OWASP A09:2021](https://owasp.org/Top10/A09_2021-Security_Logging_and_Monitoring_Failures/)

Os exemplos de código e o roteiro com o Juice Shop destinam-se a laboratório próprio ou autorizado.

## 1. Introdução

A categoria *Security Logging and Monitoring Failures* corresponde à falha de não registrar, não monitorar ou não alertar sobre eventos de segurança. Sem registro e monitoramento adequados, os ataques passam despercebidos, a resposta atrasa e a investigação posterior ao incidente torna-se inviável.

A falha caracteriza-se quando eventos de segurança relevantes, como login, falha de login, acesso negado e alteração de dados críticos, não são registrados ou o são sem informação útil, quando inexistem alertas em tempo real e quando os logs permanecem apenas localmente, sem proteção, retenção e análise. A categoria ascendeu ao nono lugar em 2021, por votação da comunidade, e é difícil de avaliar em um teste de intrusão tradicional, embora seu impacto seja elevado, pois, segundo a OWASP, a maioria das violações leva meses para ser detectada, quase sempre por terceiros, e não pelo próprio monitoramento.

A relação com a detecção de ataques é estreita, uma vez que o registro e o monitoramento constituem a camada de detecção: sem eles, não há como perceber a força bruta, a exfiltração ou o abuso de credenciais enquanto ocorrem, mas apenas depois do dano. Em produção, onde estão os dados e os usuários reais, a ausência de monitoramento permite ao atacante operar livremente por longos períodos, o que amplia o prejuízo. O caso Equifax, de 2017, ilustra a questão: a exploração de uma falha no Apache Struts concedeu acesso a dados de cerca de 147 milhões de pessoas, e o ataque permaneceu meses sem ser detectado, entre outros motivos porque um certificado expirado deixou um dispositivo de inspeção de tráfego sem monitorar, de modo que o tráfego malicioso não foi observado; os impactos foram organizacionais, com acordo de até cerca de 700 milhões de dólares, troca de executivos e grave dano reputacional, e técnicos, com meses de acesso indevido e exfiltração contínua.

## 2. Causas e sintomas

Os sintomas comuns incluem eventos de autenticação e de autorização não registrados, ou apenas os de sucesso, logs sem contexto, a ausência de alertas e de correlação, logs mantidos somente em console ou arquivo local, sem centralização nem retenção, logs passíveis de apagamento ou alteração e a falta de análise, quando ninguém os examina. O log insuficiente ou desativado registra pouco, ao omitir falhas de login, acessos negados e mudanças de privilégio, ou nada, o que deixa pontos cegos.

A correlação entre eventos e os alertas em tempo real têm importância decisiva, pois eventos isolados pouco revelam, ao passo que, correlacionados, muitas falhas de login do mesmo endereço seguidas de um sucesso e do acesso a dados sensíveis denunciam um ataque, e o alerta em tempo real possibilita a reação durante o incidente, e não semanas depois. Notificar acessos não autorizados é crítico porque o acesso negado repetido, o uso de conta em horário ou local anômalo e as tentativas de elevação sinalizam um ataque em curso que, sem alerta, passa despercebido. Logs inadequados prejudicam a investigação posterior, visto que, sem registros completos e íntegros, não se consegue reconstruir o ocorrido, o alcance, os dados vazados e a forma de fechar a brecha, o que também compromete obrigações legais de notificação sob a LGPD.

## 3. Boas práticas de logging e monitoramento

A OWASP recomenda registrar, entre outros, o login de sucesso e de falha e o logout, as falhas de autorização e os acessos negados, as alterações de conta, senha, papel e privilégio, as falhas de validação de entrada no servidor, as transações sensíveis, como as financeiras e a exportação de dados, e os erros do servidor e os eventos administrativos. A integridade dos logs assegura-se por sua centralização em um servidor separado, de preferência apenas acrescível, com proteção contra alteração e apagamento e controle de acesso; a confidencialidade impõe que nunca se registrem dados sensíveis, como senha, token, cartão ou dados pessoais em excesso, com mascaramento do necessário e atenção à retenção e à LGPD. Convém adotar formato estruturado, horário sincronizado e identificador de correlação.

O SIEM, sistema de gestão de informações e eventos de segurança, centraliza, normaliza e correlaciona logs de várias fontes, como aplicação, sistema operacional, rede e nuvem, e dispara alertas, por meio de soluções como Wazuh, Elastic Security e Splunk. Seu papel na detecção consiste em cruzar eventos de origens distintas e aplicar regras e linhas de base para identificar padrões de ataque que, isolados, não chamariam atenção, e sua análise em tempo real processa o fluxo continuamente, gera alertas e painéis e aciona a resposta, inclusive automática em soluções mais avançadas.

## 4. Exemplos práticos

O trecho a seguir registra o evento de forma insegura.

```java
try {
    login(user, password);
} catch (Exception e) {
    System.out.println("Erro ao logar");
}
```

Faltam a identificação de quem tentou, a origem, o momento e o tipo da falha; emprega-se `System.out.println`, que não é um registrador e carece de nível, destino gerenciado e rotação; a mensagem não distingue senha incorreta, usuário inexistente e erro de sistema; a exceção é suprimida em um bloco silencioso, o que elimina a causa e o rastro; e não se diferencia a falha esperada, por credencial inválida, do erro inesperado. A reescrita a seguir aplica as boas práticas.

```java
private static final Logger log = LoggerFactory.getLogger(AuthService.class);

public boolean autenticar(String usuario, String ip) {
    try {
        boolean ok = login(usuario);
        if (ok) {
            log.info("LOGIN_SUCESSO usuario={} ip={} ts={}",
                     sanitize(usuario), ip, Instant.now());
        } else {
            // evento de segurança: falha de autenticação, em nível de alerta
            log.warn("LOGIN_FALHA motivo=credencial_invalida usuario={} ip={} ts={}",
                     sanitize(usuario), ip, Instant.now());
        }
        return ok;
    } catch (Exception e) {
        String cid = UUID.randomUUID().toString();
        log.error("LOGIN_ERRO usuario={} ip={} ts={} cid={}",
                  sanitize(usuario), ip, Instant.now(), cid, e);  // causa registrada
        throw new AuthException("Falha de autenticação. Código: " + cid);
    }
}
// sanitize remove quebras de linha para impedir a injeção em log
```

A versão corrigida utiliza um registrador com níveis apropriados, registra o usuário, o endereço, o momento e o tipo da falha, nunca registra a senha, não suprime a exceção, acrescenta um identificador de correlação e sanitiza a entrada contra injeção em log; esses registros alimentam o SIEM, que alerta diante de muitas falhas do mesmo endereço.

## 5. Prática com ferramentas reais

O roteiro em laboratório local inicia pela subida do Juice Shop com `docker run --rm -p 3000:3000 bkimminich/juice-shop`, acompanhando a saída com `docker logs -f`. Em seguida, simulam-se ataques, como várias tentativas de login com senha incorreta, o acesso à área administrativa sem permissão e o envio de entrada maliciosa em um campo de busca, e verifica-se o que é efetivamente registrado. Espera-se observar que a aplicação, deliberadamente insegura, não registra de forma adequada muitos eventos de segurança, de maneira que as tentativas malsucedidas de login e os acessos indevidos tipicamente não geram log ou alerta útil, o que demonstra na prática a categoria, já que um atacante poderia iterar sem deixar rastro detectável; o exercício é relacionar quais eventos deveriam ser registrados e não são e propor onde inserir o registro.

## 6. Aplicação em cenários reais

Como responsável de segurança de um comércio eletrônico, monitoraria os logins de sucesso e de falha, a força bruta e o *credential stuffing*, os acessos negados e as tentativas de elevação de privilégio, as mudanças de conta, senha, endereço de e-mail e dados de pagamento, as transações anômalas em valor ou volume, o uso de múltiplos cartões por conta, a exportação ou consulta em massa de dados, as ações administrativas e os picos de erro do servidor. Para garantir a detecção e o alerta de acesso indevido, centralizaria os logs em um SIEM, definiria regras de correlação, estabeleceria linhas de base de comportamento, configuraria alertas em tempo real e integraria a resposta, com bloqueio de endereço ou conta e exigência de múltiplos fatores.

Em um plano de resposta a incidentes diante da ausência de coleta de logs, faltariam evidências como a linha do tempo do ataque, a origem, as contas afetadas, os dados acessados e o vetor utilizado, o que dificultaria a investigação e a notificação sob a LGPD. As mudanças a propor na política de logging incluem registrar todos os eventos de segurança relevantes, centralizar com integridade e retenção adequadas, sincronizar os relógios, proibir o registro de dados sensíveis, implantar um SIEM com alertas e testar periodicamente, por meio de ataques simulados, se o monitoramento de fato detecta as ameaças.

## 7. Conclusão

O registro e o monitoramento constituem a camada de detecção da segurança, de modo que, sem eles, os ataques permanecem invisíveis por meses, como no caso Equifax, a resposta atrasa e a investigação fica comprometida. As boas práticas consistem em registrar os eventos pertinentes com contexto de quem, de onde, quando e com que resultado, sem dados sensíveis, centralizar com integridade em um SIEM, correlacionar e alertar em tempo real e validar o monitoramento por meio de ataques simulados. A detecção precoce reduz de forma expressiva o tempo de permanência do atacante e o impacto do incidente.
