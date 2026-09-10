# Estudo de Caso: Teste Baseado em Tabela de Decisão em Reembolsos de Viagem

## Objetivo

Neste estudo de caso, a turma deve aplicar **Decision Table Testing**, ou **teste baseado em tabela de decisão**, para transformar regras de negócio combinatórias em casos de teste sistemáticos.

Você deve identificar condições e ações, construir uma tabela de decisão, verificar regras impossíveis ou redundantes e derivar ao menos um teste para cada coluna executável.

Ao final, cada grupo deve ser capaz de explicar como cada combinação de condições leva a uma decisão e por que todas as regras relevantes foram exercitadas.

## Cenário

Uma organização utiliza um sistema para analisar pedidos de reembolso de viagens profissionais. A resposta depende da documentação apresentada, do prazo de envio, da autorização prévia e do valor solicitado.

Uma combinação incorreta dessas condições pode causar pagamento indevido ou recusa de uma despesa legítima.

## Condições

O sistema avalia quatro condições booleanas:

- `documentacaoCompleta`: todos os comprovantes obrigatórios foram apresentados
- `enviadoNoPrazo`: o pedido foi enviado dentro do prazo institucional
- `viagemAutorizada`: a viagem possuía autorização prévia
- `valorAlto`: o valor solicitado é superior a `R$ 5.000,00`

Para este exercício, `valorAlto` já deve ser recebido como uma condição booleana. O limite monetário não é o foco da atividade.

## Ações possíveis

O sistema deve retornar exatamente uma das seguintes decisões:

- `APROVADO`
- `REVISAO_MANUAL`
- `RECUSADO`

## Regras de negócio

As regras possuem a seguinte prioridade:

1. Se a viagem não foi autorizada, o pedido deve ser `RECUSADO`, independentemente das demais condições.
2. Se a documentação estiver incompleta, o pedido deve ser `RECUSADO`, mesmo que a viagem tenha sido autorizada.
3. Com viagem autorizada e documentação completa, pedidos de valor alto devem ir para `REVISAO_MANUAL`.
4. Com viagem autorizada, documentação completa e valor não alto, um pedido fora do prazo deve ir para `REVISAO_MANUAL`.
5. Com viagem autorizada, documentação completa, valor não alto e envio no prazo, o pedido deve ser `APROVADO`.

