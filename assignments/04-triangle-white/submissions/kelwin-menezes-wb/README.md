Estudo de Caso: Teste Caixa Branca com Java, JUnit, Maven e JaCoCo
Objetivo
Neste estudo de caso, a turma deve desenvolver em sala um pequeno projeto Java para praticar tecnicas de teste caixa branca.

O foco nao e apenas validar o resultado do programa, mas construir testes que permitam discutir diferentes criterios de cobertura, como:

statement coverage
decision coverage
condition coverage
condition/decision coverage
path coverage
O trabalho deve ser feito sem consultar o exemplo do professor. O objetivo e reproduzir a ideia, nao copiar a implementacao.

Cenario
Uma loja virtual deseja aplicar descontos de acordo com algumas regras comerciais.

O sistema deve possuir uma classe responsavel por calcular o desconto total de uma compra a partir dos seguintes dados:

se o cliente e premium
valor da compra
se o cupom informado e valido
se a compra ocorreu na Black Friday
Regras de negocio
Implemente um metodo com a seguinte logica:

Se o valor da compra for maior ou igual a 100, adicionar 10 porcento de desconto.
Se o cliente for premium, adicionar 5 porcento de desconto.
Se o cupom for valido e o valor da compra for maior ou igual a 200, adicionar 15 porcento de desconto.
Se for Black Friday ou se o cliente for premium e o valor da compra for maior ou igual a 300, adicionar 20 porcento de desconto.
O desconto maximo permitido e 40.
O metodo deve retornar o valor final do desconto.

O que desenvolver
Crie um projeto Maven no diretorio classroom/whitebox com esta estrutura minima:

classroom/whitebox
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── ...
    └── test
        └── java
            └── ...
Parte 1: Programa Java
Desenvolva uma classe de dominio, por exemplo:

DiscountCalculator
Essa classe deve conter um metodo publico semelhante a:

int calculateDiscount(boolean premiumCustomer,
                      int purchaseAmount,
                      boolean couponValid,
                      boolean blackFriday)
Voce pode usar outro nome de classe ou metodo, desde que a logica seja equivalente.

Parte 2: Testes unitarios
Crie testes com JUnit 5 separados por intencao didatica. A sugestao e manter uma classe para cada criterio:

StatementCoverageTest
DecisionCoverageTest
ConditionCoverageTest
ConditionDecisionCoverageTest
PathCoverageTest
Como pensar os testes
1. Statement coverage
Crie um conjunto minimo de testes que execute as principais instrucoes do metodo.

Pergunta para orientar:

Um unico teste consegue executar varias linhas e ainda assim deixar decisoes importantes mal exploradas?
2. Decision coverage
Monte testes que facam cada decisao do metodo assumir true e false.

Perguntas para orientar:

O if (purchaseAmount >= 100) ja foi verdadeiro e falso?
O if (premiumCustomer) ja foi verdadeiro e falso?
As decisoes compostas tambem tiveram seus dois desfechos?
3. Condition coverage
Nas decisoes compostas, faca cada condicao atomica assumir true e false.

Exemplos de condicoes atomicas que devem ser observadas:

couponValid
purchaseAmount >= 200
blackFriday
premiumCustomer
purchaseAmount >= 300
Importante: cobrir condicoes nao significa necessariamente cobrir todas as combinacoes possiveis.

4. Condition/decision coverage
Combine os dois objetivos anteriores:

cada decisao deve assumir true e false
cada condicao atomica deve assumir true e false
5. Path coverage
Selecione caminhos representativos pelo metodo.

Atencao: para este exercicio, nao e necessario enumerar todos os caminhos possiveis. O objetivo e mostrar que o numero de caminhos cresce rapidamente e que a cobranca por cobertura total pode ficar inviavel.

Sugestao de caminhos interessantes:

compra sem nenhum desconto
compra com desconto apenas por valor minimo
compra com desconto por cliente premium
compra com cupom valido
compra com Black Friday
compra com premium + valor alto
compra que atinge o teto maximo de desconto
Requisitos tecnicos
O projeto deve usar:

Java 21
Maven
JUnit 5
JaCoCo
No pom.xml, configure:

maven-compiler-plugin
maven-surefire-plugin
jacoco-maven-plugin
O JaCoCo deve gerar relatorio apos a execucao dos testes.

Como executar
Rodar todos os testes
cd classroom/whitebox
mvn test
Rodar apenas uma classe de teste
cd classroom/whitebox
mvn -Dtest=StatementCoverageTest test
Rodar apenas um metodo de teste
cd classroom/whitebox
mvn -Dtest=StatementCoverageTest#nomeDoMetodo test
Relatorio de cobertura
Depois de executar os testes, o relatorio HTML do JaCoCo deve ficar em:

target/site/jacoco/index.html
O grupo deve abrir esse relatorio e discutir:

quais linhas foram cobertas
quais ramos ficaram sem cobertura
quais condicoes ainda nao assumiram todos os valores
quais testes aumentaram mais a confianca na regra de negocio
Entrega esperada em sala
Ao final da atividade, cada grupo deve ter:

o programa Java implementado
os testes unitarios organizados por criterio de cobertura
o pom.xml configurado com JUnit e JaCoCo
o relatorio gerado
uma explicacao curta do que cada suite de teste demonstra
Perguntas para discussao
Um teste com alta cobertura de linhas garante boa qualidade?
Qual a diferenca pratica entre decision coverage e condition coverage?
Por que path coverage tende a explodir em numero de casos?
Em um projeto real, qual criterio costuma trazer melhor custo-beneficio?