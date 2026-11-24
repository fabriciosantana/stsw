# Evidências de execução — I2-09

**Aluno:** Leonardo Freitas  
**Data deste inventário:** 24/11/2026  
**Estado:** **não comprovado** — nenhum dos oito laboratórios foi executado ou validado nesta sessão de preparação do Pull Request.

Este documento distingue **roteiro de solução** de **evidência de execução**. Os caminhos abaixo são nomes sugeridos para capturas futuras; **arquivos de imagem ainda não foram anexados**. Não confundir imagens de I2-08 (identificação/autenticação) com as oito imagens exigidas para I2-09.

| ID | Plataforma | Desafio | Status verificável | Imagem esperada |
|---|---|---|---|---|
| JS-01 | Juice Shop | Error Handling | Pendente: sem acesso à instância | `evidencias/01-error-handling.png` |
| JS-02 | Juice Shop | Deprecated Interface | Pendente: sem acesso à instância | `evidencias/02-deprecated-interface.png` |
| JS-03 | Juice Shop | Frontend Typosquatting | Pendente: sem acesso à instância | `evidencias/03-frontend-typosquatting.png` |
| JS-04 | Juice Shop | Vulnerable Library | Pendente: sem acesso à instância | `evidencias/04-vulnerable-library.png` |
| PS-01 | PortSwigger | Information disclosure in error messages | Pendente: sem sessão do lab | `evidencias/05-infoleak-error.png` |
| PS-02 | PortSwigger | Information disclosure on debug page | Pendente: sem sessão do lab | `evidencias/06-infoleak-debug.png` |
| PS-03 | PortSwigger | Source code disclosure via backup files | Pendente: sem sessão do lab | `evidencias/07-infoleak-backup.png` |
| PS-04 | PortSwigger | Authentication bypass via information disclosure | Pendente: sem sessão do lab | `evidencias/08-auth-bypass-infoleak.png` |

## Orientações de captura (uma por desafio)

1. Ao concluir no Juice Shop, registrar **Score Board** com o **nome e status do desafio**. Uma tela apenas com erro sem *Challenge Solved* é evidência parcial.
2. Ao concluir na PortSwigger, mostrar a página do lab com **Solved** e o **nome/URL** legíveis.
3. Nomear os arquivos conforme a tabela e colocá-los neste repositório em `assignments/09-security/submissions/leonardo-freitas/evidencias/`.
4. Registrar aqui, para cada caso: data/hora, URL base (sem tokens), versão do Juice Shop quando aplicável, requisição/efeito observado, status de conclusão e nome do arquivo.
5. Ocultar senhas, tokens, cookies de sessão, variáveis sensíveis e identificadores privados nas capturas, mantendo a parte que prova a conclusão.

## Modelo para completar cada resultado

```md
### JS-01 — Error Handling
- Data da execução: AAAA-MM-DD
- Instância/versão:
- Procedimento aplicado:
- Resposta/efeito observado:
- Resultado no Score Board / Solved:
- Evidência: ![Descrição](evidencias/01-error-handling.png)
- Observações e mitigação:
```

**Não marcar um caso como concluído enquanto não houver prova de execução real.**
