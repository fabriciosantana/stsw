# P2-18 — OWASP Top Ten: Security Logging and Monitoring Failures

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Prompt:** [prompts/18-logging.md](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/18-logging.md)


## 1. Introdução à Vulnerabilidade Security Logging and Monitoring Failures

### O que caracteriza uma falha e exemplos reais

A09:2021 ocorre quando eventos relevantes não são registrados, os registros são incompletos ou não são revisados, alertas não funcionam ou a equipe não responde a sinais de ataque. Log sem monitoramento não garante detecção; monitoramento sem dados confiáveis também não.

Um exemplo histórico de grande impacto é a Equifax, cuja violação de 2017 expôs informações pessoais de aproximadamente 147 milhões de pessoas. O relatório do GAO identificou fatores de identificação, detecção, segmentação e governança; o relatório também descreve dificuldades em detectar a vulnerabilidade no portal e a atividade dos invasores. A FTC informou o acordo global de pelo menos US$ 575 milhões. O caso envolveu também vulnerabilidade não corrigida, portanto não deve ser reduzido a um problema exclusivamente de logging. https://www.gao.gov/assets/gao-18-559.pdf https://www.ftc.gov/news-events/news/press-releases/2019/07/equifax-pay-575-million-part-settlement-ftc-cfpb-states-related-2017-data-breach

### Por que continua no Top 10? Relação com detecção de ataques

A OWASP manteve A09:2021 porque logging, monitoramento e resposta permitem detectar, escalar e investigar incidentes. Uma intrusão sem alerta pode continuar por longos períodos, aumentando o alcance e dificultando a delimitação do dano. A categoria também cobre logs com dados sensíveis e log injection, pois logs mal protegidos podem criar novos riscos. https://top10.owasp.org/2021/A09_2021-Security_Logging_and_Monitoring_Failures/

### Por que a falta de monitoramento é crítica em produção?

Sem monitoramento, a equipe pode não saber que contas estão sendo atacadas, que privilégios foram alterados, que dados foram acessados ou que serviços estão indisponíveis. A resposta depende de contexto e velocidade: um evento pode existir em log local, mas sem retenção central, alerta e equipe responsável, dificilmente vira ação a tempo.

### Impactos organizacionais e técnicos

Os impactos podem incluir atraso na contenção, perda de evidências, dificuldade para determinar escopo e duração, restauração mais lenta, custos de investigação e comunicação, sanções contratuais/regulatórias dependendo do contexto e danos de reputação. Tecnicamente, a equipe pode perder a trilha que relaciona usuário, sessão, request, ação e recurso afetado.

## 2. Principais Causas e Sintomas

### Sintomas comuns e impacto da ausência de alertas

- Falhas de login e de autorização não geram eventos.
- Mudanças de privilégio ou configurações administrativas não deixam trilha.
- Erros e exceções não são registrados de forma útil.
- Logs ficam apenas no servidor da aplicação e podem ser apagados pelo atacante.
- Não há sincronização de tempo, correlação de request/session ou retenção definida.
- Alertas são inexistentes, excessivos, ignorados ou não têm responsável.
- Logs não são protegidos contra leitura, alteração, exclusão ou injeção.

A ausência de alertas aumenta o tempo entre ataque, descoberta, contenção e recuperação. A ausência de logs também impede confirmar se um alerta falso ou verdadeiro corresponde a comportamento real.

### O que significa log insuficiente ou desativado?

É deixar de registrar eventos necessários para auditoria e resposta, ou registrar informação incompleta, inconsistente, sem contexto, sem timestamp confiável ou sem retenção. Logging desativado pode ser uma opção deliberada em modo de produção, uma configuração errada ou resultado de exceções silenciosas.

### Importância de correlação e alertas em tempo real

A correlação combina eventos da aplicação, identidade, rede, banco e infraestrutura. Ela permite detectar padrões distribuídos, por exemplo, muitos logins falhos seguidos de sucesso e exportação de dados. Alertas quase em tempo real reduzem a janela para abuso; precisam incluir severidade, contexto e fluxo de escalonamento.

### Por que notificar acessos não autorizados?

Tentativas negadas podem indicar enumeração, força bruta, uso de credenciais roubadas ou abuso de privilégio. A equipe pode bloquear sessões, investigar contas relacionadas e aumentar observação. Eventos isolados podem ser ruído; a detecção depende de volume, contexto, identidade e comportamento.

### Efeito na investigação pós-incidente

Se não há logs íntegros e centralizados, pode ser impossível estabelecer a sequência de ações, o usuário/serviço afetado, os dados consultados, o momento da intrusão e a efetividade de uma correção. Isso dificulta comunicação responsável e cumprimento de obrigações de auditoria.

## 3. Boas Práticas de Logging e Monitoramento

### Melhores práticas recomendadas pela OWASP

