Feature: Classificação de Triângulos
  Para garantir que a classificação dos triângulos está correta,
  Como desenvolvedor,
  Eu quero que o programa classifique corretamente os triângulos com base nos lados fornecidos.

  Scenario: Triângulo Equilátero
    Given os lados do triângulo 5, 5, 5
    When eu classifico o triângulo
    Then o resultado deve ser "Equilátero"

  Scenario: Triângulo Isósceles
    Given os lados do triângulo 5, 5, 3
    When eu classifico o triângulo
    Then o resultado deve ser "Isósceles"

  Scenario: Triângulo Escaleno
    Given os lados do triângulo 5, 4, 3
    When eu classifico o triângulo
    Then o resultado deve ser "Escaleno"

  Scenario: Não é um Triângulo
    Given os lados do triângulo 1, 2, 3
    When eu classifico o triângulo
    Then o resultado deve ser "Não é um triângulo"

  Scenario: Lados Inválidos
    Given os lados do triângulo -5, 0, 5
    When eu classifico o triângulo
    Then o resultado deve ser "Lados inválidos"
