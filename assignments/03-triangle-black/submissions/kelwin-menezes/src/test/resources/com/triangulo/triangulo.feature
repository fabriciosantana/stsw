# language: pt
Funcionalidade: Classificação de Triângulos (BDD + BVA)
  Como um analista de qualidade
  Eu quero verificar a classificação de triângulos com entradas entre 1 e 200
  Para garantir que as regras de negócio e limites sejam respeitados.

  Esquema do Cenário: Validar classes de equivalência e limites
    Dado que os lados do triângulo são <l1>, <l2> e <l3>
    Quando eu executo a classificação
    Então o resultado deve ser "<resultado>"

    Exemplos:
      | l1  | l2  | l3  | resultado           | Caso de Teste (Motivo) |
      | 5   | 5   | 5   | Equilátero          | Classe Equilátero      |
      | 10  | 10  | 15  | Isósceles           | Classe Isósceles       |
      | 3   | 4   | 5   | Escaleno            | Classe Escaleno        |
      | 1   | 2   | 3   | Não é um triângulo  | Inexistência           |
      | 1   | 1   | 1   | Equilátero          | BVA: Limite Mínimo (1) |
      | 200 | 200 | 200 | Equilátero          | BVA: Limite Máximo (200)|
      | 0   | 100 | 100 | Lados inválidos     | BVA: Abaixo do Mínimo  |
      | 201 | 100 | 100 | Lados inválidos     | BVA: Acima do Máximo   |
      | 100 | 201 | 100 | Lados inválidos     | BVA: Limite Lateral    |