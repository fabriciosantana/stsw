# P2-19 — OWASP Top Ten: Cryptographic Failures

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Prompt:** [prompts/19-cryptographic.md](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/19-cryptographic.md)

> O documento apresenta análise e procedimentos de laboratório. Não foram executados scanner TLS, secret scanner, SCA, benchmark ou aplicação de exemplo nesta sessão. Nenhuma medição ou achado de ferramenta é declarado como resultado real.

## 1. Introdução à Vulnerabilidade Cryptographic Failures

### O que caracteriza falhas criptográficas?

A02:2021 trata de falhas que expõem dados sensíveis por ausência de criptografia ou por uso incorreto de algoritmos, protocolos, parâmetros, chaves, IVs/nonces, hashing e validação de certificados. O risco começa com a classificação do dado: identificar o que precisa de proteção em trânsito e em repouso e por quanto tempo deve ser mantido. https://top10.owasp.org/2021/A02_2021-Cryptographic_Failures/

### Por que substitui “Sensitive Data Exposure”?

“Exposição de dados sensíveis” descrevia um resultado amplo. A categoria A02:2021 passa a enfatizar uma causa que pode levar a esse resultado: proteção criptográfica ausente ou inadequada. Dados podem ser expostos por outras falhas também; a nova denominação direciona a revisão para decisões de criptografia, armazenamento e gerenciamento de chaves.

### Objetivos criptográficos e como podem falhar

- **Confidencialidade:** apenas autorizados leem o dado. Falha com HTTP sem TLS, cifra obsoleta, chave exposta ou dado em claro.
- **Integridade:** alterações não autorizadas são detectadas. Falha com armazenamento sem MAC/tag/assinatura ou cifra sem autenticação.
- **Autenticidade:** origem/identidade é validada. Falha ao aceitar certificado inválido, assinatura não verificada ou token sem validação.
- **Não repúdio:** há evidência verificável de autoria/ação. Assinatura digital pode apoiar esse objetivo, mas depende de gestão da chave privada, identidade e trilha auditável; criptografia isolada não garante não repúdio legal.

### Três exemplos reais ou documentados

1. **Tráfego sem TLS:** em 2010, o Firesheep demonstrou roubo de cookies e sequestro de sessões em redes públicas quando sites protegiam apenas o login, mas não todo o tráfego autenticado. É uma demonstração pública de ataque, não a alegação de um único incidente empresarial. Mozilla descreveu a exploração e recomendou HTTPS/HSTS para impedir exposição de cookies. https://blog.mozilla.org/security/2010/10/27/cooling-down-the-firesheep/
2. **Hashing fraco de senhas:** após o incidente de 2012, o LinkedIn informou ter migrado de hashes sem salt para hashes com salt; em 2016, informou que dados da violação de 2012 foram publicados e invalidou senhas de contas que não haviam sido alteradas. O caso ilustra como hashes rápidos/sem salt facilitam cracking offline. https://news.linkedin.com/2012/06/linkedin-summarizes-password-theft-and-member-security-efforts https://www.linkedin.com/help/linkedin/answer/a1338522/notice-of-data-breach-may-2016
3. **Segredo exposto em repositório:** no incidente Uber de 2016, invasores encontraram credencial AWS em código de repositório privado, usaram a chave para acessar armazenamento S3 e obter arquivos. A FTC descreveu o caso e o DOJ registrou que os hackers usaram credenciais roubadas para acessar repositório e obter uma chave privada. https://www.ftc.gov/business-guidance/blog/2018/04/ftc-addresses-ubers-undisclosed-data-breach-new-proposed-order https://www.justice.gov/usao-ndca/pr/uber-enters-non-prosecution-agreement

### Codificação Base64 não é criptografia

Base64 converte bytes para uma representação textual e pode ser revertida por qualquer pessoa sem segredo. Criptografia usa algoritmo e chave para proteger o conteúdo. Tratar Base64 como confidencialidade deixa tokens, senhas ou dados recuperáveis por simples decodificação.

## 2. Principais Causas e Sintomas

### Algoritmos e protocolos obsoletos