- Definir eventos relevantes por risco e requisitos de auditoria.
- Registrar tentativas bem-sucedidas e falhas de autenticação, autorização e validação no servidor.
- Usar formato estruturado e consistente, com horário UTC e identificador de correlação.
- Centralizar logs, restringir acesso e proteger contra alteração e exclusão.
- Sanitizar/estruturar dados para evitar injeção e preservar confidencialidade.
- Nunca registrar senhas, tokens de sessão, chaves privadas ou dados pessoais desnecessários.
- Definir retenção e descarte compatíveis com finalidade e política.
- Monitorar alertas, medir tempos de resposta e exercitar o plano de incidentes.
- Garantir que o próprio sistema de logging seja observável e tenha alertas de falha.
- Verificar detecções em testes autorizados e em exercícios de resposta.

A OWASP orienta a registrar falhas de login, controle de acesso e validação com contexto suficiente, proteger trilhas de transações de alto valor e estabelecer monitoramento e resposta. https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html

### Quais eventos devem ser registrados?

Conforme risco e política: autenticação (sucesso/falha), MFA, encerramento de sessão, bloqueio/desbloqueio de conta, negação de autorização, alteração de privilégios, mudanças administrativas, exportação e acesso a dados sensíveis, transações de alto valor, alterações de configuração, falhas de validação relevantes, exceções, alterações de registros auditáveis, deploys e ações de credenciais de serviço.

Registrar o necessário para investigar, sem copiar o segredo ou conteúdo sensível para o log. Para autenticação, usar identificador de usuário apropriado, request ID, origem de rede confiável e tipo de falha; nunca registrar a senha.

### Como garantir integridade e confidencialidade dos logs?

- Enviar logs para serviço central com identidade de escrita restrita e acesso de leitura mínimo.
- Usar TLS no transporte e criptografia em repouso conforme classificação.
- Aplicar permissões e retenção, trilha de auditoria e armazenamento imutável/append-only quando necessário.
- Separar administração do sistema de produção e do sistema de logs.
- Evitar segredos e mascarar/anonimizar campos.
- Escapar caracteres de controle e normalizar entradas para prevenir log injection.
- Monitorar falhas de entrega, volume anômalo e interrupção de coleta.

### Conceito e papel de SIEM

SIEM (Security Information and Event Management) reúne eventos de várias fontes, normaliza e correlaciona dados, aplica regras analíticas e gera alertas/dashboards para investigação. Pode auxiliar detecção quase em tempo real e busca retrospectiva, mas depende de fontes bem configuradas, qualidade dos eventos, regras mantidas e analistas/processo de resposta. SIEM não é garantia automática contra incidentes.

## 4. Exemplos Práticos: revisão do código Java

Trecho fornecido:

~~~java
try {
    login(user, password);
} catch (Exception e) {
    System.out.println("Erro ao logar");
}
~~~

### O que está faltando?

O registro não informa timestamp confiável, tipo da falha, identificador do usuário, origem de rede, request/session correlation ID, resultado nem contexto para detectar padrões. Captura toda exceção e a descarta, o que pode esconder falha de infraestrutura e dificultar diagnóstico. Também escreve no stdout sem política de estrutura, acesso, retenção ou encaminhamento. Não se deve registrar a senha nem o token.

### Como tornar útil sem registrar segredo

O exemplo abaixo usa SLF4J com placeholders; o appender deve emitir JSON ou outro formato estruturado, adicionar timestamp UTC e encaminhar ao coletor. clientIp deve ser calculado usando a conexão ou proxy confiável configurado, e os valores externos precisam ser sanitizados para impedir log injection.

~~~java
private static final Logger log = LoggerFactory.getLogger(AuthService.class);

try {
    login(user, password);
    log.info("event=auth_success userId={} sourceIp={} requestId={}",
             userId, trustedClientIp, requestId);
} catch (InvalidCredentialsException e) {
    log.warn("event=auth_failure userId={} sourceIp={} requestId={} reason={}",
             userId, trustedClientIp, requestId, "invalid_credentials");
    throw e;
} catch (RuntimeException e) {
    log.error("event=auth_error userId={} sourceIp={} requestId={} errorType={}",
              userId, trustedClientIp, requestId,
              e.getClass().getSimpleName(), e);
    throw e;
}
~~~

Em produção, evitar registrar mensagens de exceção que possam conter dados sensíveis. A exceção técnica pode ser registrada com controle de acesso; resposta ao usuário continua genérica. As chamadas não substituem política para retentativas, bloqueios, MFA e alertas.

## 5. Prática com Ferramentas Reais — OWASP Juice Shop

### Plano para identificar falhas e eventos não registrados

A lista oficial de desafios da Juice Shop inclui desafios de observabilidade como **Access Log** e **Exposed Metrics**. A existência desses desafios demonstra que a aplicação de treinamento contém superfícies de exposição relacionadas à observabilidade; não permite concluir, sem executar e inspecionar o ambiente, quais eventos não são registrados ou quais alertas existem. https://pwning.owasp-juice.shop/companion-guide/local/part2/README.html

Procedimento local proposto:

1. Iniciar Juice Shop localmente conforme guia oficial e restringir o teste ao host local.
2. Abrir console do processo/container e preparar acesso aos logs configurados.
3. Pelo navegador, realizar ações controladas: login inválido, acesso a rota não autorizada e solicitação normal.
4. Correlacionar horário, usuário de teste e request no console/logs; verificar quais eventos aparecem, que campos são registrados e se segredos foram excluídos.
5. Confirmar se logs persistem após reinicialização e se são enviados a destino central no ambiente testado.
6. Inspecionar, no próprio laboratório, superfícies de observabilidade e logs expostos descritos no Companion Guide; verificar status, conteúdo e controle de acesso sem explorar sistemas externos.
7. Registrar evidências com versão, configuração, request/response e fragmentos de log redigidos.

**Não declarar ausência de um evento sem observar os logs configurados.** Não ver uma mensagem no terminal também não prova ausência de auditoria em outra sink.

### Simule um ataque e verifique logs

Em um laboratório local autorizado, enviar algumas tentativas controladas de login inválido usando uma conta de teste; registrar horário e request ID e examinar o log da aplicação/container. Depois fazer uma requisição a endpoint de teste sem permissão, observar se há evento de negação e confirmar se alerta foi acionado. Não realizar força bruta nem causar indisponibilidade.

**Resultado nesta entrega:** a simulação não foi executada; não há logs ou alertas observados. O procedimento acima descreve como coletar a evidência.

## 6. Aplicação em Cenário Real: e-commerce

### Quais eventos críticos monitorar?

- Logins, falhas, reset de senha, MFA e recuperação de conta.
- Mudanças em endereço, e-mail, método de pagamento, senha e privilégios.
- Negativas de autorização e acesso a dados de outros clientes.
- Criação/alteração de pedidos, reembolsos, cupons e preços.
- Exportações, consultas em massa e acesso administrativo.
- Mudanças de configuração, deploy e acesso de contas de serviço.
- Erros inesperados, falhas de integração, indisponibilidade e alterações de logging.

### Como detectar e alertar acesso indevido?

Centralizar logs estruturados; correlacionar identidade, sessão, IP/origem, dispositivo, recurso e request ID; criar alertas para picos de falhas, login suspeito seguido de exportação, alteração de privilégio, acesso em massa e ausência inesperada de eventos. Definir severidade, responsável, canal de escalonamento, limiar e procedimento de confirmação. Medir falsos positivos, tempo até triagem e tempo até contenção.

### Plano de resposta quando os logs não foram coletados

1. **Conter:** desabilitar credenciais/sessões comprometidas, limitar acesso ou isolar componente conforme impacto.
2. **Preservar:** não sobrescrever discos, backups, logs de proxy, cloud audit, WAF, IAM, SIEM, CDN, banco ou provedores.
3. **Reconstruir:** estabelecer linha do tempo a partir de fontes independentes, deploys, tickets, snapshots e registros de terceiros; marcar lacunas e nível de confiança.
4. **Determinar escopo:** listar contas, dados e sistemas potencialmente envolvidos; não presumir que ausência de registro significa ausência de acesso.
5. **Comunicar e cumprir obrigações:** acionar liderança, jurídico, privacidade e partes aplicáveis conforme política e legislação.
6. **Recuperar:** remover persistência, corrigir vulnerabilidade, rotacionar segredos e validar integridade antes de restabelecer serviço.
7. **Melhorar:** ativar logs críticos centralizados, retenção, alertas de perda de coleta, testes de detecção e exercícios de incidente.

**Evidências que podem faltar:** identidade do atacante, requisições, recursos consultados, duração, exfiltração, alteração de dados, persistência e exato momento da intrusão.

**Mudanças na política:** matriz de eventos por risco, formato e campos obrigatórios, proteção/retention, alertas e escalonamento, revisão de acessos, testes periódicos, proibição de segredos em logs e procedimento de preservação forense.

## 7. Conclusão

Logging e monitoramento são controles de visibilidade e resposta. Eventos incompletos, sem proteção ou sem alertas atrasam a contenção e podem tornar impossível reconstruir um incidente.

### Síntese de mitigação

Registrar eventos relevantes com contexto e sem segredos; usar estrutura consistente e tempo sincronizado; centralizar e proteger os logs; gerar alertas acionáveis; monitorar a saúde da própria coleta; manter resposta a incidentes e testar periodicamente os cenários de detecção.

## Referências

- OWASP Top 10:2021 A09: https://top10.owasp.org/2021/A09_2021-Security_Logging_and_Monitoring_Failures/
- OWASP Logging Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Logging_Cheat_Sheet.html
- OWASP Juice Shop Companion Guide: https://pwning.owasp-juice.shop/companion-guide/latest/part2/README.html
- U.S. GAO, GAO-18-559: https://www.gao.gov/assets/gao-18-559.pdf
- FTC — acordo da Equifax: https://www.ftc.gov/news-events/news/press-releases/2019/07/equifax-pay-575-million-part-settlement-ftc-cfpb-states-related-2017-data-breach
