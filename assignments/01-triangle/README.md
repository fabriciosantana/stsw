## Especificação Detalhada: Problema do Triângulo

O problema do triângulo envolve a criação de um programa que determina o tipo de triângulo baseado nas medidas de seus três lados. O objetivo é desenvolver uma solução que identifique corretamente o tipo de triângulo (equilátero, isósceles ou escaleno) e implemente testes automatizados para verificar a correta classificação dos triângulos. Esta especificação detalha os requisitos funcionais, casos de uso, e critérios de aceitação.

### 1. Requisitos Funcionais

1. **Entrada de Dados:**
* O programa deve aceitar três números inteiros positivos como entrada, representando os lados do triângulo: `a`, `b`, e `c`.
* Os valores de entrada devem ser validados para garantir que são números inteiros positivos, entre 1 e 200.   

2. **Validação dos Lados do Triângulo:**
   - O programa deve verificar se os três lados fornecidos podem formar um triângulo válido. Para ser um triângulo válido, as seguintes condições devem ser satisfeitas:
     - \( a + b > c \)
     - \( a + c > b \)
     - \( b + c > a \)
   - Se os lados fornecidos não satisfizerem essas condições, o programa deve informar que os lados não formam um triângulo.

3. **Classificação do Triângulo:**
   - **Equilátero**: Todos os três lados são iguais (\( a = b = c \)).
   - **Isósceles**: Dois lados são iguais (\( a = b \), \( a = c \), ou \( b = c \)).
   - **Escaleno**: Todos os três lados são diferentes (\( a \neq b \neq c \)).

4. **Saída de Dados:**
   - Se os lados formam um triângulo válido, o programa deve retornar uma string indicando o tipo do triângulo: "Equilátero", "Isósceles" ou "Escaleno".
   - Se os lados não formam um triângulo, o programa deve retornar uma mensagem: "Não é um triângulo".

### 2. Casos de Uso

**Caso de Uso 1: Entrada Válida – Triângulo Equilátero**
- **Pré-condições**: O usuário fornece três lados iguais.
- **Fluxo Principal**:
  1. O usuário fornece os lados `a = 5`, `b = 5`, `c = 5`.
  2. O programa verifica que os lados satisfazem a condição de triângulo.
  3. O programa classifica o triângulo como "Equilátero".
  4. O programa exibe a mensagem "Equilátero".
- **Pós-condições**: O tipo do triângulo é identificado corretamente como equilátero.

**Caso de Uso 2: Entrada Válida – Triângulo Isósceles**
- **Pré-condições**: O usuário fornece dois lados iguais e um diferente.
- **Fluxo Principal**:
  1. O usuário fornece os lados `a = 5`, `b = 5`, `c = 3`.
  2. O programa verifica que os lados satisfazem a condição de triângulo.
  3. O programa classifica o triângulo como "Isósceles".
  4. O programa exibe a mensagem "Isósceles".
- **Pós-condições**: O tipo do triângulo é identificado corretamente como isósceles.

**Caso de Uso 3: Entrada Válida – Triângulo Escaleno**
- **Pré-condições**: O usuário fornece três lados diferentes.
- **Fluxo Principal**:
  1. O usuário fornece os lados `a = 5`, `b = 4`, `c = 3`.
  2. O programa verifica que os lados satisfazem a condição de triângulo.
  3. O programa classifica o triângulo como "Escaleno".
  4. O programa exibe a mensagem "Escaleno".
- **Pós-condições**: O tipo do triângulo é identificado corretamente como escaleno.

**Caso de Uso 4: Entrada Inválida – Não Forma Triângulo**
- **Pré-condições**: O usuário fornece lados que não podem formar um triângulo.
- **Fluxo Principal**:
  1. O usuário fornece os lados `a = 1`, `b = 2`, `c = 3`.
  2. O programa verifica que os lados não satisfazem a condição de triângulo.
  3. O programa exibe a mensagem "Não é um triângulo".
- **Pós-condições**: O programa informa corretamente que os lados fornecidos não formam um triângulo.

**Caso de Uso 5: Entrada Inválida – Lados Negativos ou Zero**
- **Pré-condições**: O usuário fornece lados que incluem valores negativos ou zero.
- **Fluxo Principal**:
  1. O usuário fornece os lados `a = -5`, `b = 0`, `c = 5`.
  2. O programa valida que os lados devem ser números inteiros positivos.
  3. O programa exibe a mensagem "Lados inválidos".
- **Pós-condições**: O programa valida corretamente os lados e informa a invalidade dos dados fornecidos.

### 3. Critérios de Aceitação

- O programa deve ser capaz de identificar corretamente o tipo de triângulo para qualquer combinação válida de lados.
- O programa deve detectar e informar corretamente quando os lados fornecidos não podem formar um triângulo.
- O programa deve lidar com entradas inválidas (como números negativos ou zero) e fornecer uma mensagem de erro adequada.

### 5. Ferramentas e Tecnologias

- **Linguagem de Programação**: `Java`.
- **Framework de Testes**: `Junit` e `cucumber-java` para desenvolvimento orientado por comportamento (BDD).
- **Editor de Código/IDE**: Visual Studio Code ou qualquer outro editor com suporte para Python.
