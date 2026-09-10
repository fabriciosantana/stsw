# Estudo de Caso: Particionamento em Classes de Equivalência em Reservas de Laboratório

## Objetivo

Neste estudo de caso, a turma deve aplicar **Equivalence Class Partitioning (ECP)**, ou **particionamento em classes de equivalência**, para projetar casos de teste a partir de domínios de entrada e regras de negócio.

Você deve identificar classes válidas e inválidas, escolher um representante de cada classe e justificar por que valores da mesma partição devem produzir comportamento equivalente.

Ao final, cada grupo deve ser capaz de explicar quais partições foram identificadas, quais representantes foram escolhidos e como o conjunto de testes evita casos redundantes.

## Cenário

Uma universidade está implantando um sistema para reservar laboratórios de informática. Antes de registrar uma solicitação, o sistema valida os dados informados e verifica se a reserva pode ser confirmada.

A decisão depende de quatro entradas:

- quantidade de estudantes
- duração da reserva em horas
- antecedência da solicitação em dias
- tipo de solicitante

## Regra de negócio

O sistema deve considerar os seguintes domínios e regras.

### Quantidade de estudantes

- Valores menores que `1` são inválidos.
- De `1` a `20`, inclusive, a turma é considerada pequena.
- De `21` a `40`, inclusive, a turma é considerada regular.
- De `41` a `60`, inclusive, a turma é considerada grande.
- Valores maiores que `60` são inválidos.

### Duração

- Os únicos valores aceitos são `1`, `2`, `3` ou `4` horas.
- Qualquer outro valor é inválido.

### Antecedência

- Valores menores que `0` são inválidos.
- De `0` a `1` dia, a solicitação é urgente.
- De `2` a `30` dias, a solicitação é regular.
- Valores maiores que `30` são inválidos.

### Tipo de solicitante

- `PROFESSOR` é um valor válido.
- `MONITOR` é um valor válido.
- Qualquer outro código é inválido.

### Resultado da solicitação

O método deve aplicar as regras na seguinte ordem:

1. Se qualquer entrada estiver fora do domínio aceito, retornar `DADOS_INVALIDOS`.
2. Solicitações urgentes feitas por `MONITOR` devem retornar `RECUSADA`.
3. Turmas grandes com reserva de `4` horas devem retornar `LISTA_DE_ESPERA`.
4. Nos demais casos válidos, retornar `CONFIRMADA`.

## Valores nominais sugeridos

Use os seguintes valores quando uma entrada não estiver sendo analisada:

- `estudantes = 30`
- `duracao = 2`
- `antecedencia = 10`
- `solicitante = PROFESSOR`

Esses valores pertencem a classes válidas e, combinados, produzem uma reserva confirmada.

## O que desenvolver

Crie um projeto Maven no diretório `classroom/05-ecp` com esta estrutura mínima:

```text
classroom/05-ecp
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

- `PoliticaReservaLaboratorio`

Essa classe deve conter um método público semelhante a:

```java
DecisaoReserva avaliar(int estudantes,
                       int duracao,
                       int diasAntecedencia,
                       TipoSolicitante solicitante)
```

O método deve retornar:

- `CONFIRMADA`
- `LISTA_DE_ESPERA`
- `RECUSADA`
- `DADOS_INVALIDOS`

Crie os enums `DecisaoReserva` e `TipoSolicitante` para representar esses valores.

### Parte 2: Modelo de classes de equivalência

Antes de implementar os testes, registre em uma tabela:

- identificador da classe
- entrada à qual ela pertence
- descrição ou intervalo
- classe válida ou inválida
- representante escolhido
- resultado esperado quando as demais entradas usam valores nominais

Use um formato semelhante a:

| ID | Entrada | Classe de equivalência | Tipo | Representante | Resultado esperado |
|---|---|---|---|---|---|
| `E1` | estudantes | preencher | válida/inválida | preencher | preencher |

Não trate cada valor de um mesmo intervalo como uma nova classe. A partição deve agrupar entradas que, segundo a especificação, tendem a ser processadas da mesma maneira.

### Parte 3: Casos de teste com ECP

Implemente os testes em JUnit 5. Organize-os, por exemplo, em:

- `TesteClassesEquivalenciaValidas`
- `TesteClassesEquivalenciaInvalidas`
- `TesteCombinacoesResultadosNegocio`

## Como derivar os casos

### 1. Classes válidas

Identifique todas as partições válidas de cada entrada. Escolha ao menos um representante de cada uma, mantendo as demais entradas nos valores nominais.

Perguntas para orientar:

- Quantas classes válidas existem para `estudantes`?
- Os quatro valores aceitos de `duracao` são uma única classe ou precisam ser separados para revelar as regras de negócio?
- As classes de `antecedencia` podem levar a decisões diferentes?
- Os dois tipos válidos de solicitante são equivalentes em todos os contextos?

### 2. Classes inválidas

Identifique todas as formas distintas pelas quais cada entrada pode estar fora do domínio. Em cada teste, deixe somente uma entrada inválida e mantenha as demais em valores nominais válidos.

Perguntas para orientar:

- Existem domínios inválidos diferentes abaixo e acima de um intervalo?
- Um código nulo, vazio ou desconhecido de solicitante pertence à mesma partição? Justifique sua decisão.
- Todos os representantes inválidos resultam em `DADOS_INVALIDOS`?

### 3. Combinações entre classes válidas

O uso isolado de representantes não é suficiente para exercitar todos os resultados de negócio. Acrescente combinações válidas que produzam:

- `CONFIRMADA`
- `LISTA_DE_ESPERA`
- `RECUSADA`

Evite o produto cartesiano completo. Escolha somente combinações necessárias para distinguir comportamentos relevantes.

## Critérios de qualidade

O conjunto de testes deve:

- cobrir cada classe de equivalência identificada
- conter ao menos um representante por classe
- isolar uma classe inválida por teste
- cobrir todos os resultados possíveis
- evitar casos redundantes sem justificativa
- usar nomes ou descrições que revelem a intenção de cada teste

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
cd classroom/05-ecp
mvn test
```

### Gerar relatório de cobertura

```bash
cd classroom/05-ecp
mvn verify
```

O relatório HTML do JaCoCo deve ficar em:

```text
target/site/jacoco/index.html
```

## Entrega esperada em sala

Ao final da atividade, cada grupo deve entregar:

- implementação da regra de negócio
- tabela com as classes válidas e inválidas
- representantes escolhidos e suas justificativas
- testes JUnit derivados das partições
- combinações necessárias para cobrir os quatro resultados
- relatório JaCoCo gerado
- uma breve explicação sobre casos eliminados por redundância

## Perguntas para discussão

1. Por que testar todos os valores de uma classe geralmente não acrescenta informação?
2. Como saber se dois valores realmente pertencem à mesma classe de equivalência?
3. Por que uma partição válida pode precisar ser subdividida quando há resultados de negócio diferentes?
4. Qual é a vantagem de manter as outras entradas em valores nominais ao testar uma classe inválida?
5. ECP garante cobertura de fronteiras? Que técnica poderia complementar este exercício?
6. Cobertura de código alta prova que todas as classes relevantes foram testadas?
