Feature: Classificação de triângulos

  Scenario Outline: Classificar lados válidos e inválidos
    Given os lados <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado é "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 1   | 1   | 1   | Equilátero         |
      | 200 | 200 | 200 | Equilátero         |
      | 5   | 5   | 3   | Isósceles          |
      | 5   | 3   | 5   | Isósceles          |
      | 3   | 5   | 5   | Isósceles          |
      | 3   | 4   | 5   | Escaleno           |
      | 1   | 2   | 3   | Não é um triângulo |
      | 0   | 5   | 5   | Lados inválidos    |
      | 201 | 5   | 5   | Lados inválidos    |
