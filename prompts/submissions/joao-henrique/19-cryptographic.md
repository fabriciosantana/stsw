# Prompt 19: OWASP Top Ten: Cryptographic Failures (A02:2021)

Referência: [OWASP A02:2021 – Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)

> Nota: os comandos de auditoria (testssl.sh, gitleaks, benchmark) são o procedimento a executar; os valores de benchmark citados são ordens de grandeza típicas, não medições feitas por mim neste documento.

## 1. Introdução

Falhas criptográficas são o uso ausente, fraco ou incorreto de criptografia para proteger dados em trânsito ou em repouso, o que leva à exposição de dados sensíveis (senhas, cartões, dados pessoais, saúde).

- **Por que substitui "Sensitive Data Exposure":** aquela categoria descrevia o *sintoma* (dados expostos); a nova descreve a *causa raiz* (falhas de criptografia: algoritmos fracos, chaves mal geridas, ausência de TLS etc.). Subiu para a 2ª posição.
- **Objetivos da criptografia e como falham:**
  - *Confidencialidade* (só quem deve lê): falha com dados sem criptografia, chave vazada, algoritmo fraco (DES, RC4) ou ECB.
  - *Integridade* (não foi alterado): falha com cifra sem autenticação (CBC sem MAC permite adulteração) ou com hash sem chave.
  - *Autenticidade* (quem enviou): falha quando não se valida o certificado/assinatura (TrustAll) ou com JWT `alg: none`.
  - *Não repúdio* (o autor não pode negar): falha sem assinatura digital, ou com chave compartilhada (HMAC) em que qualquer das partes poderia ter gerado a mensagem.
- **Três incidentes:**
  1. *Tráfego sem TLS:* a ferramenta Firesheep (2010) mostrou o sequestro de sessões de Facebook/Twitter em Wi-Fi aberto porque só o login usava HTTPS e os cookies trafegavam em claro.
  2. *Hashing fraco:* LinkedIn (2012) guardava senhas com SHA-1 sem sal e ~117 milhões de credenciais foram quebradas em massa depois do vazamento; Adobe (2013) usou 3DES em modo ECB e o padrão das senhas ficou visível.
  3. *Segredos em repositório:* Uber (2016) — chaves AWS em um repositório privado do GitHub deram acesso a dados de 57 milhões de usuários; muitos outros casos de chaves em repositórios públicos.
- **Codificação x criptografia:** Base64 só converte bytes em texto (reversível por qualquer pessoa, sem chave); criptografia exige chave para reverter. Confundir é perigoso porque dá falsa sensação de proteção (ex.: guardar senha ou token "em Base64" é guardá-lo em claro).

## 2. Causas e sintomas

- **Algoritmos/protocolos obsoletos (MD5, SHA-1, RC4, SSLv3, TLS < 1.2):** têm colisões práticas (MD5, SHA-1), vieses estatísticos (RC4) ou ataques conhecidos (POODLE no SSLv3, BEAST no TLS 1.0), então não garantem mais a propriedade que prometiam.
- **Senhas com hash rápido sem sal:** MD5/SHA-256 "puro" são feitos para velocidade; uma GPU testa bilhões por segundo, e sem sal, rainbow tables e senhas iguais com o mesmo hash revelam tudo. Correto: função lenta, com sal e custo ajustável.
- **Segredos no código/ambiente/logs/URLs:** quem acessa o repositório, o log ou o histórico do navegador obtém as credenciais; ficam no histórico do Git mesmo depois de apagados.
- **Reutilização de IV/nonce e RNG fraco:** em AES-GCM/CTR, reutilizar o nonce com a mesma chave permite recuperar o keystream (XOR de textos claros) e forjar tags; `java.util.Random` é previsível, então chaves/IVs/tokens gerados com ele podem ser reproduzidos. Deve-se usar `SecureRandom`.
- **TLS mal configurado:** suites sem PFS (RSA estático) permitem decifrar tráfego antigo se a chave vazar; certificados inválidos/expirados treinam o usuário a ignorar avisos; sem pinning onde cabível (apps móveis) é possível interceptação por CA comprometida.
- **Efeito em detecção, resposta e impacto:** esses problemas são silenciosos (a aplicação "funciona"), então não geram alerta; quando vazam, o dano é total e retroativo (dados antigos decifráveis), e a resposta exige rotacionar chaves, reemitir credenciais e recriptografar dados.

