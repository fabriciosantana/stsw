Feature: Classificação do Triângulo com BVA
  Como um usuário do sistema
  Eu quero fornecer as medidas de três lados de um triângulo
  Para obter a classificação correta ou a indicação de invalidade

  Scenario Outline: Classificar triângulo por classes de equivalência e valores limite
    Given que os lados do triângulo são <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado deve ser "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 5   | 5   | 5   | Equilátero         |
      | 5   | 5   | 3   | Isósceles          |
      | 5   | 3   | 5   | Isósceles          |
      | 3   | 5   | 5   | Isósceles          |
      | 3   | 4   | 5   | Escaleno           |
      | 1   | 2   | 3   | Não é um triângulo |
      | 100 | 100 | 100 | Equilátero         |
      | 0   | 100 | 100 | Lados inválidos    |
      | 1   | 100 | 100 | Isósceles          |
      | 2   | 100 | 100 | Isósceles          |
      | 199 | 100 | 100 | Isósceles          |
      | 200 | 100 | 100 | Não é um triângulo |
      | 201 | 100 | 100 | Lados inválidos    |
      | 100 | 0   | 100 | Lados inválidos    |
      | 100 | 1   | 100 | Isósceles          |
      | 100 | 2   | 100 | Isósceles          |
      | 100 | 199 | 100 | Isósceles          |
      | 100 | 200 | 100 | Não é um triângulo |
      | 100 | 201 | 100 | Lados inválidos    |
      | 100 | 100 | 0   | Lados inválidos    |
      | 100 | 100 | 1   | Isósceles          |
      | 100 | 100 | 2   | Isósceles          |
      | 100 | 100 | 199 | Isósceles          |
      | 100 | 100 | 200 | Não é um triângulo |
      | 100 | 100 | 201 | Lados inválidos    |
