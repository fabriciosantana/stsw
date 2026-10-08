# language: pt
Funcionalidade: Autorizar missões de drones usando Boundary Value Analysis

  Regra: A missão só é autorizada quando bateria, vento e peso de carga estão dentro dos limites.

    Esquema do Cenário: BVA Normal – suposição de falha única
      Quando eu avalio uma missão com bateria <bateria>, vento <vento> e peso da carga <peso_carga>
      Então a missão deve ser "<resultado>"

      Exemplos:
        | bateria | vento | peso_carga | resultado  |
        |      70 |    20 |          4 | AUTORIZADA |
        |      30 |    20 |          4 | AUTORIZADA |
        |      31 |    20 |          4 | AUTORIZADA |
        |      99 |    20 |          4 | AUTORIZADA |
        |     100 |    20 |          4 | AUTORIZADA |
        |      70 |     0 |          4 | AUTORIZADA |
        |      70 |     1 |          4 | AUTORIZADA |
        |      70 |    39 |          4 | AUTORIZADA |
        |      70 |    40 |          4 | AUTORIZADA |
        |      70 |    20 |          1 | AUTORIZADA |
        |      70 |    20 |          2 | AUTORIZADA |
        |      70 |    20 |          7 | AUTORIZADA |
        |      70 |    20 |          8 | AUTORIZADA |

    Esquema do Cenário: BVA Robusto – valores fora dos limites
      Quando eu avalio uma missão com bateria <bateria>, vento <vento> e peso da carga <peso_carga>
      Então a missão deve ser "<resultado>"

      Exemplos:
        | bateria | vento | peso_carga | resultado  |
        |      70 |    20 |          4 | AUTORIZADA |
        |      29 |    20 |          4 | NEGADA     |
        |      30 |    20 |          4 | AUTORIZADA |
        |      31 |    20 |          4 | AUTORIZADA |
        |      99 |    20 |          4 | AUTORIZADA |
        |     100 |    20 |          4 | AUTORIZADA |
        |     101 |    20 |          4 | NEGADA     |
        |      70 |    -1 |          4 | NEGADA     |
        |      70 |     0 |          4 | AUTORIZADA |
        |      70 |     1 |          4 | AUTORIZADA |
        |      70 |    39 |          4 | AUTORIZADA |
        |      70 |    40 |          4 | AUTORIZADA |
        |      70 |    41 |          4 | NEGADA     |
        |      70 |    20 |          0 | NEGADA     |
        |      70 |    20 |          1 | AUTORIZADA |
        |      70 |    20 |          2 | AUTORIZADA |
        |      70 |    20 |          7 | AUTORIZADA |
        |      70 |    20 |          8 | AUTORIZADA |
        |      70 |    20 |          9 | NEGADA     |