MD5 e SHA-1 não são adequados a novas assinaturas/controles que dependem de resistência a colisões; SHA-256 puro é rápido demais para armazenamento de senhas. RC4 e SSLv3 são legados inseguros; protocolos anteriores a TLS 1.2 devem ser desabilitados conforme compatibilidade, priorizando configuração moderna TLS 1.2/1.3. A análise correta inclui biblioteca, configuração do servidor e validação pelo cliente.

### Hash rápido de senha sem salt ou com custo inadequado

MD5/SHA-256 simples permitem testar grandes quantidades de palpites por segundo. Sem salt aleatório por senha, hashes iguais revelam senhas iguais e tabelas pré-computadas são mais úteis. Salt não precisa ser secreto; ele impede pré-computação e reduz comparação entre usuários. Pepper é um segredo adicional opcional guardado fora do banco, em secret manager, e não substitui salt nem um algoritmo lento.

### Segredos hardcoded, em variáveis ou em logs/URLs

API keys, tokens, credenciais e chaves no código podem ser copiadas para histórico Git e caches mesmo após apagar a linha. Variáveis de ambiente também precisam de controle, pois podem vazar por logs, dumps, diagnóstico ou processos. Tokens em query string podem aparecer em histórico, referer, proxies e observabilidade. Ao confirmar exposição, revogar e rotacionar a credencial; apagar o commit não invalida o segredo.

### Reutilização de IV/nonce ou RNG não criptográfico

Modos AEAD como AES-GCM exigem que o nonce não se repita com a mesma chave. Reutilização pode revelar relações entre mensagens e comprometer autenticação. AES-CTR também exige nonce/contador único por chave. IV, nonce e salt não são conceitos idênticos; os requisitos dependem do modo. Use APIs recomendadas e CSPRNG quando requerido, sem criar geradores próprios.

### TLS incorreto, pinning e impacto em detecção/resposta

Certificados inválidos aceitos por um cliente permitem interceptação; cipher suites/protocolos fracos diminuem a proteção. Pinning pode reduzir confiança em CAs para um cliente específico, mas exige plano de rotação e contingência; não é substituto de validação normal de hostname/certificado.

A criptografia não substitui logging: não registrar segredos, mas registrar metadata necessária para auditar acessos e alertar. Falhas criptográficas afetam confidencialidade, integridade ou autenticidade; rotacionar chaves e preservar trilha auditável são parte da resposta.

## 3. Boas Práticas de Criptografia

### Princípios e decisões arquiteturais

- Classificar dado, reduzir coleta e retenção; dados que não são armazenados não podem ser roubados do repositório.
- **Em trânsito:** HTTPS em todas as rotas autenticadas e internas necessárias, TLS moderno, certificado/hostname validados, HSTS após validar todo o domínio e subdomínios.
- **Em repouso:** cifra autenticada, como AES-GCM ou ChaCha20-Poly1305, com chaves geridas em KMS/Secret Manager e acesso mínimo.
- **Senhas:** hash adaptativo com salt aleatório usando Argon2id preferencialmente; bcrypt/scrypt/PBKDF2 quando adequados ao ambiente/requisito. Ajustar custo por benchmark no hardware de produção.
- **Integridade/autenticidade:** assinatura digital para verificar autoria de artefatos/documentos; HMAC para autenticidade/integridade entre sistemas que compartilham segredo. Escolher pelo modelo de confiança.
- **IV/nonce:** obedecer requisitos do modo; não repetir nonce por chave quando exigido; armazenar IV junto do ciphertext quando necessário, pois IV não é segredo.
- **Governança:** classificar, mascarar, limitar retenção, evitar segredos em URL/log e definir rotação, backup e resposta a comprometimento.
- **Testes:** revisar cheat sheets oficiais, SAST/SCA, testes TLS, secret scanning e revisão independente.

A OWASP recomenda algoritmos padronizados atuais, gerenciamento de chaves, TLS com forward secrecy, hash adaptativo com salt e cifra autenticada. https://top10.owasp.org/2021/A02_2021-Cryptographic_Failures/ https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

