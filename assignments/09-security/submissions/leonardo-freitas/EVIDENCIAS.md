# Evidências de execução — I2-09

**Aluno:** Leonardo Freitas  
**Atualização do inventário:** 26/11/2026  
**Estado documentado:** **2 de 8 desafios com evidência direta**. As capturas recebidas mostram dois desafios de Security Misconfiguration do Juice Shop concluídos. Não comprovam a execução dos outros dois desafios Juice Shop nem dos quatro laboratórios PortSwigger.

## Inventário

| ID | Plataforma | Desafio | Status baseado nas capturas | Evidência |
|---|---|---|---|---|
| JS-01 | OWASP Juice Shop | Error Handling | Concluído: aparece na lista “Challenge Solved!” | [PDF, página 1](./evidencias/juice-shop-capturas.pdf) |
| JS-02 | OWASP Juice Shop | Deprecated Interface | Concluído: aparece na lista “Challenge Solved!” | [PDF, página 1](./evidencias/juice-shop-capturas.pdf) |
| JS-03 | OWASP Juice Shop | Frontend Typosquatting | Pendente: nenhuma captura do desafio no Score Board | — |
| JS-04 | OWASP Juice Shop | Vulnerable Library | Pendente: auditoria resumida não mostra a conclusão do desafio no Score Board | [Auditoria suplementar, PDF página 2](./evidencias/juice-shop-capturas.pdf) |
| PS-01 | PortSwigger | Information disclosure in error messages | Pendente: sem captura de conclusão da instância | — |
| PS-02 | PortSwigger | Information disclosure on debug page | Pendente: sem captura de conclusão da instância | — |
| PS-03 | PortSwigger | Source code disclosure via backup files | Pendente: sem captura de conclusão da instância | — |
| PS-04 | PortSwigger | Authentication bypass via information disclosure | Pendente: sem captura de conclusão da instância | — |

## O que as imagens mostram

- **Página 1:** instância local em `localhost:3000`; aviso “Challenge Solved!” e lista com “Exposed Metrics”, “Deprecated Interface” e “Error Handling”. Isso evidencia diretamente os dois desafios do Juice Shop exigidos na parte de Security Misconfiguration. “Exposed Metrics” aparece como desafio adicional, não substitui outro item do enunciado.
- A mesma imagem mostra solicitações observadas no navegador para `/metrics`, `/ftp/package.json.bak` e `/rest/admin/application-configuration`. A imagem, sozinha, não registra respostas completas nem comprova os quatro labs da PortSwigger.
- **Página 2:** painel “Security Audit Results” em `localhost:3000), com status “Audit Passed” e quatro itens listados como “Fixed”, incluindo dependências JWT/Sanitize-HTML. É uma tela de auditoria, mas não mostra o Score Board com os desafios oficiais “Frontend Typosquatting” e “Vulnerable Library”; portanto, não foi contabilizada como conclusão desses desafios.
- Os arquivos `print_2.jpg` e `print_3.jpg` eram duplicados idênticos; a captura foi incluída uma vez no PDF para evitar repetição.

O PDF preserva as capturas fornecidas, reunidas em duas páginas. A data acima registra a atualização deste inventário, não uma data de execução inferida das imagens. O conteúdo comprova apenas o que está visível nelas; os demais resultados continuam pendentes.

## Orientações para completar os resultados

1. Para JS-03 e JS-04, anexar captura do Score Board indicando o nome do desafio concluído.
2. Para PS-01 a PS-04, anexar uma captura individual por laboratório mostrando “Solved” e o nome ou a URL da instância.
3. Ocultar senhas, tokens, cookies, chaves e outros dados sensíveis, sem remover a parte necessária para demonstrar a conclusão.

**Não considerar a I2-09 integralmente concluída até haver evidência para os oito desafios.**
