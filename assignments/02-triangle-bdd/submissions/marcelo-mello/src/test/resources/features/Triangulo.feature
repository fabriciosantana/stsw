# language: pt
Funcionalidade: Testar classificação de Triângulos com BVA (Boundary Value Analysis)

  Regra: Lados de um triângulo devem ser entre 1 e 200.

    Esquema do Cenário: BVA Normal para o lado "a" (Triângulos e não triângulos)
      Onde fixamos b = 100 e c = 100 (valores nominais).
      Quando eu classifico o triângulo com lados <a>, <b>, e <c>
      Então a classificação deve ser "<resultado>"

      Exemplos:
        | a   | b   | c   | resultado          |
        |   1 | 100 | 100 | Isósceles          |
        |   2 | 100 | 100 | Isósceles          |
        | 100 | 100 | 100 | Equilátero         |
        | 199 | 100 | 100 | Isósceles          |
        | 200 | 100 | 100 | Não é um triângulo |

    Esquema do Cenário: BVA Robusto - Lados inválidos
      Testamos valores imediatamente fora dos limites (0 e 201)
      Quando eu classifico o triângulo com lados <a>, <b>, e <c>
      Então a classificação deve ser "Lados inválidos"

      Exemplos:
        | a   | b   | c   | 
        |   0 | 100 | 100 |
        | 201 | 100 | 100 |
        | 100 |   0 | 100 |
        | 100 | 201 | 100 |
        | 100 | 100 |   0 |
        | 100 | 100 | 201 |