## 4. Exemplos Práticos em Java

### Hash de senha inseguro

~~~java
String hash = DigestUtils.md5Hex(password); // inseguro para armazenar senha
saveUser(user, hash);
~~~

MD5 é rápido e o exemplo não apresenta salt individual. Um atacante com o banco pode testar dicionários e combinações offline; hashes repetidos revelam senhas repetidas. Rainbow tables são especialmente úteis sem salt.

### Reescrita com Argon2id

Exemplo com Spring Security:

~~~java
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

PasswordEncoder passwordEncoder =
    Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();

String passwordHash = passwordEncoder.encode(password);
saveUser(user, passwordHash);

// Na autenticação:
boolean matches = passwordEncoder.matches(candidatePassword, passwordHash);
~~~

O encoder gera e guarda salt junto dos metadados do hash; não criar salt fixo nem comparar strings diretamente. O parâmetro defaultsForSpringSecurity_v5_8 depende da versão da biblioteca; conferir a API suportada no projeto e medir latência/custo no hardware. Em ambientes que exigem FIPS, avaliar PBKDF2 conforme política. OWASP recomenda Argon2id e algoritmos adaptativos, com custo ajustado. https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html

### AES em ECB inseguro

~~~java
Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
c.init(Cipher.ENCRYPT_MODE, key);
byte[] out = c.doFinal(plaintext);
~~~

ECB cifra blocos iguais de plaintext iguais em ciphertext iguais sob a mesma chave. Isso revela padrões em arquivos/imagens e mensagens estruturadas; não oferece autenticação contra alteração.

### Reescrita com AES-GCM, IV único, AAD e verificação de tag

~~~java
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

record Encrypted(byte[] iv, byte[] ciphertextAndTag) {}

static Encrypted encrypt(byte[] plaintext, byte[] aad, SecretKey key)
        throws Exception {
    byte[] iv = new byte[12]; // 96 bits recomendado para GCM
    new SecureRandom().nextBytes(iv);

    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
    cipher.updateAAD(aad);

    return new Encrypted(iv, cipher.doFinal(plaintext));
}

static byte[] decrypt(Encrypted encrypted, byte[] aad, SecretKey key)
        throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    cipher.init(
        Cipher.DECRYPT_MODE,
        key,
        new GCMParameterSpec(128, encrypted.iv())
    );
    cipher.updateAAD(aad);
    // doFinal valida a tag. Não usar plaintext se a validação falhar.
    return cipher.doFinal(encrypted.ciphertextAndTag());
}
~~~

A chave deve vir de KMS/keystore, nunca de constante no código; o IV deve ser armazenado junto do ciphertext, pois não precisa ser secreto. AAD autentica metadados que não são cifrados, como identificador do registro. Cada IV deve ser único por chave conforme requisitos do GCM; tratar falha da tag como erro de autenticação e não revelar detalhes ao cliente.

### Cliente HTTP OkHttp inseguro e correção

Inseguro:

~~~java
OkHttpClient client = new OkHttpClient.Builder()
    .hostnameVerifier((host, session) -> true) // aceita qualquer hostname
    .build();
~~~

Isso remove a validação de hostname e facilita ataque man-in-the-middle. Não substituir trust manager/verifier por “TrustAll”.

Seguro: usar validação padrão da plataforma e manter provedores/certificados atualizados:

~~~java
OkHttpClient client = new OkHttpClient.Builder().build();
~~~

Se o modelo de ameaça justificar pinning, usar CertificatePinner com pins atuais e alternativos, sobreposição para rotação, plano de emergência e teste de expiração. Nunca desabilitar a validação normal de cadeia/hostname. Rejeitar protocolos obsoletos no servidor e no cliente conforme biblioteca e política; conferir compatibilidade antes de remover versões.

## 5. Prática com Ferramentas Reais

### Auditoria de TLS com testssl.sh ou SSL Labs

Para um serviço local de laboratório que já exponha HTTPS:

~~~bash
testssl.sh --fast https://localhost:8443
~~~

