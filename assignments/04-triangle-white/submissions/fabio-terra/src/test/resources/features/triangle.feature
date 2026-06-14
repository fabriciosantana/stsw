# language: pt
Funcionalidade: Identificar tipo de triângulo
  Como um analista de qualidade
  Eu quero validar a classificação de triângulos com 5 casos específicos
  Para garantir que a lógica de negócio está correta

  Esquema do Cenário: Validar classificação
    Dado que os lados informados são <a>, <b> e <c>
    Quando eu executo a identificação
    Então o sistema retorna "<resultado>"

    Exemplos:
      | a  | b | c | resultado           |
      | 5  | 5 | 5 | Equilatero          |
      | 5  | 5 | 3 | Isosceles           |
      | 5  | 4 | 3 | Escaleno            |
      | 1  | 2 | 3 | Nao e um triangulo  |
      | -5 | 0 | 5 | Invalido            |