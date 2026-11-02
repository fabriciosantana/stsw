# I2-07 — Registro de evidências de SSRF

**Aluno:** Leonardo Freitas  
**Escopo:** quatro labs oficiais da PortSwigger + um desafio SSRF do OWASP Juice Shop, citado como complemento da seção Juice Shop no enunciado.

> **Status em 02/11/2026:** nenhum laboratório foi executado ou comprovado nesta entrega. Não existem prints ou declarações de sucesso artificiais. Os **26 testes locais Java** comprovam apenas a validação do exemplo defensivo, não a solução dos laboratórios.

## Quadro de acompanhamento

| Código | Laboratório | Link oficial | Resultado | Print previsto |
|---|---|---|---|---|
| PS-01 | SSRF contra servidor local | [PortSwigger](https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-localhost) | PENDENTE | `evidencias/ps-01-solved.png` |
| PS-02 | SSRF contra outro backend | [PortSwigger](https://portswigger.net/web-security/ssrf/lab-basic-ssrf-against-backend-system) | PENDENTE | `evidencias/ps-02-solved.png` |
| PS-03 | SSRF com blacklist | [PortSwigger](https://portswigger.net/web-security/ssrf/lab-ssrf-with-blacklist-filter) | PENDENTE | `evidencias/ps-03-solved.png` |
| PS-04 | SSRF com open redirect | [PortSwigger](https://portswigger.net/web-security/ssrf/lab-ssrf-filter-bypass-via-open-redirection) | PENDENTE | `evidencias/ps-04-solved.png` |
| JS-01 | OWASP Juice Shop — SSRF | [OWASP](https://juice-shop.github.io/tab_challenges.html) | PENDENTE | `evidencias/js-01-solved.png` |

## Formulário curto para registrar cada resolução

Após executar **cada** lab, atualizar o registro abaixo com dados reais:

```text
Identificador: PS-01 / PS-02 / PS-03 / PS-04 / JS-01
Data da execução: AAAA-MM-DD
Ferramenta e versão: PortSwigger/Burp ou OWASP Juice Shop + versão
URL da instância: https://<seu-id>.web-security-academy.net/ ou localhost
Ação observada: (resumo dos passos efetivamente realizados)
Resposta relevante: (HTTP status/trecho não sensível)
Confirmação de sucesso: banner Solved / challenge solved
Print: evidencias/<identificador>-solved.png
Observações: ...
```

**Critério de evidência:** screenshot autêntico com banner de conclusão, nome do laboratório e, se possível, identificação da instância. Não incluir sessão, senha, cookies ou dados pessoais nas imagens. Para o Juice Shop, usar print do Score Board com o desafio concluído ou notificação de sucesso.

## Próximos passos

1. Abrir cada link oficial e iniciar o laboratório individual.
2. Seguir os procedimentos no [`README.md`](./README.md), apenas em ambientes de treino autorizados.
3. Registrar evidência autêntica, preencher o formulário de execução e atualizar `PENDENTE` para `RESOLVIDO` **somente após confirmação**.
4. Adicionar os arquivos `evidencias/*.png` ao mesmo branch/PR já criado.
5. Entregar no Ambiente Virtual o link do Pull Request quando a tarefa estiver efetivamente completa.