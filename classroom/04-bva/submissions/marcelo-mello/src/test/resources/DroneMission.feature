Feature: Autorizar missões de drones usando Boundary Value Analysis

  Rule: A missão só é autorizada quando bateria, vento e peso de carga estão dentro dos limites. Bateria mínima de 30%, vento máximo de 20km/h e peso máximo de 4kg.

    Scenario Outline: Bva normal com suposição de falha única
      When eu avalio uma missão com bateria <bateria>, vento <vento>, e peso de carga <peso_carga>
      Then a missão deve ser <resultado>

      Examples:
        | bateria | vento | pesoCarga | resultado  |
        |      70 |    20 |         4 | AUTORIZADA |
        |      30 |    20 |         4 | AUTORIZADA |
        |      31 |    20 |         4 | AUTORIZADA |
