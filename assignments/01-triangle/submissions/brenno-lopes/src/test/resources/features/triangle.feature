# language: pt

Funcionalidade: Classificação de Triângulos
  Como um usuário do sistema
  Quero informar três lados de um triângulo
  Para que o sistema me diga o tipo do triângulo ou se os dados são inválidos

  Cenário: Triângulo Equilátero
    Dado que os lados são 5, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Equilátero"

  Cenário: Triângulo Isósceles com a igual a b
    Dado que os lados são 5, 5 e 3
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Isósceles com a igual a c
    Dado que os lados são 5, 3 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Isósceles com b igual a c
    Dado que os lados são 3, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Escaleno
    Dado que os lados são 3, 4 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Escaleno"

  Cenário: Lados que não formam triângulo
    Dado que os lados são 1, 2 e 3
    Quando eu classifico o triângulo
    Então o resultado deve ser "Não é um triângulo"

  Cenário: Lados com valor zero
    Dado que os lados são 0, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Cenário: Lados negativos
    Dado que os lados são -5, 0 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Cenário: Lado acima do máximo permitido
    Dado que os lados são 201, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Esquema do Cenário: Classificação de múltiplos tipos
    Dado que os lados são <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado           |
      | 10  | 10  | 10  | Equilátero          |
      | 7   | 7   | 5   | Isósceles           |
      | 5   | 7   | 9   | Escaleno            |
      | 1   | 10  | 2   | Não é um triângulo  |
      | 200 | 200 | 200 | Equilátero          |
      | 1   | 1   | 1   | Equilátero          |