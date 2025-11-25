## Cryptographic Failures (A02:2021)

Referências:

- [OWASP TOP Ten - Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)

## Cryptographic Failures (A02:2021)

Referências:

* [OWASP Top 10 – A02:2021 Cryptographic Failures]

### Introdução à Vulnerabilidade Cryptographic Failures

```
Explique o que caracteriza falhas criptográficas em aplicações modernas e aponte como elas levam à exposição de dados sensíveis.
- Por que “Criptographic Failures” substitui a antiga “Exposição de Dados Sensíveis” no Top 10?
- Quais objetivos da criptografia no software (confidencialidade, integridade, autenticidade e não repúdio) e como cada um pode falhar?
- Dê 3 exemplos reais de incidentes causados por: tráfego sem TLS, hashing fraco de senhas e chaves/segredos expostos em repositórios.
- Diferencie codificação (Base64) de criptografia. Por que confundir esses termos é perigoso?
```

### Principais Causas e Sintomas de Falhas Criptográficas

```
Descreva sintomas comuns que indicam uso inadequado de criptografia.
- Uso de algoritmos/protocolos obsoletos (MD5/SHA-1/RC4/SSLv3/TLS < 1.2) e por que são inseguros.
- Armazenamento de senhas com hash rápido (MD5/SHA-256 “puro”) sem sal/pepper ou com custo inadequado.
- Segredos hardcoded no código (Tokens, API Keys, credenciais de BD), em variáveis de ambiente sem controle, ou vazando em logs/URLs.
- Reutilização de IV/nonce ou RNG não criptográfico; por que isso quebra a segurança de AES-GCM/CTR.
- TLS mal configurado (cipher suites sem PFS, certificados inválidos, pinning ausente onde cabível).
- Como cada um desses sintomas afeta detecção, resposta e impacto do incidente.
```

### Boas Práticas de Criptografia

```
Liste princípios e decisões arquiteturais para prevenir `Cryptographic Failures`.
- Em trânsito: obrigar HTTPS (TLS 1.2+ preferindo 1.3), HSTS, PFS; rejeitar “TrustAll” em clientes.
- Em repouso: criptografia autenticada (AES-GCM/ChaCha20-Poly1305), gerenciamento de chaves via KMS/Secret Manager, rotação e princípio do menor privilégio.
- Senhas: Argon2id (preferencial) ou bcrypt/PBKDF2 com sal aleatório e custo ajustado; evitar SHA-1/MD5/SHA-256 “puro”.
- Integridade/autenticidade: assinaturas digitais (Ed25519/ECDSA) e MAC (HMAC) quando apropriado.
- Governança: política clara de handling de dados sensíveis (classificação, retenção, mascaramento e “no logs”).
Explique como validar essas práticas com “cheat sheets”, testes automatizados e revisões de segurança.
```

### Exemplos Práticos (código em Java)

```
Analise o trecho abaixo e identifique por que é inseguro para armazenar senhas:

String hash = DigestUtils.md5Hex(password); // ou SHA-256 “puro”
saveUser(user, hash);

- Quais ataques isso permite (e.g., dicionário/rainbow tables)?
- Reescreva usando Argon2id/bcrypt com sal aleatório e custo configurável.
```

```
Analise o uso de AES em ECB:

Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
c.init(Cipher.ENCRYPT_MODE, key);
byte[] out = c.doFinal(plaintext);

- Por que ECB vaza padrão? Dê um exemplo prático.
- Reescreva com AES-GCM, IV único por mensagem (SecureRandom), AAD e verificação de tag.
```

```
Cliente HTTP inseguro:

OkHttpClient client = new OkHttpClient.Builder()
   .hostnameVerifier((h, s) -> true)  // “TrustAll” (ANTI-exemplo)
   .build();

- O que está errado e como corrigir (verificação de hostname, pinning opcional, rejeitar TLS obsoleto)?
```

### Prática com Ferramentas Reais

```
Faça uma auditoria básica de criptografia no seu laboratório.
- Execute um scanner de TLS (ex.: testssl.sh ou SSL Labs) contra um serviço de teste e liste: versões, suites fracas, HSTS, PFS.
- Rode um secret scanner (ex.: gitleaks/truffleHog) no repositório de exemplo e identifique segredos expostos; proponha revogação e migração para Secret Manager/KMS.
- Utilize um SCA/Dependency Check para localizar bibliotecas criptográficas obsoletas e planeje atualização.
```

```
Benchmark de hashing de senhas.
- Compare Argon2id, bcrypt e PBKDF2 com diferentes custos (latência vs. segurança).
- Proponha parâmetros padrão para produção e um plano de revisão periódica.
```

### Aplicação em Cenários Reais

```
Você é o responsável de segurança de um banco digital.
- Defina a política de proteção de dados sensíveis por classe (pública/interna/confidencial/restrita), “em trânsito” e “em repouso”.
- Especifique quando usar criptografia autenticada, assinatura digital e HMAC.
- Desenhe o fluxo de gestão de chaves: geração, rotação, armazenamento, acesso, logging e backup.
```

```
Cenário: SQL Injection + “cripto transparente” no BD.
- Explique como uma SQLi pode retornar dados “em claro” se a aplicação decripta automaticamente.
- Defina defesas em camadas: prepared statements, criptografia no app, segregação de funções, e minimização de dados sensíveis.
```

```
Cenário: segredos em URLs e logs.
- Mostre como tokens na query string vazam para histórico, proxies e observabilidade.
- Corrija usando headers adequados (Authorization/Bearer), TTL curto e storage seguro no cliente.
```

### Testes Automatizados (Gherkin + pipeline)

```
Política de hash de senhas (Gherkin)
Dado que um usuário cadastra uma senha
Quando a credencial é persistida
Então o hash deve usar Argon2id ou bcrypt com sal aleatório e custo >= X
E não deve existir nenhum uso de MD5/SHA-1/SHA-256 “puro” no caminho de persistência
```

```
Transporte seguro (Gherkin)
Dado que o serviço expõe endpoints públicos
Quando um cliente tenta conectar via HTTP
Então deve redirecionar para HTTPS e HSTS deve estar ativo
E conexões com TLS < 1.2 devem ser rejeitadas
```

```
Guardrails no CI
- Falhar o build se: secret scanning encontrar segredos; SCA sinalizar lib cripto obsoleta; verificação de TLS detectar protocolo/suite fracos.
```

### Reflexão e Avaliação

```
Explique:
- Por que “Base64 não é criptografia”.
- Diferenças entre criptografia simétrica, assimétrica, hashing e assinatura digital (um exemplo de uso para cada).
- Três decisões arquiteturais que reduzem o risco A02 na sua aplicação.
```

```
- Elabore 5 perguntas objetivas (com gabarito) sobre: TLS forte, hashing de senhas, gestão de chaves, IV/nonce, e segredos em logs/URLs.
- Liste 3 métricas de auditoria contínua (ex.: % endpoints com TLS 1.3, % dependências cripto atualizadas, MTTR de rotação de segredos) e defina metas trimestrais.
```
