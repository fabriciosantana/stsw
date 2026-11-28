# Atividade 10: Laboratórios de Cryptographic Failures

**Aluno:** Guilherme Abade

Esta atividade resolve o desafio de problemas criptográficos do OWASP Juice Shop, ambiente deliberadamente vulnerável destinado ao estudo, executado localmente por `docker run --rm -p 3000:3000 bkimminich/juice-shop`. O desafio é apresentado com a falha explorada, o procedimento de resolução e a correção correspondente.

## OWASP Juice Shop: Cryptographic Issues

### Informar a loja sobre um algoritmo ou biblioteca que não deveria ser usado da forma como está

A aplicação emprega mecanismos criptográficos de maneira inadequada em diferentes pontos, entre eles o uso de codificação ou de funções de hash impróprias para proteger dados sensíveis. A solução investiga os tokens, os identificadores e os dados persistidos pela aplicação, identifica o emprego inadequado, por exemplo um valor apenas codificado em Base64 que se faz passar por proteção, ou uma função de hash rápida e sem sal usada para senhas, e reporta o problema pelo mecanismo de feedback da aplicação. A correção substitui a codificação pela criptografia autenticada quando o objetivo é confidencialidade, adota algoritmos de hash próprios para senha, como Argon2id ou bcrypt, com sal aleatório e custo ajustado, e mantém as chaves em um serviço dedicado de gestão de chaves.

## Síntese

O desafio evidencia que codificação e criptografia não se confundem, pois a primeira é reversível sem segredo, e que o uso de algoritmos de hash rápidos e sem sal para senhas permite a recuperação por dicionário e por tabelas pré-computadas. A correção de falhas criptográficas assenta-se em quatro pilares: criptografia autenticada para dados em repouso, TLS moderno para dados em trânsito, hashing de senha com custo ajustável e sal aleatório, e gestão adequada de chaves e de segredos, fora do código-fonte.
