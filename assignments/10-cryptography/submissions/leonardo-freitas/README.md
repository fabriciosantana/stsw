# I2-10 — Cryptographic Failures

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Enunciado:** [assignments/10-cryptography/README.md](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/10-cryptography/README.md)

## Escopo

O enunciado da atividade indica um desafio do OWASP Juice Shop: **“Inform the shop about an algorithm or library it should definitely not use the way it does”**, também identificado como **Weird Crypto**.

**Estado nesta entrega:** o roteiro abaixo descreve como executar e validar o desafio, mas não comprova a execução. O aluno informou que as três imagens anexadas são desta atividade; elas foram incluídas em [evidencias/prints-recebidos.pdf](./evidencias/prints-recebidos.pdf), conforme solicitado. Nas telas visíveis, porém, aparecem a identificação **I2-09 — Security Misconfiguration / Vulnerable and Outdated Components** e desafios dessa categoria. Elas não mostram Weird Crypto resolvido e, portanto, não comprovam a conclusão da I2-10. Consulte [EVIDENCIAS.md](./EVIDENCIAS.md).

## Desafio — Weird Crypto

### Objetivo

Identificar e reportar, pelo formulário **Customer Feedback** do Juice Shop, um uso inadequado de algoritmo ou biblioteca criptográfica. O guia oficial informa que a aplicação possui exemplos didáticos, entre eles uso de MD5 sem salt para senhas, uso de Base64 em uma senha padrão, e bibliotecas/implementações inadequadas em outros fluxos. O guia lista como palavras reconhecidas pelo desafio: `z85`, `base85`, `base64`, `md5` ou `hashid`.

Base64 e Base85 são codificações reversíveis, não mecanismos de criptografia. MD5 é uma função de hash rápida e inadequada para armazenar senhas. A solução didática do desafio consiste em comunicar um desses problemas pelo formulário, não em explorar ou extrair dados de terceiros.

### Procedimento reproduzível

1. Iniciar uma instância própria/autorizada do OWASP Juice Shop e abrir `http://localhost:3000` (ou a URL local configurada).
2. Acessar **Customer Feedback** em `/#/contact`.
3. Preencher os campos obrigatórios apresentados pela versão em execução. No comentário, descrever brevemente um problema e incluir uma das palavras reconhecidas pelo desafio, por exemplo: `md5`.
4. Enviar o feedback e verificar se o Juice Shop exibe **“Challenge Solved!”** para **Weird Crypto**.
5. Abrir o **Score Board** e confirmar o estado concluído. Registrar uma captura legível contendo a confirmação do desafio e anexá-la como `evidencias/weird-crypto.png`.
6. Anotar aqui a versão do Juice Shop e o que foi observado. Não incluir senhas, cookies, tokens ou dados de outra pessoa.

### Análise e correção recomendada

- **MD5 para senhas:** por ser rápido e não ser um esquema moderno de armazenamento de senhas, facilita tentativas offline em caso de vazamento de hashes; sem salt exclusivo, senhas iguais também produzem hashes iguais.
- **Base64/Base85:** apenas representam bytes em outro formato; qualquer pessoa pode reverter a codificação. Não devem ser usadas como proteção de segredo.
- **Mitigação:** armazenar senhas usando uma função adaptativa específica, como Argon2id; utilizar salt único por senha e parâmetros de custo calibrados. Para sistemas legados, planejar migração gradual e redefinição de credenciais conforme o risco. Usar bibliotecas criptográficas reconhecidas e não criar algoritmos próprios.

## Resultado observado

**Ainda não registrado.** Esta entrega não afirma que o desafio foi executado ou concluído. O resultado só deve ser atualizado após a confirmação real no Score Board e inclusão da captura correspondente.

## Referências

- [Enunciado oficial da I2-10](https://github.com/fabriciosantana/stsw/blob/2026.2/assignments/10-cryptography/README.md)
- [Guia oficial do OWASP Juice Shop — Cryptographic Issues / Weird Crypto](https://pwning.owasp-juice.shop/companion-guide/latest/part2/cryptographic-issues.html)
- [Soluções oficiais do OWASP Juice Shop — Weird Crypto](https://pwning.owasp-juice.shop/companion-guide/latest/appendix/solutions.html)
- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
