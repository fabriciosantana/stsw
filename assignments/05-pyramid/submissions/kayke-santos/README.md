# Submissão - Atividade 05: Estudo de Caso The Practical Test Pyramid

Este projeto implementa o estudo de caso da **Pirâmide de Testes (Test Pyramid)** de Martin Fowler/Ham Vocke para a disciplina de Software Testing & Quality.

## Estrutura da Pirâmide de Testes

A suíte de testes está dividida em 3 camadas bem definidas:

1. **Camada 1: Testes Unitários (Base da Pirâmide - Rápidos, Isolados, Alta Quantidade)**
   - `OrderUnitTest.java`: Valida entidades de domínio `Order` e `OrderItem`, cálculos de subtotal/total e validações de regras de negócio.
   - `PaymentServiceUnitTest.java`: Valida o serviço de pagamento, regras de descontos automáticos acima de R$ 500 e tratamento de erros.

2. **Camada 2: Testes de Integração (Camada Intermediária - Integração entre Componentes)**
   - `OrderRepositoryIntegrationTest.java`: Valida a persistência, consulta e atualização de estado de pedidos no repositório de dados.

3. **Camada 3: Testes de Ponta a Ponta / Aceitação (Topo da Pirâmide - Funcional BDD E2E)**
   - `order_pyramid_e2e.feature`: Especificação Gherkin BDD descrevendo o fluxo completo do usuário (criação de pedido, inclusão de itens e pagamento).
   - `OrderE2ESteps.java`: Step definitions orquestrando a execução de ponta a ponta via `OrderController`.
   - `RunPyramidE2ETest.java`: Cucumber Test Runner via JUnit Platform Suite.

---

## Como Executar os Testes

Execute o comando Maven para rodar todas as camadas da pirâmide e gerar o relatório de cobertura JaCoCo:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-16.0.2"; .\mvnw.cmd clean test
```

### Relatório de Cobertura JaCoCo:
Após a execução dos testes, o relatório de cobertura é gerado em:
`target/site/jacoco/index.html`
