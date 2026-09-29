Feature: Classificação de triângulos por classes de equivalência e valores limite

  Scenario Outline: Validar as fronteiras dos lados de 1 a 200
    Given os lados <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado é "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 0   | 100 | 100 | Lados inválidos    |
      | 1   | 100 | 100 | Isósceles          |
      | 2   | 100 | 100 | Isósceles          |
      | 199 | 100 | 100 | Isósceles          |
      | 200 | 100 | 100 | Não é um triângulo |
      | 201 | 100 | 100 | Lados inválidos    |

  Scenario Outline: Classificar as demais classes de equivalência
    Given os lados <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado é "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 100 | 100 | 100 | Equilátero         |
      | 3   | 4   | 5   | Escaleno           |
      | 1   | 2   | 4   | Não é um triângulo |
