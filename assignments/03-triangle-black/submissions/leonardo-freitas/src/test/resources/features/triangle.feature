Feature: Classificação de triângulos usando técnicas de caixa-preta e BVA
  Como estudante de Segurança e Teste de Software
  Quero testar o classificador apenas por suas entradas e saídas públicas
  Para validar as classes de equivalência e os valores de fronteira do domínio 1 a 200

  Background:
    Given um classificador de triângulos disponível

  Scenario Outline: Classes de equivalência de triângulos válidos
    When eu classifico os lados <a>, <b> e <c>
    Then o resultado deve ser "<resultado>"

    Examples:
      | a   | b   | c   | resultado  |
      | 100 | 100 | 100 | Equilátero |
      | 120 | 120 | 100 | Isósceles  |
      | 120 | 100 | 120 | Isósceles  |
      | 100 | 120 | 120 | Isósceles  |
      | 120 | 130 | 140 | Escaleno   |

  Scenario Outline: Lados dentro do domínio que não formam um triângulo
    When eu classifico os lados <a>, <b> e <c>
    Then o resultado deve ser "Não é um triângulo"

    Examples:
      | a   | b   | c   |
      | 1   | 1   | 2   |
      | 2   | 3   | 5   |
      | 50  | 50  | 100 |
      | 200 | 100 | 100 |

  Scenario Outline: BVA robusto aplicado individualmente aos três lados
    When eu classifico os lados <a>, <b> e <c>
    Then o resultado deve ser "<resultado>"

    Examples:
      | a   | b   | c   | resultado       |
      | 0   | 150 | 150 | Lados inválidos |
      | 1   | 150 | 150 | Isósceles       |
      | 2   | 150 | 150 | Isósceles       |
      | 199 | 150 | 150 | Isósceles       |
      | 200 | 150 | 150 | Isósceles       |
      | 201 | 150 | 150 | Lados inválidos |
      | 150 | 0   | 150 | Lados inválidos |
      | 150 | 1   | 150 | Isósceles       |
      | 150 | 2   | 150 | Isósceles       |
      | 150 | 199 | 150 | Isósceles       |
      | 150 | 200 | 150 | Isósceles       |
      | 150 | 201 | 150 | Lados inválidos |
      | 150 | 150 | 0   | Lados inválidos |
      | 150 | 150 | 1   | Isósceles       |
      | 150 | 150 | 2   | Isósceles       |
      | 150 | 150 | 199 | Isósceles       |
      | 150 | 150 | 200 | Isósceles       |
      | 150 | 150 | 201 | Lados inválidos |
