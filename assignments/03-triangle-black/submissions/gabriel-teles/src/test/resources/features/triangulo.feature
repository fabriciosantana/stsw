Feature: Classificação de Triângulos - Testes BVA
  Verificar a classificação dos triângulos com base nos valores de limite dos lados

  Scenario Outline: Classificação dos Triângulos
    Given os lados do triângulo são <a>, <b>, <c>
    When eu classifico o triângulo
    Then a saída deve ser "<resultado>"

    Examples:
      | a   | b   | c   | resultado           |
      | 0   | 1   | 1   | Lados inválidos     |
      | 1   | 0   | 1   | Lados inválidos     |
      | 1   | 1   | 0   | Lados inválidos     |
      | 201 | 200 | 200 | Lados inválidos     |
      | 200 | 201 | 200 | Lados inválidos     |
      | 200 | 200 | 201 | Lados inválidos     |
      | 1   | 1   | 1   | EQUILÁTERO          |
      | 200 | 200 | 200 | EQUILÁTERO          |
      | 2   | 2   | 3   | ISÓSCELES           |
      | 199 | 200 | 200 | ISÓSCELES           |
      | 198 | 199 | 200 | ESCALENO            |
      | 1   | 2   | 3   | Não é um triângulo  |
      | 100 | 100 | 200 | Não é um triângulo  |