Quando uma condição não alterar o resultado de uma regra, marque-a com `-` (*don't care*) na tabela consolidada.

## O que desenvolver

Crie um projeto Maven no diretório `classroom/06-decisao-table` com esta estrutura mínima:

```text
classroom/06-decisao-table
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── ...
    └── test
        └── java
            └── ...
```

### Parte 1: Programa Java

Desenvolva uma classe de domínio, por exemplo:

- `PoliticaReembolsoViagem`

Essa classe deve conter um método público semelhante a:

```java
DecisaoReembolso avaliar(boolean documentacaoCompleta,
                         boolean enviadoNoPrazo,
                         boolean viagemAutorizada,
                         boolean valorAlto)
```

Crie o enum `DecisaoReembolso` para representar as ações possíveis.

### Parte 2: Tabela de decisão completa

Construa inicialmente a tabela completa com todas as combinações possíveis.

Como há quatro condições booleanas, a tabela completa deve conter:

```text
2^4 = 16 regras
```

Use `V` para verdadeiro e `F` para falso. Numere as colunas de `R1` a `R16` e associe exatamente uma ação a cada regra.

Modelo inicial:

| Condições e ações | R1 | R2 | ... | R16 |
|---|:---:|:---:|:---:|:---:|
| Documentação completa? |  |  |  |  |
| Enviado no prazo? |  |  |  |  |
| Viagem autorizada? |  |  |  |  |
| Valor alto? |  |  |  |  |
| **Aprovar** |  |  |  |  |
| **Enviar para revisão manual** |  |  |  |  |
| **Recusar** |  |  |  |  |

Preencha a tabela em um arquivo Markdown no projeto e mantenha uma ordem consistente para alternar os valores das condições.

### Parte 3: Tabela de decisão consolidada

Depois de completar as 16 regras, consolide colunas que tenham a mesma ação e nas quais uma ou mais condições sejam irrelevantes.

A tabela consolidada deve:

- preservar o comportamento das 16 combinações originais
- usar `-` somente quando os dois valores da condição produzirem a mesma ação naquele contexto
- não combinar regras que dependam de prioridades diferentes sem justificativa
- associar exatamente uma ação a cada coluna

Para cada consolidação, indique quais regras da tabela completa foram agrupadas.

### Parte 4: Casos de teste

Implemente os testes em JUnit 5. Cada coluna executável da tabela consolidada deve originar ao menos um caso de teste.

Sugestão de organização:

- `TesteTabelaDecisaoCompleta`, para a tabela de 16 combinações
- `TesteTabelaDecisaoConsolidada`, para o conjunto reduzido

Os testes podem ser parametrizados com `@ParameterizedTest` e `@MethodSource` ou `@CsvSource`.

## Como derivar os casos

### 1. Enumere as combinações

Liste todas as combinações de `V` e `F` antes de calcular as ações.

Perguntas para orientar:

- Todas as 16 combinações aparecem exatamente uma vez?
- A ordem escolhida permite detectar rapidamente uma combinação ausente ou duplicada?

### 2. Aplique a prioridade das regras

Determine a ação de cada coluna começando pelas regras de maior prioridade.

Perguntas para orientar:

- Quando `viagemAutorizada` é falsa, alguma outra condição muda o resultado?
- Quando a documentação é incompleta, prazo e valor ainda são relevantes?
- Em quais situações o prazo deixa de influenciar a decisão?

### 3. Consolide com *don't care*

Agrupe somente colunas que diferem em uma condição irrelevante e possuem a mesma ação.

Perguntas para orientar:

- O símbolo `-` representa qualquer valor ou um valor ainda não definido?
- É possível expandir cada coluna consolidada e recuperar as 16 regras originais?
- Quantos testes são economizados após a consolidação?

### 4. Transforme regras em testes

Para uma condição marcada com `-`, escolha `V` ou `F` como representante e registre a escolha. Se necessário, use representantes diferentes para demonstrar que a condição é realmente irrelevante.

Cada teste deve informar:

- identificador da regra
- valores das quatro condições
- decisão esperada

## Critérios de qualidade

O trabalho deve demonstrar:

- completude da tabela original
- ausência de combinações duplicadas
- uma única ação por regra
- respeito à prioridade declarada
- consolidação semanticamente correta
- rastreabilidade entre regra da tabela e teste JUnit

## Requisitos técnicos

O projeto deve usar:

- Java 21
- Maven
- JUnit 5
- JaCoCo

No `pom.xml`, configure:

- `maven-compiler-plugin`
- `maven-surefire-plugin`
- `jacoco-maven-plugin`

## Como executar

### Rodar todos os testes

```bash
cd classroom/06-decisao-table
mvn test
```

### Gerar relatório de cobertura

```bash
cd classroom/06-decisao-table
mvn verify
```

O relatório HTML do JaCoCo deve ficar em:

```text
target/site/jacoco/index.html
```

## Entrega esperada em sala

Ao final da atividade, cada grupo deve entregar:

- implementação da regra de negócio
- tabela de decisão completa com 16 regras
- tabela consolidada com as justificativas dos agrupamentos
- testes derivados das regras consolidadas
- rastreabilidade entre colunas e testes
- relatório JaCoCo gerado
- uma breve comparação entre a quantidade de regras antes e depois da consolidação

## Perguntas para discussão

1. Que defeitos uma tabela de decisão ajuda a encontrar melhor do que testes escolhidos informalmente?
2. Por que a prioridade entre regras precisa estar explícita?
3. Quando é seguro substituir uma condição por `-`?
4. Uma tabela consolidada pode ocultar uma combinação importante? Como validar a consolidação?
5. Cobrir todas as linhas de código equivale a cobrir todas as regras da tabela?
6. O que aconteceria com o número de colunas se fossem adicionadas mais duas condições booleanas?