## 3. Boas práticas

- **Em trânsito:** HTTPS obrigatório (TLS 1.2+, de preferência 1.3), HSTS, suites com PFS (ECDHE), redirecionamento de HTTP, e nunca "TrustAll" nos clientes.
- **Em repouso:** criptografia autenticada (AES-GCM ou ChaCha20-Poly1305); chaves em KMS/Secret Manager/HSM, com rotação e menor privilégio; chave separada dos dados.
- **Senhas:** Argon2id (preferido) ou bcrypt/PBKDF2 com sal aleatório por usuário e custo ajustado; nunca MD5/SHA-1/SHA-256 puro.
- **Integridade/autenticidade:** assinaturas digitais (Ed25519/ECDSA) e HMAC quando as partes compartilham uma chave.
- **Governança:** classificação de dados, retenção mínima, mascaramento, proibição de dados sensíveis em logs, e não guardar o que não é necessário.
- **Como validar:** seguir os *cheat sheets* da OWASP (Password Storage, Cryptographic Storage, Transport Layer Security), testes automatizados (Gherkin e testes de integração que checam o algoritmo/headers), scanners (testssl, gitleaks, SCA) no CI e revisão de segurança/modelagem de ameaças.

## 4. Exemplos práticos em Java

**(a) Hash de senha inseguro**

```java
String hash = DigestUtils.md5Hex(password); // ou SHA-256 "puro"
saveUser(user, hash);
```

- **Problemas:** MD5/SHA-256 são rápidos e sem sal; permite ataque de dicionário/força bruta offline (bilhões de tentativas por segundo), rainbow tables, e dois usuários com a mesma senha têm o mesmo hash.
- **Correção (bcrypt via Spring Security; Argon2id também é opção):**

```java
// bcrypt com custo 12 (sal aleatório incluído no hash)
PasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hash = encoder.encode(password);
saveUser(user, hash);
boolean ok = encoder.matches(senhaDigitada, hashSalvo);

// Argon2id (Spring Security 5.8+/BouncyCastle): 
// PasswordEncoder argon = new Argon2PasswordEncoder(16, 32, 1, 1 << 16, 3); // sal 16B, hash 32B, 64MiB, 3 iterações
```

**(b) AES em modo ECB**

```java
Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
c.init(Cipher.ENCRYPT_MODE, key);
byte[] out = c.doFinal(plaintext);
```

- **Por que ECB vaza padrão:** cada bloco de 16 bytes é cifrado independentemente com a mesma chave; blocos iguais de texto claro geram blocos iguais de texto cifrado. O exemplo clássico é o "pinguim ECB": uma imagem cifrada em ECB continua mostrando o contorno do desenho. Em dados, campos repetidos (mesmo CPF/mesmo status) ficam visíveis e os blocos podem ser reordenados ou trocados sem detecção (não há integridade).
- **Correção com AES-GCM (IV único, AAD, tag verificada):**

```java
SecureRandom rng = new SecureRandom();
byte[] iv = new byte[12];
rng.nextBytes(iv);                               // novo IV a cada mensagem

Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
c.updateAAD(contexto);                           // dados autenticados mas não cifrados (ex.: id do usuário)
byte[] cifrado = c.doFinal(plaintext);           // já inclui a tag de 128 bits
// guardar iv + cifrado

// Decifrar: se o texto/AAD/tag foram alterados, doFinal lança AEADBadTagException
Cipher d = Cipher.getInstance("AES/GCM/NoPadding");
d.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, iv));
d.updateAAD(contexto);
byte[] claro = d.doFinal(cifrado);
```

**(c) Cliente HTTP inseguro**

```java
OkHttpClient client = new OkHttpClient.Builder()
   .hostnameVerifier((h, s) -> true)  // "TrustAll"
   .build();
```

- **O que está errado:** aceita qualquer certificado/host, então desabilita a autenticação do servidor e permite ataque man-in-the-middle: um atacante na rede apresenta um certificado qualquer e lê/altera todo o tráfego, apesar de usar "HTTPS".
- **Correção:** manter o `HostnameVerifier` padrão e a validação da cadeia de certificados do sistema; opcionalmente fixar a chave pública (pinning) e restringir a TLS 1.2/1.3:

