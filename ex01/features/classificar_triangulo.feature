Feature: Classificação de Triângulos

  Scenario: Triângulo Equilátero
    Given um triângulo com lados 5, 5, 5
    When classifico o triângulo
    Then o tipo de triângulo deve ser "Equilátero"

  Scenario: Triângulo Isósceles
    Given um triângulo com lados 5, 5, 3
    When classifico o triângulo
    Then o tipo de triângulo deve ser "Isósceles"

  Scenario: Triângulo Escaleno
    Given um triângulo com lados 5, 4, 3
    When classifico o triângulo
    Then o tipo de triângulo deve ser "Escaleno"

  Scenario: Não é um triângulo
    Given um triângulo com lados 1, 2, 3
    When classifico o triângulo
    Then o tipo de triângulo deve ser "Não é um triângulo"

  Scenario: Lados Inválidos
    Given um triângulo com lados -5, 0, 5
    When classifico o triângulo
    Then o tipo de triângulo deve ser "Lados inválidos"
