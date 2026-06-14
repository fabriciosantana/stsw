# language: pt
Funcionalidade: Classificação de Triângulos com BVA (Boundary Value Analysis)
  Como um testador de software
  Quero validar a classificação de triângulos
  Para garantir que o sistema identifica corretamente tipos de triângulos e casos inválidos
  Usando técnicas de caixa-preta (Black Box Testing)

  # ========================================
  # CLASSE 1: Lados Inválidos (BVA - Fora do domínio 1-200)
  # ========================================
  Esquema do Cenário: Validar lados inválidos (valores fora do domínio)
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Valores fora do domínio (BVA)
      | a   | b   | c   | resultado             |
      | 0   | 10  | 10  | Lados inválidos       |
      | -1  | 10  | 10  | Lados inválidos       |
      | -5  | 20  | 20  | Lados inválidos       |
      | 10  | 0   | 10  | Lados inválidos       |
      | 10  | -1  | 10  | Lados inválidos       |
      | 10  | 10  | 0   | Lados inválidos       |
      | 10  | 10  | -5  | Lados inválidos       |
      | 201 | 10  | 10  | Lados inválidos       |
      | 202 | 50  | 50  | Lados inválidos       |
      | 10  | 201 | 10  | Lados inválidos       |
      | 10  | 10  | 201 | Lados inválidos       |
      | 300 | 300 | 300 | Lados inválidos       |

  # ========================================
  # CLASSE 2: Não forma triângulo (desigualdade triangular)
  # ========================================
  Esquema do Cenário: Validar desigualdade triangular
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Violações da desigualdade triangular (BVA)
      | a   | b   | c   | resultado             |
      | 1   | 2   | 3   | Não é um triângulo    |
      | 1   | 1   | 2   | Não é um triângulo    |
      | 2   | 1   | 1   | Não é um triângulo    |
      | 1   | 10  | 20  | Não é um triângulo    |
      | 10  | 1   | 1   | Não é um triângulo    |
      | 1   | 2   | 4   | Não é um triângulo    |
      | 5   | 5   | 10  | Não é um triângulo    |
      | 200 | 1   | 1   | Não é um triângulo    |
      | 100 | 100 | 200 | Não é um triângulo    |

  # ========================================
  # CLASSE 3: Triângulo Equilátero (a == b == c)
  # ========================================
  Esquema do Cenário: Validar triângulo equilátero
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Triângulos equiláteros (BVA)
      | a   | b   | c   | resultado             |
      | 1   | 1   | 1   | Equilátero            |
      | 2   | 2   | 2   | Equilátero            |
      | 5   | 5   | 5   | Equilátero            |
      | 10  | 10  | 10  | Equilátero            |
      | 50  | 50  | 50  | Equilátero            |
      | 100 | 100 | 100 | Equilátero            |
      | 200 | 200 | 200 | Equilátero            |

  # ========================================
  # CLASSE 4: Triângulo Isósceles (exatamente 2 lados iguais)
  # ========================================
  Esquema do Cenário: Validar triângulo isósceles
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Triângulos isósceles (BVA)
      | a   | b   | c   | resultado             |
      | 2   | 2   | 3   | Isósceles             |
      | 5   | 5   | 7   | Isósceles             |
      | 5   | 7   | 5   | Isósceles             |
      | 7   | 5   | 5   | Isósceles             |
      | 10  | 10  | 15  | Isósceles             |
      | 10  | 15  | 10  | Isósceles             |
      | 15  | 10  | 10  | Isósceles             |
      | 100 | 100 | 150 | Isósceles             |
      | 200 | 200 | 100 | Isósceles             |

  # ========================================
  # CLASSE 5: Triângulo Escaleno (todos diferentes)
  # ========================================
  Esquema do Cenário: Validar triângulo escaleno
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Triângulos escalenos (BVA)
      | a   | b   | c   | resultado             |
      | 3   | 4   | 5   | Escaleno              |
      | 2   | 3   | 4   | Escaleno              |
      | 10  | 11  | 20  | Escaleno              |
      | 20  | 30  | 40  | Escaleno              |
      | 50  | 100 | 120 | Escaleno              |
      | 100 | 150 | 200 | Escaleno              |

  # ========================================
  # CASOS ESPECIAIS DE VALOR LIMITE
  # ========================================
  Esquema do Cenário: Validar casos limite especiais
    Dado que eu tenho lados <a>, <b> e <c>
    Quando eu classifico o triângulo
    Então o resultado deve ser "<resultado>"

    Exemplos: Casos limite e transições (BVA)
      | a   | b   | c   | resultado             |
      | 1   | 2   | 2   | Isósceles             |
      | 1   | 1   | 2   | Não é um triângulo    |
      | 2   | 2   | 4   | Não é um triângulo    |
      | 2   | 2   | 3   | Isósceles             |
      | 200 | 200 | 1   | Isósceles             |
      | 200 | 100 | 100 | Não é um triângulo    |
      | 1   | 100 | 100 | Isósceles             |