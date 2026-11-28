# I2-10 — Resolver laboratórios de Cryptographic Failures

**Laboratório:** OWASP Juice Shop v20.4.0 (local, `http://localhost:3000`)

Evidência em [`evidencias/01-weird-crypto.png`](./evidencias/01-weird-crypto.png).

## Desafio: "Weird Crypto"

O código-fonte do Juice Shop (`lib/insecurity.ts`) define a função de hash de senhas assim:

```ts
export const hash = (data: string) => crypto.createHash('md5').update(data).digest('hex')
```

Isso usa **MD5 sem sal** para armazenar senhas de usuários. MD5 é um algoritmo de hash criptográfico quebrado (colisões práticas conhecidas desde 2004) e, além disso, é **rápido demais** para uso em senhas: sem sal, usuários com a mesma senha têm o mesmo hash (visível, por exemplo, nos hashes extraídos no desafio de SQL Injection da atividade 06), e o hash pode ser atacado por força bruta/rainbow tables em alta velocidade com GPU.

Reportei isso pelo formulário de feedback (`/#/contact`): *"The shop hashes user passwords with unsalted MD5 (crypto.createHash('md5')), a broken, fast algorithm unsuitable for passwords."*

**Correção esperada:** usar uma função de hash lenta e com sal própria para senhas, como Argon2id ou bcrypt.
