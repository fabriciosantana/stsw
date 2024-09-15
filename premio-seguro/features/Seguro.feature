Feature: Calcula prêmio seguro

    Scenario Outline: O motorista não pode contratar seguro por ter menos de 16 anos
        Given que o motorista tem <idade> anos
        And o motorista possui <pontos> pontos na carteira
        When eu calcular o prêmio do seguro
        Then o resultado deve ser <esperado>
        Examples:
        | idade | pontos |  esperado |
        | 15    | 0      |  False     |
        | 14    | 1      |  False     |
        | 13    | 0      |  False     |
        | 10    | 2      |  False     |