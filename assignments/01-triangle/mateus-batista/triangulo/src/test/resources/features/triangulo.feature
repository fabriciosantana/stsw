Feature: Classificação de Triângulos
  Scenario: Equilátero
    Given os lados do triângulo são 5, 5, 5
    When verifico o tipo do triângulo
    Then o resultado deve ser "Equilátero"