Registrar versões TLS, cipher suites, certificado, HSTS e suporte a forward secrecy. Se a aplicação local falar somente HTTP, configurar um reverse proxy TLS de laboratório antes de testar. SSL Labs é adequado a domínio de teste autorizado e publicamente acessível; não enviar para análise externa serviço privado ou dados sem autorização.

**Não executado:** nenhum scanner TLS foi iniciado; não há versões/ciphers observados.

### Secret scanner com Gitleaks

Executar em cópia local de repositório de exemplo:

~~~bash
gitleaks dir .
~~~

Revisar os achados sem imprimir ou copiar segredos para relatórios. Para segredo confirmado: revogar/rotacionar, identificar uso e acessos durante a janela, mover para Secret Manager/KMS, remover do código/histórico conforme processo e manter monitoramento. O scanner pode gerar falso positivo e não substitui revisão de histórico/configurações externas. https://github.com/gitleaks/gitleaks

**Não executado:** Gitleaks não foi instalado nem rodado nesta sessão; não afirmo existência de segredo no repositório analisado.

### SCA / Dependency-Check

~~~bash
dependency-check.sh --project "crypto-lab" --scan . --format HTML --out ./reports
~~~

Investigar dependências de criptografia e versões sem suporte; validar match, advisory, versão e caminho. Atualizar e repetir a análise, documentando exceções temporárias.

**Não executado:** nenhum relatório de SCA foi produzido.

### Benchmark de hashing: Argon2id, bcrypt e PBKDF2

O benchmark deve usar JMH ou harness controlado na mesma máquina/VM da produção, com a mesma implementação e número representativo de operações. Medir p50/p95 de latência, throughput, memória, CPU, concorrência e impacto em login legítimo; testar resistência a abuso de login e política de rate limiting. Aumentar custo até atingir a meta de latência segura definida pela equipe e repetir em hardware representativo.

| Algoritmo | Benchmark nesta entrega | Como interpretar |
|---|---|---|
| Argon2id | Não medido | Ajustar memória/iterações e paralelismo para dificultar cracking sem tornar login suscetível a DoS |
| bcrypt | Não medido | Ajustar cost; considerar limite de tamanho da senha da implementação |
| PBKDF2 | Não medido | Ajustar iterações conforme política e requisitos de conformidade |

**Não foram medidos tempos ou parâmetros de produção.** Não se deve inventar vencedor numérico; a seleção depende de runtime, hardware, volume e requisitos regulatórios.

## 6. Aplicação em Cenários Reais: banco digital

### Política de proteção por classe e estado

| Classe | Exemplos | Em trânsito | Em repouso |
|---|---|---|---|
| Pública | Conteúdo institucional publicado | HTTPS nas interfaces públicas | Armazenamento conforme integridade/ disponibilidade |
| Interna | Dados operacionais sem identificação sensível | TLS em redes internas conforme arquitetura | Acesso controlado e criptografia conforme risco |
| Confidencial | Dados de clientes e informações comerciais | TLS moderno em todas as conexões | Cifra autenticada com chave gerida e privilégio mínimo |
| Restrita | Credenciais, dados financeiros altamente sensíveis, chaves | TLS, autenticação forte de serviço e segmentação | Cifra autenticada/tokenização; chaves isoladas em KMS/HSM |

Classificação e retenção devem ser aprovadas pela organização, jurídico/privacidade e requisitos regulatórios; a tabela é proposta didática, não parecer de conformidade.

### Quando usar cifra autenticada, assinatura e HMAC?

- **Cifra autenticada (AES-GCM/ChaCha20-Poly1305):** proteger dado confidencial e detectar alteração na decifragem.
- **Assinatura digital:** provar que artefato/documento foi assinado por chave privada correspondente e não foi modificado desde então; consumidor verifica com chave pública.
- **HMAC:** verificar integridade/autenticidade entre partes que compartilham segredo, como requisição interna ou webhook; todos os verificadores que conhecem o segredo também podem produzir MAC.

### Fluxo de gestão de chaves

