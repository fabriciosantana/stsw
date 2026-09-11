Feature: Testes de Caixa-Preta para Classificação de Triângulos (ECP e BVA)
  Como um analista de testes de software
  Eu quero aplicar técnicas de Caixa-Preta (Particionamento por Classes de Equivalência e Análise do Valor Limite)
  Para validar completamente o comportamento observável da aplicação de triângulos sem dependência de código interno

  Rule: Entradas válidas pertencem ao intervalo [1, 200] e cumprem a desigualdade triangular a + b > c

    @ecp-validas
    Scenario Outline: Validar classes de equivalência de triângulos válidos (ECP)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Classes Válidas (Equilátero, Isósceles, Escaleno)
        | a   | b   | c   | resultado  |
        | 100 | 100 | 100 | Equilátero |
        | 100 | 100 | 150 | Isósceles  |
        | 100 | 150 | 100 | Isósceles  |
        | 150 | 100 | 100 | Isósceles  |
        | 3   | 4   | 5   | Escaleno   |

    @ecp-invalidas
    Scenario Outline: Validar classes de equivalência inválidas por violação de desigualdade ou intervalo (ECP)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Classes Inválidas de Entrada
        | a   | b   | c   | resultado          |
        | 1   | 1   | 2   | Não é um triângulo |
        | 1   | 2   | 4   | Não é um triângulo |
        | 5   | 1   | 2   | Não é um triângulo |
        | 0   | 100 | 100 | Lados inválidos    |
        | -5  | 100 | 100 | Lados inválidos    |
        | 205 | 100 | 100 | Lados inválidos    |

    @bva-limites
    Scenario Outline: Validar fronteiras e limites operacionais da aplicação (BVA)
      Given que os lados do triângulo são <a>, <b> e <c>
      When o triângulo é classificado
      Then o resultado deve ser "<resultado>"

      Examples: Limites Extremos (Min-1, Min, Min+1, Max-1, Max, Max+1)
        | a   | b   | c   | resultado       |
        | 0   | 1   | 1   | Lados inválidos |
        | 1   | 1   | 1   | Equilátero      |
        | 2   | 2   | 2   | Equilátero      |
        | 199 | 199 | 199 | Equilátero      |
        | 200 | 200 | 200 | Equilátero      |
        | 201 | 200 | 200 | Lados inválidos |
        | 200 | 201 | 200 | Lados inválidos |
        | 200 | 200 | 201 | Lados inválidos |