```java
CertificatePinner pinner = new CertificatePinner.Builder()
    .add("api.exemplo.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")  // pin do certificado/chave
    .build();
ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.RESTRICTED_TLS).build();
OkHttpClient client = new OkHttpClient.Builder()
    .certificatePinner(pinner)
    .connectionSpecs(List.of(spec))               // TLS 1.2+ e suites fortes
    .build();
```

## 5. Prática com ferramentas

- **Scanner de TLS:** `testssl.sh https://meu-lab.local` (ou SSL Labs para serviços públicos). Listar: versões habilitadas (reprovar SSLv3, TLS 1.0/1.1), suites fracas (RC4, 3DES, CBC antigas, sem PFS), HSTS presente, PFS (ECDHE), validade/cadeia do certificado e vulnerabilidades conhecidas (Heartbleed, ROBOT).
- **Secret scanner:** `gitleaks detect --source . -v` (ou `trufflehog git file://.`). Para cada segredo encontrado: *revogar e rotacionar imediatamente* (remover do histórico é insuficiente), migrar para Secret Manager/KMS (ou variáveis injetadas em runtime), e adicionar pre-commit hook.
- **SCA/Dependency-Check:** `mvn org.owasp:dependency-check-maven:check` para achar bibliotecas criptográficas obsoletas (ex.: BouncyCastle antigo, OpenSSL/JCE sem suporte); planejar atualização com testes de regressão.
- **Benchmark de hashing de senhas:** medir o tempo de um hash em cada algoritmo/custo no hardware do servidor, buscando ~250–500 ms por hash em login (compromisso entre latência e segurança). Em ordem de grandeza: PBKDF2-HMAC-SHA256 com 600 mil iterações (recomendação OWASP); bcrypt custo 12–13; Argon2id com 19 MiB de memória, 2 iterações e 1 de paralelismo (mínimo OWASP) ou 64 MiB/3 iterações.
- **Parâmetros padrão para produção:** Argon2id (m=64 MiB, t=3, p=1), sal de 16 bytes aleatório, com *rehash* transparente no login quando os parâmetros mudarem; plano de revisão a cada 6–12 meses (ou quando o hardware mudar) e monitorar a latência do login.

## 6. Aplicação em cenários reais: banco digital

**Política por classe de dado:**

| Classe | Exemplos | Em trânsito | Em repouso |
|---|---|---|---|
| Pública | Material de marketing | HTTPS | Sem exigência |
| Interna | Documentos internos | TLS 1.2+ | Criptografia de disco |
| Confidencial | Dados cadastrais, CPF | TLS 1.3 + HSTS | Criptografia no app/coluna (AES-GCM), chaves no KMS |
| Restrita | Senhas, PAN de cartão, chaves, biometria | TLS 1.3, mTLS entre serviços | Criptografia no app + HSM; senhas só como hash Argon2id; mascaramento/tokenização; acesso com MFA e log |

**Quando usar cada mecanismo:** criptografia autenticada (AES-GCM) para proteger dados que precisam ser lidos de volta e não podem ser adulterados; **assinatura digital** (Ed25519/ECDSA) quando é preciso provar autoria/não repúdio (contratos, transações, artefatos); **HMAC** quando as duas pontas confiam entre si e só precisam de integridade/autenticidade (webhooks, cookies assinados).

**Gestão de chaves:** geração em KMS/HSM com RNG forte → armazenamento nunca junto dos dados (envelope encryption: chave de dados cifrada pela chave mestra) → acesso por papéis de menor privilégio e separação de funções → rotação periódica e imediata após suspeita (versionamento de chaves para decifrar dados antigos) → log/auditoria de todo uso → backup cifrado, com cópia em outra região e teste de recuperação → destruição com registro ao fim da vida.

**SQL Injection + "cripto transparente" no BD:** com TDE ou colunas decifradas automaticamente pelo banco/aplicação, a consulta injetada é executada com os privilégios da aplicação e o dado volta já decifrado, pois a decifragem é transparente para quem tem acesso. Defesas em camadas: prepared statements; criptografia no nível da aplicação com chaves fora do banco (o SQL injetado só vê o texto cifrado); contas de banco com privilégio mínimo e segregação de funções; minimização/mascaramento dos dados sensíveis; WAF e monitoramento de consultas anômalas.

