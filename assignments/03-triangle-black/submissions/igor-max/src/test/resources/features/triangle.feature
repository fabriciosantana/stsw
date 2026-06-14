Feature: Classificação de triângulos usando BVA

  Como testador
  Quero validar a classificação de triângulos
  Para garantir comportamento correto nos limites do domínio (1–200)

  Scenario Outline: Classificação de triângulos válidos
    When eu classifico um triangulo com lados <a>, <b>, <c>
    Then o resultado deve ser "<resultado>"

    Examples:
      | a | b | c | resultado   |
      | 5 | 5 | 5 | Equilátero  |
      | 5 | 5 | 3 | Isósceles   |
      | 4 | 5 | 6 | Escaleno    |

  Scenario Outline: Limite inferior dos lados
    When eu classifico um triangulo com lados <a>, 5, 5
    Then o resultado deve ser "<resultado>"

    Examples:
      | a | resultado           |
      | 0 | Lados inválidos     |
      | 1 | Isósceles           |
      | 2 | Isósceles           |

  Scenario Outline: Limite superior dos lados
    When eu classifico um triangulo com lados <a>, 5, 5
    Then o resultado deve ser "<resultado>"

    Examples:
      | a   | resultado           |
      | 199 | Não é um triângulo  |
      | 200 | Não é um triângulo  |
      | 201 | Lados inválidos     |

  Scenario Outline: Valores fora do domínio permitido
    When eu classifico um triangulo com lados <a>, <b>, <c>
    Then o resultado deve ser "<resultado>"

    Examples:
      | a | b | c | resultado        |
      | 0 | 5 | 5 | Lados inválidos  |
      | 5 | 0 | 5 | Lados inválidos  |
      | 5 | 5 | 0 | Lados inválidos  |
      | 201 | 5 | 5 | Lados inválidos |
      | 5 | 201 | 5 | Lados inválidos |
      | 5 | 5 | 201 | Lados inválidos |

  Scenario Outline: Validação da desigualdade triangular
    When eu classifico um triangulo com lados <a>, <b>, <c>
    Then o resultado deve ser "<resultado>"

    Examples:
      | a | b | c | resultado           |
      | 1 | 2 | 3 | Não é um triângulo  |
      | 3 | 4 | 7 | Não é um triângulo  |
      | 2 | 2 | 3 | Isósceles           |
      | 3 | 4 | 6 | Escaleno            |