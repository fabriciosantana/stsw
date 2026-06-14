# language: pt

Funcionalidade: Classificação de Triângulos — Teste Caixa-Preta com BVA
  Como um usuário do sistema
  Quero informar três lados de um triângulo
  Para que o sistema classifique corretamente aplicando análise de valores limite

  # ===== CLASSES DE EQUIVALÊNCIA: Triângulos válidos =====

  Cenário: Triângulo Equilátero com lados iguais
    Dado que os lados são 10, 10 e 10
    Quando eu classifico o triângulo
    Então o resultado deve ser "Equilátero"

  Cenário: Triângulo Isósceles com a igual a b
    Dado que os lados são 10, 10 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Isósceles com a igual a c
    Dado que os lados são 10, 5 e 10
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Isósceles com b igual a c
    Dado que os lados são 5, 10 e 10
    Quando eu classifico o triângulo
    Então o resultado deve ser "Isósceles"

  Cenário: Triângulo Escaleno com lados diferentes
    Dado que os lados são 3, 4 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Escaleno"

  # ===== CLASSES DE EQUIVALÊNCIA: Entradas inválidas =====

  Cenário: Lado zero é inválido
    Dado que os lados são 0, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Cenário: Lado negativo é inválido
    Dado que os lados são -1, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Cenário: Lado acima do máximo é inválido
    Dado que os lados são 201, 5 e 5
    Quando eu classifico o triângulo
    Então o resultado deve ser "Lados inválidos"

  Cenário: Lados que não formam triângulo
    Dado que os lados são 1, 2 e 10
    Quando eu classifico o triângulo
    Então o resultado deve ser "Não é um triângulo"

  # ===== BVA: Limite inferior do domínio (MIN_SIDE = 1) =====

  Esquema do Cenário: BVA no limite inferior do domínio
    Dado que os lados são <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a  | b  | c  | resultado          |
      | 0  | 10 | 10 | Lados inválidos    |
      | 1  | 10 | 10 | Isósceles          |
      | 2  | 10 | 10 | Isósceles          |
      | 10 | 0  | 10 | Lados inválidos    |
      | 10 | 1  | 10 | Isósceles          |
      | 10 | 10 | 0  | Lados inválidos    |
      | 10 | 10 | 1  | Isósceles          |

  # ===== BVA: Limite superior do domínio (MAX_SIDE = 200) =====

  Esquema do Cenário: BVA no limite superior do domínio
    Dado que os lados são <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado       |
      | 199 | 199 | 199 | Equilátero      |
      | 200 | 200 | 200 | Equilátero      |
      | 201 | 200 | 200 | Lados inválidos |
      | 200 | 201 | 200 | Lados inválidos |
      | 200 | 200 | 201 | Lados inválidos |
      | 200 | 199 | 100 | Escaleno        |
      | 1   | 1   | 1   | Equilátero      |

  # ===== BVA: Limite da desigualdade triangular (a + b > c) =====

  Esquema do Cenário: BVA no limite da desigualdade triangular
    Dado que os lados são <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a  | b  | c   | resultado              |
      | 1  | 2  | 3   | Não é um triângulo     |
      | 1  | 3  | 2   | Não é um triângulo     |
      | 3  | 1  | 2   | Não é um triângulo     |
      | 2  | 2  | 3   | Isósceles              |
      | 3  | 2  | 2   | Isósceles              |
      | 2  | 3  | 2   | Isósceles              |
      | 5  | 5  | 9   | Isósceles              |
      | 5  | 5  | 10  | Não é um triângulo     |
      | 100| 100| 199  | Isósceles              |
      | 100| 100| 200  | Não é um triângulo     |

  # ===== BVA: Combinações nos extremos do domínio =====

  Esquema do Cenário: Combinações de valores extremos
    Dado que os lados são <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado          |
      | 1   | 1   | 2   | Não é um triângulo |
      | 1   | 200 | 200 | Isósceles          |
      | 200 | 1   | 200 | Isósceles          |
      | 200 | 200 | 1   | Isósceles          |
      | 100 | 101 | 200 | Escaleno           |
      | 100 | 100 | 100 | Equilátero         |