1. **Geração:** CSPRNG/KMS ou HSM, parâmetros e algoritmo aprovados.
2. **Armazenamento:** KMS/Secret Manager/HSM, sem chave em código, imagem, variável sem controle ou log.
3. **Acesso:** identidade de serviço, menor privilégio, escopo por função e aprovação para operações administrativas.
4. **Uso:** chamada de serviço autorizada; evitar exportar chave quando KMS permite operação criptográfica no serviço.
5. **Logging:** registrar ID da chave, operação, identidade e resultado sem registrar chave ou plaintext sensível.
6. **Rotação:** período definido e rotação acionada por risco; manter versionamento para decifrar dados antigos enquanto migra.
7. **Backup/recuperação:** cópias criptografadas, segregadas e testadas, com divisão de responsabilidades e recuperação auditada.
8. **Revogação/destruição:** procedimento de emergência e política de retenção que considere impacto em dados ainda cifrados.

### SQL Injection com “criptografia transparente” no banco

Se a aplicação decifra automaticamente os dados para responder à consulta e uma SQLi consegue executar a consulta sob a identidade da aplicação, os resultados podem retornar em claro. Criptografia em disco ou transparente do banco não impede que uma aplicação autorizada leia e decifre dados. Defesas em camadas: prepared statements, validação e autorização no servidor, identidade de banco com privilégio mínimo, separar serviço de chave da consulta, criptografia no nível da aplicação quando adequado, segregação de funções, tokenização e minimizar dados armazenados.

### Segredos em URLs e logs

Tokens em query string podem ficar em histórico do navegador, logs de proxy/CDN, analytics, screenshots e cabeçalho Referer. Enviar credenciais em header Authorization/Bearer por TLS, com escopo mínimo e TTL curto; não registrar o header; armazenar token em local seguro no cliente conforme plataforma e modelo de ameaça. Se vazou, revogar imediatamente. Redigir logs e configurar proxies para remover parâmetros sensíveis.

## 7. Testes Automatizados (Gherkin + pipeline)

### Política de hash de senha

~~~gherkin
Dado que um usuário cadastra uma senha
Quando a credencial é persistida
Então o hash deve usar Argon2id ou bcrypt com salt aleatório
E o custo deve atender ao parâmetro aprovado e medido para o ambiente
E não deve existir uso de MD5, SHA-1 ou SHA-256 puro no caminho de persistência
~~~

### Transporte seguro

~~~gherkin
Dado que o serviço expõe endpoints públicos
Quando um cliente tenta conectar via HTTP
Então deve redirecionar para HTTPS ou rejeitar a conexão conforme a política
E HSTS deve estar ativo para os domínios inteiramente preparados para HTTPS
E conexões com TLS abaixo da versão mínima aprovada devem ser rejeitadas
~~~

### Guardrails no CI

- Falhar o build se secret scanner encontrar credencial válida não aprovada.
- Falhar ou exigir exceção revisada para biblioteca criptográfica vulnerável/fora de suporte.
- Falhar se testes automatizados detectarem hashing simples de senha ou configuração TLS fraca.
- Assinar artefatos e verificar assinatura/proveniência antes da publicação.
- Não imprimir segredos em logs; executar scan com permissões mínimas.

## 8. Reflexão e Avaliação

### Base64, criptografia simétrica, assimétrica, hashing e assinatura

- **Base64:** codificação reversível, sem chave; exemplo: transportar bytes em texto.
- **Criptografia simétrica:** mesma chave secreta cifra e decifra; exemplo: AES-GCM para dados de aplicação.
- **Criptografia assimétrica:** par público/privado; exemplo: troca/autenticação de chave ou cifra de dados pequenos conforme protocolo.
- **Hashing:** transformação unidirecional; exemplo: Argon2id para verificação de senha.
- **Assinatura digital:** chave privada assina e chave pública verifica; exemplo: validar autoria/integridade de release.

### Três decisões arquiteturais que reduzem A02

1. Classificar dados e minimizar coleta/retention, usando tokenização quando possível.
2. Centralizar chaves em KMS com menor privilégio, rotação e trilha de auditoria.
3. Forçar TLS moderno e validação estrita; armazenar senhas com algoritmo adaptativo e cifra autenticada para dados reversíveis.

