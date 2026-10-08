Feature: Categorizacao de Triangulos (Boundary Value Analysis)

  Dado um triangulo com tres lados a, b e c
  Eu quero categoriza-lo ou informar erros
  Para validar os limites (1-200) mapeados no exercicio

  Scenario Outline: Validando limites extremos e externos
    Given os lados do triangulo sao <a>, <b> e <c>
    When eu classifico o triangulo
    Then o resultado deve ser "<esperado>"
    
    Examples:
      | a   | b   | c   | esperado          |
      | 0   | 10  | 10  | Lados inválidos   |
      | 10  | 0   | 10  | Lados inválidos   |
      | 10  | 10  | 0   | Lados inválidos   |
      | 201 | 10  | 10  | Lados inválidos   |
      | 10  | 201 | 10  | Lados inválidos   |
      | 10  | 10  | 201 | Lados inválidos   |
      | 201 | 201 | 201 | Lados inválidos   |
      | 0   | -1  | 200 | Lados inválidos   |


  Scenario Outline: Validando tipos validos de triangulo nos limites e nominais
    Given os lados do triangulo sao <a>, <b> e <c>
    When eu classifico o triangulo
    Then o resultado deve ser "<esperado>"

    Examples:
      | a   | b   | c   | esperado          |
      | 1   | 1   | 1   | Equilátero        |
      | 10  | 10  | 10  | Equilátero        |
      | 100 | 100 | 100 | Equilátero        |
      | 199 | 199 | 199 | Equilátero        |
      | 200 | 200 | 200 | Equilátero        |
      
      | 10  | 10  | 15  | Isósceles         |
      | 10  | 15  | 10  | Isósceles         |
      | 15  | 10  | 10  | Isósceles         |
      | 200 | 200 | 199 | Isósceles         |
      | 1   | 200 | 200 | Isósceles         |
      
      | 10  | 12  | 14  | Escaleno          |
      | 3   | 4   | 5   | Escaleno          |
      | 198 | 199 | 200 | Escaleno          |


  Scenario Outline: Validando condicao para nao ser triangulo nas particoes validas
    Given os lados do triangulo sao <a>, <b> e <c>
    When eu classifico o triangulo
    Then o resultado deve ser "<esperado>"

    Examples:
      | a   | b   | c   | esperado           |
      | 1   | 1   | 200 | Não é um triângulo |
      | 1   | 2   | 3   | Não é um triângulo |
      | 10  | 20  | 10  | Não é um triângulo |
      | 10  | 10  | 20  | Não é um triângulo |
      | 20  | 10  | 10  | Não é um triângulo |
      | 100 | 100 | 200 | Não é um triângulo |
