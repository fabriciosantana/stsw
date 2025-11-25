# language: pt
Funcionalidade: Classificação de triângulos
  Como estudante
  Quero classificar lados inteiros de 1..200
  Para identificar Equilátero, Isósceles, Escaleno, "Não é triângulo" e "Lados inválidos"

  Regra: Validação de faixa (BVA 1..200)

    Esquema do Cenário: Lado fora da faixa deve ser inválido (falha única)
      Dado que eu tenho os lados <a>, <b> e <c>
      Quando eu classifico o triângulo
      Então o resultado deve ser "Invalid sides"

      Exemplos:
        | a   | b   | c   |
        |   0 |   5 |   5 |
        | 201 |   5 |   5 |
        |   5 |   0 |   5 |
        |   5 | 201 |   5 |
        |   5 |   5 |   0 |
        |   5 |   5 | 201 |

    Esquema do Cenário: Limites válidos aceitos
      Dado que eu tenho os lados <a>, <b> e <c>
      Quando eu classifico o triângulo
      Então o resultado deve ser "<esperado>"

      Exemplos:
        | a   | b   | c   | esperado       |
        |   1 |   1 |   1 | Equilateral    |
        | 200 | 200 | 200 | Equilateral    |
        |   1 |   1 |   2 | Not a triangle |
        |   1 |   2 |   2 | Isosceles      |
        |   2 |   3 |   4 | Scalene        |

  Regra: Desigualdade do triângulo (BVA a+b>c, etc.)

    Esquema do Cenário: Fronteiras da forma (falha única)
      Dado que eu tenho os lados <a>, <b> e <c>
      Quando eu classifico o triângulo
      Então o resultado deve ser "<esperado>"

      Exemplos:
        | a   | b   | c   | esperado       |
        |   2 |   3 |   5 | Not a triangle |
        |   2 |   3 |   4 | Scalene        |
        |   3 |   3 |   6 | Not a triangle |
        |   3 |   4 |   7 | Not a triangle |
        | 100 | 100 | 199 | Isosceles      |
        | 100 | 100 | 200 | Not a triangle |

  Regra: Classificações corretas

    Esquema do Cenário: Equilátero, Isósceles e Escaleno
      Dado que eu tenho os lados <a>, <b> e <c>
      Quando eu classifico o triângulo
      Então o resultado deve ser "<esperado>"

      Exemplos:
        | a | b | c | esperado    |
        | 5 | 5 | 5 | Equilateral |
        | 5 | 5 | 3 | Isosceles   |
        | 5 | 3 | 5 | Isosceles   |
        | 3 | 5 | 5 | Isosceles   |
        | 4 | 5 | 6 | Scalene     |

  Regra: Falhas múltiplas (mais de um erro no mesmo caso)

    Esquema do Cenário: Prioridade da mensagem em falhas múltiplas
      Dado que eu tenho os lados <a>, <b> e <c>
      Quando eu classifico o triângulo
      Então o resultado deve ser "Invalid sides"

      Exemplos:
        | a   | b | c    |
        |   0 | 1 |    3 |
        | 201 | 2 |    2 |
        |   5 | 0 | 1000 |
