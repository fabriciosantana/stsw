Feature: Classificação de Triângulos
  Para garantir que a classificação dos triângulos está correta,
  Como desenvolvedor,
  Eu quero que o programa classifique corretamente os triângulos com base nos lados fornecidos.

  Scenario: Triângulo Equilátero
    Given os lados do triângulo 5, 5, 5
    When eu classifico o triângulo
    Then o resultado deve ser Equilátero

  Scenario Outline: Classificar um triângulo válido
    Given os lados do triângulo <a>, <b>, <c>
    When eu classifico o triângulo
    Then o resultado deve ser <esperado>
    Examples:
        | a | b | c | esperado |
        | 1 | 2 | 200 | Não é um triângulo |
        | 1 | 50 | 50 | Isósceles |
        | 201 | 50 | 50 | Fora do intervalo |
        | 4 | 5 | 6 | Escaleno |
        | -1 | 5 | 5 | Fora do intervalo |
        | 205 | 205 | 205 | Fora do intervalo |
        | 10 | 11 | 12 | Escaleno |
        | 100 | 100 | 2 | Isósceles |
        | 1 | 1 | 199 | Não é um triângulo |