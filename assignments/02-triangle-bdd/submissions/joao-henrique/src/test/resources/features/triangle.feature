# language: pt
Funcionalidade: Classificação de Triângulos com BDD e BVA
  Como usuário do sistema de geometria
  Eu quero fornecer o tamanho de três lados inteiros
  Para obter a classificação do triângulo e a validação dos limites do domínio (1 a 200)

  Esquema do Cenário: Classificação típica de triângulos válidos
    Dado que os lados fornecidos são <a>, <b> e <c>
    Quando o classificador de triângulo é executado
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado          |
      | 5   | 5   | 5   | Equilátero         |
      | 5   | 5   | 3   | Isósceles          |
      | 5   | 3   | 5   | Isósceles          |
      | 3   | 5   | 5   | Isósceles          |
      | 3   | 4   | 5   | Escaleno           |
      | 7   | 8   | 9   | Escaleno           |

  Esquema do Cenário: Análise de valores limite (BVA) nas bordas do domínio 1..200
    Dado que os lados fornecidos são <a>, <b> e <c>
    Quando o classificador de triângulo é executado
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado          |
      | 1   | 1   | 1   | Equilátero         |
      | 2   | 2   | 2   | Equilátero         |
      | 200 | 200 | 200 | Equilátero         |
      | 199 | 199 | 199 | Equilátero         |
      | 0   | 100 | 100 | Lados inválidos    |
      | 100 | 0   | 100 | Lados inválidos    |
      | 100 | 100 | 0   | Lados inválidos    |
      | -1  | 100 | 100 | Lados inválidos    |
      | 201 | 100 | 100 | Lados inválidos    |
      | 100 | 201 | 100 | Lados inválidos    |
      | 100 | 100 | 201 | Lados inválidos    |

  Esquema do Cenário: Fronteiras da desigualdade triangular
    Dado que os lados fornecidos são <a>, <b> e <c>
    Quando o classificador de triângulo é executado
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | a   | b   | c   | resultado          |
      | 1   | 2   | 3   | Não é um triângulo |
      | 3   | 1   | 2   | Não é um triângulo |
      | 2   | 3   | 1   | Não é um triângulo |
      | 5   | 5   | 10  | Não é um triângulo |
      | 5   | 5   | 11  | Não é um triângulo |
      | 5   | 5   | 9   | Isósceles          |
