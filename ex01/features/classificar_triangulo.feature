Feature: Classificar Triângulo
    O programa deve receber três inteiros representando lados de um triângulo
    e deve retornar seu tipo, qual seja: equilátero, escaleno, isósceles

    Scenario: Classificar um Triânculo Equilátero Válido
        Given os lados do triângulo 1, 1, 1
        When eu classifico o triângulo
        Then o resultado deve ser Equilátero

    Scenario Outline: Classificar um Triânculo Válido
        Given os lados do triângulo <a>, <b>, <c>
        When eu classifico o triângulo
        Then o resultado deve ser <esperado>
        Examples: triângulos
            | a | b | c | esperado |
            | 3 | 3 | 3 | Equilátero |
            | 2 | 2 | 2 | Equilátero |
            | 1	| 1	| 1	|Equilátero |
            | 1	| 2	| 200| Não é um triângulo |
            | 1 | 50 | 50 | Isósceles |
            | 2 | 50 | 50 | Isósceles |
            | 50 | 50 | 50 | Equilátero |
            | 199 | 50 | 50 | Não é um triângulo |
            | 200 | 50 | 50 | Não é um triângulo |
            | 201 | 50 | 50 | Fora do intervalo |
                
    Scenario: Classificar triângulo com lado igual a uma letra
        Given os lados do triângulo x, y, z
        Then o sistema deve disparar exception