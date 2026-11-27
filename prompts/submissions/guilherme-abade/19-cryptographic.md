# Prompt 19: Cryptographic Failures (A02:2021)

**Aluno:** Guilherme Abade
**Referência:** [OWASP A02:2021](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)

Os exemplos em Java e os roteiros de ferramentas, como testssl.sh e gitleaks, destinam-se a laboratório próprio.

## 1. Introdução

A categoria *Cryptographic Failures* designa o uso ausente ou inadequado de criptografia que conduz à exposição de dados sensíveis, e ocupou o segundo lugar em 2021. A denominação substituiu a anterior, *Sensitive Data Exposure*, porque o nome antigo descrevia o sintoma, o dado exposto, enquanto o atual destaca a causa raiz, a falha criptográfica que leva à exposição, seja por não criptografar, por empregar algoritmo fraco ou por gerir mal as chaves.

A criptografia persegue objetivos que podem falhar de maneiras distintas. A confidencialidade, que restringe a leitura aos autorizados, falha com tráfego sem TLS, dado em claro no banco ou algoritmo fraco. A integridade, que assegura que o dado não foi alterado, falha na ausência de código de autenticação de mensagem ou de assinatura. A autenticidade, que comprova a origem, falha sem assinatura digital ou sem validação do certificado. O não repúdio, que impede a negação da autoria, falha sem a assinatura com a chave privada do autor.

Diversos incidentes reais ilustram a categoria. O tráfego sem TLS expôs aplicativos que enviaram credenciais por HTTP puro, interceptadas em redes públicas. O hashing fraco de senhas marcou vazamentos como o do LinkedIn, em 2012, que empregava SHA-1 sem sal, o que permitiu quebrar milhões de senhas com tabelas pré-computadas. As chaves e os segredos em repositórios figuram em inúmeros vazamentos de credenciais de nuvem publicadas por engano, com uso indevido e custos elevados. Convém distinguir codificação de criptografia: o Base64 é representação reversível sem segredo, que qualquer um decodifica, ao passo que a criptografia depende de uma chave; confundir os termos é perigoso, pois leva a supor protegido um dado apenas codificado.

## 2. Causas e sintomas

Vários sintomas indicam o uso inadequado de criptografia. O emprego de algoritmos e protocolos obsoletos, como MD5, SHA-1, RC4, DES, SSLv3 e TLS anterior à versão 1.2, é inseguro por fraquezas matemáticas conhecidas ou por chave curta. O hashing rápido de senha, com MD5 ou SHA-256 sem tratamento, permite testar bilhões de combinações por segundo; sem sal, entradas iguais produzem hashes iguais, o que viabiliza tabelas pré-computadas; sem custo ajustável, não há como frear a força bruta. Os segredos embutidos no código, em variáveis de ambiente sem controle ou expostos em logs e URLs ficam acessíveis a quem lê o repositório ou os registros. A reutilização de vetor de inicialização ou de *nonce*, bem como o uso de gerador não criptográfico, quebra a confidencialidade em modos como AES-GCM e AES-CTR e torna chaves e vetores previsíveis. O TLS mal configurado, com cifras sem sigilo futuro, certificados inválidos ou ausência de fixação onde cabível, completa o quadro. Cada um desses sintomas transmite falsa sensação de segurança, de forma que, descoberto o vazamento, o dado já se encontra recuperável, o que amplia o impacto e as obrigações legais.

## 3. Boas práticas

Em trânsito, impõe-se o uso obrigatório de HTTPS, com TLS 1.2 ou superior, de preferência 1.3, acompanhado de HSTS e de sigilo futuro, rejeitando-se clientes que confiam em qualquer certificado. Em repouso, recomenda-se a criptografia autenticada, por AES-GCM ou ChaCha20-Poly1305, com gestão de chaves em um serviço dedicado, rotação e menor privilégio. Para senhas, preferem-se o Argon2id ou, alternativamente, o bcrypt e o PBKDF2, com sal aleatório por senha e custo ajustado, evitando-se MD5, SHA-1 e SHA-256 sem tratamento. Para integridade e autenticidade, empregam-se a assinatura digital, por Ed25519 ou ECDSA, e o código de autenticação de mensagem quando apropriado. No plano da governança, define-se uma política de dados sensíveis, com classificação, retenção, mascaramento e proibição de registro. A validação dessas práticas apoia-se nos guias de referência da OWASP, em testes automatizados, em scanners no pipeline e em revisões de segurança de código.

## 4. Exemplos práticos em Java

O primeiro trecho armazena a senha de forma insegura.

```java
String hash = DigestUtils.md5Hex(password);   // ou SHA-256 sem tratamento
saveUser(user, hash);
```

