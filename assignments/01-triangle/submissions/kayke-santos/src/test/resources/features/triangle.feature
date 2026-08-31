Feature: Classificação do Triângulo
  Como um usuário do sistema
  Eu quero fornecer as medidas de três lados de um triângulo
  Para obter a classificação correta ou a indicação de invalidade

  Scenario Outline: Classificar triângulo válido ou inválido
    Given que os lados do triângulo são <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado deve ser "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 5   | 5   | 5   | Equilátero         |
      | 1   | 1   | 1   | Equilátero         |
      | 200 | 200 | 200 | Equilátero         |
      | 5   | 5   | 3   | Isósceles          |
      | 5   | 3   | 5   | Isósceles          |
      | 3   | 5   | 5   | Isósceles          |
      | 5   | 4   | 3   | Escaleno           |
      | 3   | 4   | 5   | Escaleno           |
      | 1   | 2   | 3   | Não é um triângulo |
      | 1   | 2   | 4   | Não é um triângulo |
      | -5  | 0   | 5   | Lados inválidos    |
      | 201 | 100 | 100 | Lados inválidos    |
