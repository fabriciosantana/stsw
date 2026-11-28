# Prompt 18: OWASP Top Ten: Security Logging and Monitoring Failures (A09:2021)

Referência: [OWASP A09:2021](https://owasp.org/Top10/A09_2021-Security_Logging_and_Monitoring_Failures/)

> Nota: a parte do Juice Shop descreve o procedimento e o que se espera observar; não incluí saídas de log reais capturadas.

## 1. Introdução

A falha ocorre quando eventos relevantes de segurança não são registrados, os logs são insuficientes ou não protegidos, e ninguém monitora nem recebe alertas, de forma que ataques em curso não são detectados nem investigados.

- **Por que continua relevante:** logging é considerado "chato" e raramente é testado; é difícil avaliar sem simular ataques; os testes de penetração em geral não disparam alertas. Como é difícil medir, a categoria veio da pesquisa da comunidade e permanece na lista (A09).
- **Relação com a detecção de ataques:** sem logs e alertas, o atacante permanece no ambiente por meses. O tempo médio para identificar uma violação costuma ser de mais de 200 dias, e muitas vezes quem descobre é um terceiro.
- **Risco crítico em produção:** não é possível detectar, conter, investigar nem provar o que aconteceu (exigência legal da LGPD, que obriga a comunicar incidentes), e o dano cresce com o tempo.
- **Caso Equifax (2017):** a vulnerabilidade do Apache Struts (CVE-2017-5638) não foi corrigida; o certificado do dispositivo de inspeção de tráfego estava expirado havia cerca de 19 meses, então o tráfego criptografado de saída não foi inspecionado e os invasores extraíram dados por 76 dias sem alerta. Foram expostos dados de ~147 milhões de pessoas.
- **Impactos:** técnicos: detecção tardia, perda de evidências, dificuldade para determinar o escopo; organizacionais: multa/acordo de cerca de US$ 575 milhões ou mais, queda de confiança, saída de executivos, custos de monitoramento de crédito para as vítimas.

## 2. Causas e sintomas

- **Log insuficiente ou desativado:** não se registram logins (sucesso e falha), falhas de controle de acesso e validação de entrada; o nível de log em produção está em ERROR e perde eventos de segurança; os logs só ficam no console/local e se perdem; ou o log existe mas ninguém o lê.
- **Sintomas comuns:** testes de penetração/scans (DAST) não geram alertas; alertas sem prioridade ou ignorados; logs sem contexto (quem, quando, de onde, o quê); logs só locais; logs que podem ser alterados pelo atacante; incidentes descobertos por clientes ou terceiros.
- **Ausência de alertas e resposta:** sem alerta em tempo (quase) real, a equipe só descobre depois de dias ou meses, o atacante tem tempo para escalar privilégios, exfiltrar dados e apagar rastros.
- **Correlação de eventos:** um evento isolado (uma falha de login) é banal; milhares de falhas em um minuto de muitos IPs, seguidos de um sucesso e de acesso a dados em massa, mostram um ataque. Correlacionar eventos de fontes diferentes (aplicação, WAF, SO, rede) permite perceber isso e disparar alertas.
- **Notificar acessos não autorizados:** é o que dá a chance de agir (bloquear conta/IP, revogar sessões) antes do dano; também é obrigação regulatória comunicar incidentes com dados pessoais.
- **Impacto na investigação pós-incidente:** sem logs não há linha do tempo, não se sabe o que foi acessado, qual o alcance nem como o atacante entrou, impossibilitando a correção da causa, a comunicação correta e a defesa legal.

## 3. Boas práticas (OWASP)

- **Eventos a registrar:** logins e falhas, logouts, mudanças de senha/permissões, falhas de controle de acesso, falhas de validação de entrada, uso de funções administrativas, erros de aplicação relevantes, acessos a dados sensíveis, transações de alto valor e mudanças de configuração. Cada registro com: *timestamp* (UTC), usuário/ID, IP de origem, ação, recurso, resultado e identificador de correlação.
- **Não registrar:** senhas, tokens, chaves, dados de cartão e dados pessoais em claro (mascarar).
- **Integridade e confidencialidade:** enviar os logs para um servidor central separado (append-only/WORM), com controle de acesso e criptografia em trânsito/repouso, hashing/assinatura para detectar adulteração, retenção definida e sincronização de horário (NTP). Sanitizar a entrada registrada para evitar *log injection* (quebras de linha).
- **SIEM (Security Information and Event Management):** plataforma que coleta logs de muitas fontes, normaliza, armazena, correlaciona e gera alertas e painéis (exemplos: Splunk, Elastic Security, Wazuh, Microsoft Sentinel, QRadar).
- **Papel na detecção de ataques e análise em tempo real:** aplica regras de correlação (força bruta, viagem impossível, escalada de privilégios), usa inteligência de ameaças e análise de anomalias, envia alertas imediatos e facilita a investigação com buscas sobre todo o histórico.

## 4. Exemplo prático

```java
try {
    login(user, password);
} catch (Exception e) {
    System.out.println("Erro ao logar");
}
```

- **O que falta:** só imprime no console (não em um sistema de log), sem timestamp, sem usuário, sem IP, sem tipo de falha, sem stack trace/causa (`e` é ignorada), sem distinguir senha errada de erro do sistema, sem nível/categoria de segurança, sem sucesso registrado e sem alerta.
- **Como tornar mais útil:** registrar com um logger com nível apropriado, contexto (usuário, IP, user-agent, ID de correlação), tipo de falha, e eventos de sucesso e de falha; nunca registrar a senha.

Reescrita com boas práticas:

```java
private static final Logger audit = LoggerFactory.getLogger("AUDIT");

public void autenticar(String user, String password, HttpServletRequest req) {
    String ip = req.getRemoteAddr();
    String userSeguro = user.replaceAll("[\\r\\n]", "_");     // evita log injection
    try {
        login(user, password);
        audit.info("evento=LOGIN_SUCESSO usuario={} ip={} ts={}", userSeguro, ip, Instant.now());
    } catch (BadCredentialsException e) {
        audit.warn("evento=LOGIN_FALHA tipo=CREDENCIAL_INVALIDA usuario={} ip={} ts={}", userSeguro, ip, Instant.now());
        contador.registrarFalha(userSeguro, ip);             // alimenta alerta de força bruta
        throw e;
    } catch (LockedException e) {
        audit.warn("evento=LOGIN_FALHA tipo=CONTA_BLOQUEADA usuario={} ip={} ts={}", userSeguro, ip, Instant.now());
        throw e;
    } catch (Exception e) {
        audit.error("evento=LOGIN_ERRO usuario={} ip={} ts={}", userSeguro, ip, Instant.now(), e);
        throw e;
    }
}
```

## 5. Prática com o Juice Shop

Procedimento (instância local do Juice Shop, rodando com `npm start`):

1. Executar ações de ataque (por exemplo, SQL injection no login `' OR 1=1--`, tentativas de força bruta de senha, acesso a `/ftp` e a `#/administration`) e observar o console e o diretório `logs/` da aplicação.
2. **O que se espera ver:** o Juice Shop registra principalmente logs de acesso HTTP (via Morgan) e mensagens de inicialização, e notificações de desafios resolvidos; ele não registra de forma estruturada eventos de segurança como falhas de login, tentativas de injeção ou falhas de autorização, nem gera alertas. Isso está ligado ao desafio de "Security Logging" do app (que explora o acesso ao diretório de logs `/support/logs`).
3. **Eventos que não são registrados:** falhas de autenticação com identificação do usuário/IP, payloads suspeitos de injeção, acessos negados, ações administrativas e alterações de dados.
4. Conclusão: o ataque ocorre sem gerar um alerta; um monitoramento real precisaria de logs de segurança estruturados e regras de alerta.

## 6. Aplicação em cenário real: e-commerce

- **Eventos críticos a monitorar:** falhas e sucessos de login (em volume), troca de e-mail/senha/telefone, redefinição de senha, falhas de autorização (403/401 repetidos), alterações de preço/estoque, ações administrativas, exportações em massa, uso de cupons/fraude em pagamentos, erros 500 em picos, acessos de países/IPs incomuns e tentativas de injeção bloqueadas pelo WAF.
- **Garantir detecção e alerta de acesso indevido:** logs centralizados no SIEM; regras como ">10 falhas em 5 min por IP/usuário", "login de dois países em pouco tempo", "acesso a muitos pedidos de usuários diferentes"; alertas por e-mail/chat/pager com severidade e responsável; testes periódicos (red team/purple team) para confirmar que os ataques geram alerta; runbooks de resposta.
- **Plano de resposta quando os logs não foram coletados:**
  1. *Conter:* isolar sistemas suspeitos, trocar credenciais/chaves, revogar sessões e bloquear indicadores conhecidos.
  2. *Preservar o que existir:* imagens de disco/memória, logs de infraestrutura (CDN, WAF, balanceador, banco, nuvem/CloudTrail, provedor de pagamento), backups e logs de terceiros.
  3. *Reconstruir:* linha do tempo a partir de fontes alternativas (banco de dados, tabelas de auditoria, e-mails, tráfego de rede); presumir o pior alcance.
  4. *Comunicar:* ANPD e titulares, conforme a LGPD; jurídico e provedores.
  5. *Erradicar e recuperar:* corrigir a causa provável, reconstruir os sistemas e monitorar de perto.
  6. *Lições aprendidas.*
- **Evidências que poderiam estar faltando:** logins e IPs de origem, requisições HTTP (corpo/parâmetros), consultas ao banco, acessos a arquivos, ações administrativas, tráfego de saída (exfiltração), alterações de código/configuração e horários exatos.
- **Mudanças na política de logging:** definir os eventos obrigatórios e o formato padrão; centralização no SIEM com retenção (ex.: 12 meses online e mais em arquivo); proteção de integridade; alertas com donos e SLA; testes de detecção periódicos; mascaramento de dados pessoais; revisão trimestral e auditoria.