O MD5 é rápido e apresenta colisões, e, sem sal, admite tabelas pré-computadas e revela senhas iguais entre usuários, o que torna trivial a força bruta por dicionário. A reescrita com bcrypt, cujo sal é gerado automaticamente e cujo custo é configurável, corrige o problema; o Argon2id é ainda preferível.

```java
// bcrypt: sal embutido e custo configurável
String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
saveUser(user, hash);
boolean ok = BCrypt.checkpw(tentativa, hash);
```

```java
// Argon2id, preferencial
Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
String hash = argon2.hash(3, 64 * 1024, 1, password);  // iterações, memória, paralelismo
boolean ok = argon2.verify(hash, tentativa);
```

O segundo trecho cifra em modo ECB.

```java
Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
c.init(Cipher.ENCRYPT_MODE, key);
byte[] out = c.doFinal(plaintext);
```

O modo ECB vaza padrão porque cifra cada bloco de forma independente, de maneira que blocos de texto iguais geram blocos cifrados iguais e a estrutura do dado permanece visível, como evidencia o exemplo clássico da imagem cifrada que continua reconhecível; o modo também não oferece integridade. A reescrita adota AES-GCM, com vetor de inicialização único por mensagem e verificação de etiqueta de autenticação.

```java
byte[] iv = new byte[12];
SecureRandom.getInstanceStrong().nextBytes(iv);          // vetor único por mensagem
Cipher c = Cipher.getInstance("AES/GCM/NoPadding");
c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
c.updateAAD(aad);                                         // dados associados, como id e versão
byte[] cifrado = c.doFinal(plaintext);                   // inclui a etiqueta de autenticação
```

A regra é jamais reutilizar o par formado pela chave e pelo vetor, obter o vetor de um gerador criptográfico e verificar a etiqueta na decifragem. O terceiro trecho desativa a validação do certificado.

```java
OkHttpClient client = new OkHttpClient.Builder()
   .hostnameVerifier((h, s) -> true)   // aceita qualquer certificado, anti-exemplo
   .build();
```

O erro está em desativar a verificação de nome e de certificado, o que faz um atacante intermediário, de posse de um certificado falso, ser aceito, anulando o TLS. A correção remove o verificador personalizado e o gestor de confiança permissivo, retornando aos padrões, que validam a cadeia e o nome, rejeita o TLS obsoleto e, quando pertinente, fixa o certificado.

```java
CertificatePinner pinner = new CertificatePinner.Builder()
    .add("api.exemplo.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build();
OkHttpClient client = new OkHttpClient.Builder().certificatePinner(pinner).build();
```

## 5. Prática com ferramentas reais

A auditoria de TLS executa um scanner contra um serviço de teste e relaciona as versões aceitas, as cifras fracas, o estado do HSTS e do sigilo futuro e a validade do certificado.

```bash
testssl.sh https://meu-servico.local
# para serviços públicos, o teste do SSL Labs em https://www.ssllabs.com/ssltest/
```

A varredura de segredos identifica credenciais no código e no histórico.

```bash
gitleaks detect --source . --report-path gitleaks.json
```

Diante de um segredo exposto, a resposta consiste em revogá-lo de imediato, migrar para um serviço de gestão de chaves e limpar o histórico quando necessário, lembrando que o segredo deve ser rotacionado de qualquer forma, pois o commit antigo persiste até ser removido. A análise de composição localiza bibliotecas criptográficas obsoletas e orienta a atualização. O *benchmark* de hashing de senhas compara o Argon2id, o bcrypt e o PBKDF2 em diferentes custos, medindo a latência em relação à segurança; como parâmetros de produção, sugerem-se Argon2id com memória de ao menos 64 MB e iterações ajustadas para cerca de meio segundo por hash no hardware real, ou bcrypt com custo igual ou superior a 12, revistos periodicamente conforme a evolução do hardware.

## 6. Aplicação em cenários reais

Para um banco digital, a política de proteção de dados sensíveis organiza-se por classe, como resume a tabela.

| Classe | Em trânsito | Em repouso |
|---|---|---|
| Pública | TLS | Não se aplica |
| Interna | TLS 1.2 ou superior | Criptografia de disco |
| Confidencial | TLS 1.3 e TLS mútuo interno | AES-GCM na aplicação, com serviço de chaves |
| Restrita, como dados de cartão | TLS 1.3 e TLS mútuo | AES-GCM por campo, chaves por inquilino e tokenização |

