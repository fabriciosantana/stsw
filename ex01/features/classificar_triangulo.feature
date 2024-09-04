Feature: Classificação de Triângulos
  O programa deve receber três inteiros representando os lados de um triângulo
  e deve retornar o tipo do triângulo: Equilátero, Isósceles, Escaleno ou "Não é um triângulo".

  Scenario: Classificar um Triângulo Equilátero Válido
    Given os lados do triângulo 3, 3, 3
    When eu classifico o triângulo
    Then o resultado deve ser "Equilátero"

  Scenario Outline: Classificar diferentes tipos de triângulos
    Given os lados do triângulo <a>, <b>, <c>
    When eu classifico o triângulo
    Then o resultado deve ser "<esperado>"

    Examples:
      | a   | b   | c   | esperado              |
      | 3   | 3   | 3   | Equilátero            |
      | 5   | 5   | 3   | Isósceles             |
      | 5   | 4   | 3   | Escaleno              |
      | 1   | 2   | 3   | Não é um triângulo    |
      | 199 | 50  | 50  | Não é um triângulo    |
      | 200 | 50  | 50  | Não é um triângulo    |
      | 1   | 1   | 201 | Fora do Intervalo       |

  Scenario: Classificar triângulo com valores inválidos
    Given os lados do triângulo -5, 0, 5
    Then o resultado deve ser "Lados inválidos"

  Scenario: Classificar triângulo com letras como lados
    Given os lados do triângulo x, y, z
    Then o sistema deve disparar uma exception