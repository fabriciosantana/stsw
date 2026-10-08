Feature: Classificação de Triângulos com Análise de Valor Limite (BVA)
  Como um analista de qualidade de software
  Eu quero testar os valores limites e fronteiras das medidas dos lados de um triângulo
  Para garantir a robustez e exatidão da classificação de acordo com a técnica BVA (Boundary Value Analysis)

  Rule: O triângulo só é válido quando os lados estão no intervalo [1, 200] e satisfazem a desigualdade triangular

    @bva-normal
    Scenario Outline: Classificar triângulo avaliando valores válidos nos limites operacionais (BVA Normal)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Limites Inferiores e Superiores Válidos
        | a   | b   | c   | resultado  |
        | 1   | 1   | 1   | Equilátero |
        | 2   | 2   | 2   | Equilátero |
        | 100 | 100 | 100 | Equilátero |
        | 199 | 199 | 199 | Equilátero |
        | 200 | 200 | 200 | Equilátero |
        | 100 | 100 | 150 | Isósceles  |
        | 150 | 100 | 100 | Isósceles  |
        | 100 | 150 | 100 | Isósceles  |
        | 3   | 4   | 5   | Escaleno   |
        | 198 | 199 | 200 | Escaleno   |

    @bva-robusto
    Scenario Outline: Classificar triângulo avaliando condições fora dos limites operacionais (BVA Robusto)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Valores Abaixo e Acima das Fronteiras Válidas
        | a   | b   | c   | resultado          |
        | 0   | 100 | 100 | Lados inválidos    |
        | 100 | 0   | 100 | Lados inválidos    |
        | 100 | 100 | 0   | Lados inválidos    |
        | -1  | 100 | 100 | Lados inválidos    |
        | 201 | 200 | 200 | Lados inválidos    |
        | 200 | 201 | 200 | Lados inválidos    |
        | 200 | 200 | 201 | Lados inválidos    |

    @bva-desigualdade
    Scenario Outline: Validar descontinuidade da desigualdade triangular nas fronteiras exatas (a + b = c e a + b < c)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Limites de Desigualdade Triangular
        | a   | b   | c   | resultado          |
        | 1   | 1   | 2   | Não é um triângulo |
        | 1   | 2   | 3   | Não é um triângulo |
        | 100 | 100 | 200 | Não é um triângulo |
        | 50  | 50  | 101 | Não é um triângulo |