**Segredos em URLs e logs:** tokens na query string (`/reset?token=abc`) vazam no histórico do navegador, no cabeçalho `Referer`, em logs de servidores/proxies/CDN e na observabilidade (APM). Correção: enviar o token no header `Authorization: Bearer ...` (ou no corpo/POST), com TTL curto e uso único, armazenar no cliente de forma segura (cookie `HttpOnly; Secure; SameSite` ou armazenamento seguro do SO) e mascarar parâmetros sensíveis nos logs.

## 7. Testes automatizados (Gherkin e pipeline)

```gherkin
# language: pt
Funcionalidade: Política de hash de senhas
  Cenário: Senha persistida com algoritmo adequado
    Dado que um usuário cadastra uma senha
    Quando a credencial é persistida
    Então o hash deve usar Argon2id ou bcrypt com sal aleatório e custo mínimo configurado
    E não deve existir uso de MD5, SHA-1 ou SHA-256 puro no caminho de persistência

Funcionalidade: Transporte seguro
  Cenário: Cliente tenta usar HTTP
    Dado que o serviço expõe endpoints públicos
    Quando um cliente tenta conectar via HTTP
    Então deve ser redirecionado para HTTPS
    E o cabeçalho Strict-Transport-Security deve estar presente
    E conexões com TLS inferior a 1.2 devem ser rejeitadas
```

**Guardrails no CI (o build falha se):** o *secret scanning* (gitleaks) encontra segredos; o SCA aponta biblioteca criptográfica obsoleta ou com CVE alta/crítica; a verificação de TLS (testssl) detecta protocolo/suite fraco ou ausência de HSTS; uma análise estática (Semgrep/SpotBugs) encontra `MessageDigest.getInstance("MD5")`, `AES/ECB` ou `hostnameVerifier` permissivo.

## 8. Reflexão e avaliação

- **Base64 não é criptografia:** é só uma codificação, pública e reversível por qualquer um sem chave; serve para transportar bytes como texto, não para proteger.
- **Diferenças e exemplos:**
  - *Simétrica:* mesma chave para cifrar e decifrar (rápida). Ex.: AES-GCM para cifrar campos no banco.
  - *Assimétrica:* par de chaves pública/privada. Ex.: troca de chaves no TLS (ECDHE) ou cifrar para um destinatário (RSA-OAEP).
  - *Hashing:* função de mão única, sem chave, tamanho fixo. Ex.: guardar senhas (com Argon2id/bcrypt) ou verificar integridade de arquivos (SHA-256).
  - *Assinatura digital:* hash assinado com a chave privada; valida com a pública. Ex.: assinar um release ou um JWT (Ed25519/RS256).
- **Três decisões arquiteturais para reduzir o risco A02:** (1) TLS 1.3 obrigatório com HSTS em todas as bordas e mTLS entre serviços; (2) segredos e chaves em um Secret Manager/KMS, com rotação, e nunca no código; (3) senhas com Argon2id, e dados sensíveis cifrados com AES-GCM no nível da aplicação, coletando e guardando só o necessário.

**Cinco perguntas objetivas (com gabarito):**

1. *Qual a versão mínima aceitável de TLS hoje?* (a) SSLv3 (b) TLS 1.0 (c) TLS 1.2 (d) TLS 1.1 — **c** (preferencialmente TLS 1.3).
2. *Qual a melhor opção para armazenar senhas?* (a) SHA-256 puro (b) MD5 com sal (c) Base64 (d) Argon2id com sal aleatório — **d**.
3. *Qual a prática correta de gestão de chaves?* (a) chave no código-fonte (b) chave no mesmo banco dos dados (c) chaves em KMS com rotação e acesso mínimo (d) a mesma chave por anos — **c**.
4. *Em AES-GCM, o que não pode acontecer?* (a) usar IV de 12 bytes (b) reutilizar o par chave+IV (c) usar `SecureRandom` (d) verificar a tag — **b**.
5. *Qual a forma correta de enviar um token de acesso?* (a) na query string da URL (b) em um log de depuração (c) no cabeçalho `Authorization` com TTL curto (d) em um comentário HTML — **c**.

**Três métricas de auditoria contínua (com meta trimestral):**

| Métrica | Meta trimestral |
|---|---|
| % de endpoints públicos com TLS 1.3 e HSTS | ≥ 95% (Q1), 100% (Q2) |
| % de dependências criptográficas sem CVE alta/crítica e dentro do suporte | ≥ 98% |
| MTTR de rotação de segredos expostos (do alerta à revogação) | ≤ 4 horas para críticos; 0 segredos ativos em repositórios |