### Cinco perguntas objetivas com gabarito

1. **Base64 garante confidencialidade?**  
   **Resposta:** Não; é codificação reversível sem chave.

2. **Para que serve o salt no armazenamento de senha?**  
   **Resposta:** Tornar cada hash único, mesmo para senhas iguais, e dificultar pré-computação; o salt não precisa ser secreto.

3. **AES-GCM pode reutilizar o mesmo nonce com a mesma chave?**  
   **Resposta:** Não; nonce repetido compromete as garantias do modo.

4. **O que fazer com uma chave exposta em repositório?**  
   **Resposta:** Revogar/rotacionar, investigar uso, limitar impacto e substituir o segredo por gestão segura; apagar o commit não basta.

5. **Por que rejeitar certificado/hostname inválido?**  
   **Resposta:** Para impedir que o cliente confie em servidor falso e aceite interceptação da conexão.

### Três métricas de auditoria contínua e metas trimestrais

| Métrica | Meta proposta ao fim do trimestre |
|---|---|
| Percentual de endpoints elegíveis com TLS moderno e validação revisada | 100% dos endpoints públicos e serviços críticos |
| Dependências criptográficas com versão suportada e sem CVE crítica confirmada | 100% em serviços críticos; exceções documentadas com prazo |
| Tempo médio para revogar segredo confirmado como exposto | Até 4 horas após triagem/confirmacão, com simulado trimestral |

As metas são propostas para orientar uma equipe; devem ser ajustadas à capacidade, criticidade e requisitos reais. Medir baseline antes de avaliar cumprimento.

## 9. Conclusão

Falhas criptográficas decorrem tanto de ausência de proteção quanto de escolhas e operações incorretas: algoritmo, protocolo, chave, nonce, armazenamento, validação de certificado ou logging. A mitigação combina classificação de dados, bibliotecas confiáveis, parâmetros modernos, gestão de chaves, testes e resposta rápida a segredos expostos.

### Garantir proteção em produção

Minimizar dados, usar TLS validado, cifra autenticada para conteúdo reversível, hashing adaptativo para senhas, CSPRNG apropriado, nonce único conforme modo, KMS/Secret Manager, rotação, secret scanning, SCA e revisão de logs. Testes contínuos e critérios aprovados devem validar que as configurações continuam efetivas.

### Papel de assinaturas, versionamento e controles CI/CD

Assinaturas ajudam a confirmar origem e integridade de artefatos; versionamento permite revisar e auditar mudanças; CI/CD detecta segredos, bibliotecas vulneráveis e parâmetros fracos antes do deploy. Esses controles funcionam em conjunto com gestão de chaves, privilégios mínimos e resposta a incidente.

## Referências

- OWASP Top 10:2021 A02: https://top10.owasp.org/2021/A02_2021-Cryptographic_Failures/
- OWASP Password Storage Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
- OWASP Cryptographic Storage Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Cryptographic_Storage_Cheat_Sheet.html
- OWASP Transport Layer Security Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Transport_Layer_Security_Cheat_Sheet.html
- OWASP Key Management Cheat Sheet: https://cheatsheetseries.owasp.org/cheatsheets/Key_Management_Cheat_Sheet.html
- Mozilla Security Blog — Firesheep: https://blog.mozilla.org/security/2010/10/27/cooling-down-the-firesheep/
- LinkedIn — password security update: https://news.linkedin.com/2012/06/linkedin-summarizes-password-theft-and-member-security-efforts
- LinkedIn — notice of data breach: https://www.linkedin.com/help/linkedin/answer/a1338522/notice-of-data-breach-may-2016
- FTC — Uber breach: https://www.ftc.gov/business-guidance/blog/2018/04/ftc-addresses-ubers-undisclosed-data-breach-new-proposed-order
- U.S. DOJ — Uber agreement: https://www.justice.gov/usao-ndca/pr/uber-enters-non-prosecution-agreement
- Gitleaks: https://github.com/gitleaks/gitleaks
