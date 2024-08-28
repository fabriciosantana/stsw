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