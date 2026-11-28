# Atividade 07: Laboratórios de SSRF

**Aluno:** Guilherme Abade

Esta atividade resolve os laboratórios de Server-Side Request Forgery do PortSwigger Web Security Academy, ambientes deliberadamente vulneráveis destinados ao estudo, resolvidos na plataforma oficial. Em todos eles, a funcionalidade afetada é a verificação de estoque, que faz o servidor emitir uma requisição a uma URL influenciada pela entrada do usuário. Cada laboratório é descrito com a falha explorada, o procedimento de resolução e a correção correspondente.

## Basic SSRF against the local server

A funcionalidade de verificação de estoque envia uma requisição para uma URL informada em um parâmetro. A solução substitui essa URL pelo endereço de *loopback* seguido do caminho da interface administrativa, o que faz o servidor acessar a própria administração interna e permite, em seguida, excluir um usuário. A causa está em confiar na URL fornecida, e a correção aplica uma lista de permissão de destinos e bloqueia o acesso a endereços internos.

## Basic SSRF against another back-end system

Neste laboratório, o alvo não é o próprio servidor, mas outro sistema interno em uma faixa de endereços privada. A solução varia o último octeto do endereço interno no parâmetro vulnerável até localizar o sistema administrativo que responde, e então aciona a ação restrita. A correção segmenta a rede, de forma que a aplicação não tenha rota para sistemas internos sensíveis, e valida o endereço de destino antes de realizar a requisição.

## SSRF with blacklist-based input filter

A aplicação tenta impedir o ataque por meio de uma lista de bloqueio que veta termos como o endereço de *loopback*. A solução contorna o filtro com representações alternativas do endereço, como uma notação equivalente ou a variação de caixa, e, quando necessário, com dupla codificação dos caracteres. O laboratório evidencia a fragilidade da lista de bloqueio, e a correção substitui essa abordagem por uma lista de permissão e pela validação do endereço resolvido.

## SSRF with filter bypass via open redirection vulnerability

O filtro restringe os destinos permitidos, mas a aplicação possui um redirecionamento aberto em outra funcionalidade. A solução aponta o parâmetro vulnerável para um destino autorizado que, por meio do redirecionamento aberto, encaminha a requisição ao endereço interno pretendido. A correção elimina o redirecionamento aberto, ao validar o destino do redirecionamento, e valida o endereço final da requisição, e não apenas o inicial.

## Síntese

Os laboratórios demonstram que a SSRF decorre de confiar na URL fornecida pelo usuário e que as defesas baseadas em lista de bloqueio são facilmente contornadas, seja por representações alternativas de endereço, seja por redirecionamentos abertos. A defesa adequada combina uma lista de permissão de destinos, a resolução e a validação do endereço no instante da conexão e a segmentação de rede, de modo que a aplicação não alcance serviços internos sensíveis.