A criptografia autenticada, por AES-GCM, aplica-se à confidencialidade e à integridade de dados em repouso e de mensagens; a assinatura digital, à autenticidade e ao não repúdio de documentos, artefatos e tokens; o código de autenticação de mensagem, à integridade e à autenticidade entre partes que compartilham um segredo, como na validação de webhooks. O fluxo de gestão de chaves compreende a geração em serviço dedicado ou módulo de segurança, o armazenamento protegido, fora do código, o acesso por menor privilégio e auditado, a rotação periódica e sob suspeita de comprometimento, o registro de uso e a cópia de segurança das chaves de longa duração.

No cenário de injeção SQL combinada com criptografia transparente no banco, caso este decifre automaticamente e a aplicação decifre ao ler, uma injeção devolve os dados em claro, pois a camada que a explora já se encontra após a decifragem; as defesas em camadas compreendem a parametrização das consultas, a criptografia no nível da aplicação, com a chave sob controle desta, a segregação de funções e a minimização dos dados sensíveis armazenados. No cenário de segredos em URLs e logs, os tokens na *query string* vazam para o histórico do navegador, os logs de servidor e proxy e as ferramentas de observabilidade; a correção envia as credenciais em cabeçalho, nunca na URL, adota tempo de vida curto e armazenamento seguro no cliente e institui a proibição de registrar segredos.

## 7. Testes automatizados

Os cenários em Gherkin a seguir verificam a política de hash e o transporte seguro.

```gherkin
Cenário: Política de hash de senhas
  Dado que um usuário cadastra uma senha
  Quando a credencial é persistida
  Então o hash deve usar Argon2id ou bcrypt com sal aleatório e custo maior ou igual a 12
  E não deve existir uso de MD5, SHA-1 ou SHA-256 sem tratamento no caminho de persistência
```

```gherkin
Cenário: Transporte seguro
  Dado que o serviço expõe endpoints públicos
  Quando um cliente tenta conectar via HTTP
  Então deve redirecionar para HTTPS e o HSTS deve estar ativo
  E conexões com TLS anterior à versão 1.2 devem ser rejeitadas
```

As barreiras no pipeline falham a construção quando a varredura de segredos encontra credenciais, quando a análise de composição sinaliza biblioteca criptográfica obsoleta ou quando a verificação de TLS detecta protocolo ou cifra fracos.

## 8. Reflexão e avaliação

O Base64 não é criptografia, pois constitui apenas uma codificação reversível sem chave, que não protege nada. Convém distinguir quatro mecanismos. A criptografia simétrica usa uma chave para cifrar e decifrar, como no AES-GCM, e serve para dados em repouso. A assimétrica utiliza um par de chaves, pública e privada, como no RSA e na ECC, e serve à troca de chaves e à cifragem dirigida a um destinatário. O hashing é uma função de mão única, como o SHA-256 e o Argon2id, e serve para armazenar senhas e verificar integridade. A assinatura digital combina hash e chave privada, como no Ed25519, e serve à autenticidade e ao não repúdio. Três decisões arquiteturais reduzem o risco desta categoria: adotar TLS 1.3 com HSTS em todos os pontos, sem clientes que confiam em qualquer certificado; proteger senhas com Argon2id ou bcrypt e manter os segredos em um serviço de gestão de chaves, fora do código; e aplicar criptografia autenticada em repouso, com rotação de chaves e barreiras no pipeline.

As cinco questões objetivas a seguir, com gabarito, sintetizam os pontos centrais. A versão mínima de TLS a exigir atualmente é a 1.2, e não a 1.0, a SSLv3 ou a 1.1, de modo que a resposta é a 1.2. O algoritmo adequado para armazenar senhas, entre MD5, SHA-256 sem tratamento, Argon2id e Base64, é o Argon2id. A reutilização de vetor de inicialização em AES-GCM compromete a confidencialidade e a integridade. As chaves e os segredos devem permanecer em um serviço de gestão de chaves, e não no código nem na *query string*. O Base64 é apenas uma codificação reversível, e não criptografia nem hashing. Como métricas de auditoria contínua, propõem-se o percentual de endpoints com TLS 1.3, com meta de ao menos 95%, o percentual de dependências criptográficas atualizadas, com meta de 100% das críticas, e o tempo médio de rotação de um segredo após sua exposição, com meta inferior a 24 horas.

## 9. Conclusão

As *Cryptographic Failures* consistem em não usar criptografia ou em usá-la de forma inadequada, com tráfego sem TLS, hashing fraco de senha, segredos no código, modos e algoritmos obsoletos e gestão deficiente de chaves. A correção emprega criptografia forte e autenticada, TLS moderno, hashing de senha com custo, gestão de chaves em serviço dedicado e barreiras automatizadas no pipeline, com a varredura de segredos, a análise de composição e o teste de TLS, tratando os dados sensíveis conforme sua classificação, tanto em trânsito quanto em repouso.
