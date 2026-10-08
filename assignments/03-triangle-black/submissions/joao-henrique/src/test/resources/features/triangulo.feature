# language: pt
Funcionalidade: Testes de Caixa-Preta (ECP e BVA) para o Classificador de Triângulos
  Como analista de qualidade
  Eu quero avaliar o classificador de triângulo como uma caixa-preta
  Para garantir a conformidade com as classes de equivalência e a análise de valores limite

  Esquema do Cenário: Classes de Equivalência - Tipos válidos de triângulo
    Dado que os valores de entrada para os lados são <a>, <b> e <c>
    Quando a função de classificação da caixa-preta for consultada
    Então a saída observada deve ser "<saida>"

    Exemplos:
      | a   | b   | c   | saida      |
      | 10  | 10  | 10  | Equilátero |
      | 10  | 10  | 6   | Isósceles  |
      | 10  | 6   | 10  | Isósceles  |
      | 6   | 10  | 10  | Isósceles  |
      | 6   | 8   | 10  | Escaleno   |
      | 10  | 12  | 14  | Escaleno   |

  Esquema do Cenário: Classes de Equivalência e Fronteira - Desigualdade Triangular
    Dado que os valores de entrada para os lados são <a>, <b> e <c>
    Quando a função de classificação da caixa-preta for consultada
    Então a saída observada deve ser "<saida>"

    Exemplos:
      | a   | b   | c   | saida              |
      | 1   | 2   | 3   | Não é um triângulo |
      | 3   | 1   | 2   | Não é um triângulo |
      | 2   | 3   | 1   | Não é um triângulo |
      | 1   | 2   | 4   | Não é um triângulo |
      | 12  | 5   | 5   | Não é um triângulo |
      | 5   | 12  | 5   | Não é um triângulo |
      | 10  | 10  | 20  | Não é um triângulo |
      | 10  | 10  | 19  | Isósceles          |

  Esquema do Cenário: BVA - Limites do Domínio 1..200 e Entradas Inválidas
    Dado que os valores de entrada para os lados são <a>, <b> e <c>
    Quando a função de classificação da caixa-preta for consultada
    Então a saída observada deve ser "<saida>"

    Exemplos:
      | a   | b   | c   | saida           |
      | 1   | 1   | 1   | Equilátero      |
      | 2   | 2   | 2   | Equilátero      |
      | 200 | 200 | 200 | Equilátero      |
      | 199 | 199 | 199 | Equilátero      |
      | 0   | 10  | 10  | Lados inválidos |
      | 10  | 0   | 10  | Lados inválidos |
      | 10  | 10  | 0   | Lados inválidos |
      | -1  | 10  | 10  | Lados inválidos |
      | 10  | -5  | 10  | Lados inválidos |
      | 10  | 10  | -10 | Lados inválidos |
      | 201 | 10  | 10  | Lados inválidos |
      | 10  | 201 | 10  | Lados inválidos |
      | 10  | 10  | 201 | Lados inválidos |
      | 205 | 200 | 200 | Lados inválidos |
