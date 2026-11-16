# Evidências — I2-08

## Imagens fornecidas

Os três arquivos abaixo foram anexados pelo aluno e preservados como recebidos. A análise visual **não permite autenticar a origem nem confirmar execução de laboratório**. As imagens não contêm URL de instância PortSwigger nem identificador verificável de sessão do Juice Shop.

| Arquivo | O que aparece | Valor probatório |
|---|---|---|
| [`1.jpg`](./evidencias/1.jpg) | Burp e duas janelas com `localhost:3000`; avisos “Challenge Solved” para Login Admin, Bender e Jim | Ilustração/alegação visual de conclusão. “Login Admin” se relaciona a JS-01, mas Bender e Jim não são itens do enunciado. A composição não identifica a instância nem comprova a sequência de requisições. |
| [`2.jpg`](./evidencias/2.jpg) | Página “Security Audit Results: I2-08 Identification Failures”, quatro linhas “Fixed” e código resumido | Tela de relatório/demonstração. Não é um resultado verificável dos 16 desafios. As quatro linhas não correspondem à lista integral do enunciado. |
| [`3.jpg`](./evidencias/3.jpg) | Diagrama de hashing bcrypt e exemplo de código Node.js | Material explicativo sobre armazenamento de senhas. Não demonstra execução nem correção dos desafios. |

Mesmo que um aviso diga *Solved* ou *Audit Passed*, a captura isolada registra somente o que estava visível, não valida a proveniência, a sessão ou a execução real. **Não houve teste local do Juice Shop, Burp ou PortSwigger nesta entrega.** Nenhum resultado é apresentado como resolução comprovada.

## Checklist das atividades exigidas

| ID | Desafio | Evidência real de resolução | Estado |
|---|---|---|---|
| JS-01 | Login administrador sem SQLi/troca de senha | Score Board + versão + registro do método | Pendente |
| JS-02 | Acesso à área administrativa | Score Board + rota e resposta observadas | Pendente |
| JS-03 | Remover feedbacks 5 estrelas | Score Board + operação observada | Pendente |
| JS-04 | Ver cesta de outro usuário | Score Board + objeto acessado, sem dados pessoais | Pendente |
| PS-A01 | Username enumeration via different responses | URL de instância + banner Solved + diferença de respostas | Pendente |
| PS-A02 | 2FA simple by-pass | URL de instância + banner Solved + fluxo observado | Pendente |
| PS-A03 | Password reset broken logic | URL de instância + banner Solved + fluxo observado | Pendente |
| PS-C01 | Unprotected admin functionality | URL de instância + banner Solved | Pendente |
| PS-C02 | Unprotected admin functionality with unpredictable URL | URL de instância + banner Solved | Pendente |
| PS-C03 | User role controlled by request parameter | URL de instância + banner Solved | Pendente |
| PS-C04 | User role can be modified in user profile | URL de instância + banner Solved | Pendente |
| PS-C05 | User ID controlled by request parameter | URL de instância + banner Solved | Pendente |
| PS-C06 | User ID controlled by request parameter, with unpredictable user IDs | URL de instância + banner Solved | Pendente |
| PS-C07 | User ID controlled by request parameter with data leakage in redirect | URL de instância + banner Solved | Pendente |
| PS-C08 | User ID controlled by request parameter with password disclosure | URL de instância + banner Solved | Pendente |
| PS-C09 | Insecure direct object references | URL de instância + banner Solved | Pendente |

## Registro futuro

Para cada item, acrescentar a data, versão, URL/identificador da instância, passos **realmente executados**, resultado observado e captura original com o banner de conclusão. Ocultar tokens, senhas e dados pessoais. Atualizar o estado somente após essa verificação.

