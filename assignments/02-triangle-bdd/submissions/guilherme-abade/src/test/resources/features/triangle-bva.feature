Feature: Classificação de triângulos nos valores limite dos lados

  Scenario Outline: Classificar com BVA normal para lados de 1 a 200
    Given os lados <a>, <b> e <c>
    When o triângulo é classificado
    Then o resultado é "<resultado>"

    Examples:
      | a   | b   | c   | resultado          |
      | 100 | 100 | 100 | Equilátero         |
      | 1   | 100 | 100 | Isósceles          |
      | 2   | 100 | 100 | Isósceles          |
      | 199 | 100 | 100 | Isósceles          |
      | 200 | 100 | 100 | Não é um triângulo |
      | 100 | 1   | 100 | Isósceles          |
      | 100 | 2   | 100 | Isósceles          |
      | 100 | 199 | 100 | Isósceles          |
      | 100 | 200 | 100 | Não é um triângulo |
      | 100 | 100 | 1   | Isósceles          |
      | 100 | 100 | 2   | Isósceles          |
      | 100 | 100 | 199 | Isósceles          |
      | 100 | 100 | 200 | Não é um triângulo |
      | 99  | 100 | 101 | Escaleno           |
