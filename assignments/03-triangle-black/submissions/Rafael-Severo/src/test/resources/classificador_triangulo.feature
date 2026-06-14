# language: pt
Funcionalidade: Classificação de Triângulos
  Como um usuário do sistema de matemática
  Eu quero classificar triângulos com base no tamanho dos seus lados
  Para saber se é Equilátero, Isósceles, Escaleno, ou inválido

  Esquema do Cenário: Classificar diferentes combinações de lados
    Dado que eu informo os lados do triângulo como <lado_a>, <lado_b> e <lado_c>
    Quando eu peço para classificar
    Então o resultado retornado deve ser "<resultado_esperado>"

    Exemplos:
      | lado_a | lado_b | lado_c | resultado_esperado   |
      # Triângulos Válidos - Equilátero
      | 1      | 1      | 1      | Equilátero           |
      | 100    | 100    | 100    | Equilátero           |
      | 200    | 200    | 200    | Equilátero           |
      # Triângulos Válidos - Isósceles
      | 2      | 2      | 1      | Isósceles            |
      | 2      | 1      | 2      | Isósceles            |
      | 1      | 2      | 2      | Isósceles            |
      | 100    | 100    | 50     | Isósceles            |
      | 200    | 200    | 100    | Isósceles            |
      # Triângulos Válidos - Escaleno
      | 3      | 4      | 5      | Escaleno             |
      | 5      | 12     | 13     | Escaleno             |
      | 7      | 24     | 25     | Escaleno             |
      | 50     | 60     | 70     | Escaleno             |
      | 150    | 160    | 170    | Escaleno             |
      # Regra de Formação - Não é triângulo (soma de dois lados <= terceiro)
      | 1      | 1      | 2      | Não é um triângulo   |
      | 1      | 2      | 3      | Não é um triângulo   |
      | 2      | 1      | 3      | Não é um triângulo   |
      | 3      | 1      | 2      | Não é um triângulo   |
      | 1      | 1      | 3      | Não é um triângulo   |
      | 10     | 2      | 2      | Não é um triângulo   |
      | 100    | 50     | 50     | Não é um triângulo   |
      | 200    | 100    | 100    | Não é um triângulo   |
      # Limites de Valores - Lados inválidos (<=0 ou >200)
      | 0      | 5      | 5      | Lados inválidos      |
      | -1     | 5      | 5      | Lados inválidos      |
      | 5      | 0      | 5      | Lados inválidos      |
      | 5      | -1     | 5      | Lados inválidos      |
      | 5      | 5      | 0      | Lados inválidos      |
      | 5      | 5      | -1     | Lados inválidos      |
      | 201    | 5      | 5      | Lados inválidos      |
      | 5      | 201    | 5      | Lados inválidos      |
      | 5      | 5      | 201    | Lados inválidos      |
      # Análise de Valores Limite - Próximos aos limites
      | 1      | 1      | 1      | Equilátero           |  # Já incluído
      | 2      | 2      | 2      | Equilátero           |
      | 199    | 199    | 199    | Equilátero           |
      | 200    | 200    | 200    | Equilátero           |  # Já incluído
      | 1      | 2      | 2      | Isósceles            |  # Já incluído
      | 2      | 2      | 1      | Isósceles            |  # Já incluído
      | 199    | 199    | 198    | Isósceles            |
      | 200    | 200    | 199    | Isósceles            |
      | 2      | 3      | 4      | Escaleno             |
      | 199    | 198    | 197    | Escaleno             |
      | 200    | 199    | 198    | Escaleno             |
      # Casos de fronteira para desigualdade triangular
      | 1      | 1      | 1      | Equilátero           |  # Válido mínimo
      | 1      | 1      | 2      | Não é um triângulo   |  # Já incluído
      | 1      | 2      | 2      | Isósceles            |  # Já incluído
      | 100    | 100    | 199    | Isósceles            |  # 100+100 > 199
      | 100    | 100    | 200    | Não é um triângulo   |  # 100+100 = 200
      | 199    | 1      | 199    | Isósceles            |  # 199+1 > 199
      | 200    | 199    | 199    | Isósceles            |  # 200+199 > 199
