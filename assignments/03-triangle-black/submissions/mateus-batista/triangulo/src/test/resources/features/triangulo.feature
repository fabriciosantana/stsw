# language: pt
Funcionalidade: Classificação de triângulos (BVA/caixa-preta)
  Como aluno
  Quero validar as fronteiras e classes de equivalência
  Para garantir o comportamento correto sem olhar a implementação interna

  # Domínio 1..200 (BVA): testar 0, 1, 200, 201 e negativos
  Esquema do Cenário: Faixa dos lados (Boundary Value Analysis)
    Quando eu classifico o triângulo com lados <a>, <b> e <c>
    Então o resultado deve ser "<esperado>"

    Exemplos:
      | a   | b   | c   | esperado         |
      | 0   | 1   | 1   | Lados inválidos  |
      | -1  | 2   | 3   | Lados inválidos  |
      | 201 | 10  | 10  | Lados inválidos  |
      | 200 | 200 | 200 | Equilátero       |

  # Desigualdade estrita: x + y > z (degenerado na fronteira “=”)
  Esquema do Cenário: Existência do triângulo (fronteira)
    Quando eu classifico o triângulo com lados <a>, <b> e <c>
    Então o resultado deve ser "<esperado>"

    Exemplos:
      | a | b | c | esperado            |
      | 1 | 1 | 2 | Não é um triângulo  |
      | 2 | 3 | 5 | Não é um triângulo  |
      | 2 | 3 | 4 | Escaleno            |

  # Classes válidas (equivalência): Equilátero, Isósceles, Escaleno
  Esquema do Cenário: Classes de equivalência válidas
    Quando eu classifico o triângulo com lados <a>, <b> e <c>
    Então o resultado deve ser "<esperado>"

    Exemplos:
      | a  | b  | c  | esperado            |
      | 1  | 1  | 1  | Equilátero          |
      | 3  | 3  | 4  | Isósceles           |
      | 3  | 4  | 5  | Escaleno            |
      | 10 | 10 | 20 | Não é um triângulo  |  # fronteira alta (10+10=20)
