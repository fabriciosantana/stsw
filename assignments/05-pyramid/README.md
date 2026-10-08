# Estudo de caso: The Practical Test Pyramid

Execute o tutorial [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html).

- Reaproveite a estrutura Maven com JUnit 5 e Cucumber para construir o projeto da atividade no seu diretório pessoal.
- Implemente uma aplicação simulando um serviço de gerenciamento de pedidos (`OrderService`).
- Estruture a suíte de testes aplicando os princípios da **Pirâmide de Testes (Test Pyramid)** de Martin Fowler:
  1. **Testes Unitários (Unit Tests)**: Testes rápidos e isolados focados no domínio (`Order`, `OrderItem`).
  2. **Testes de Integração (Integration Tests)**: Testes intermediários validando a integração com o repositório em memória e com serviços externos simulados (`OrderRepository`, `PaymentService`).
  3. **Testes de Ponta a Ponta (End-to-End / Acceptance Tests)**: Testes BDD com Cucumber exercitando o fluxo funcional completo via controller de entrada (`OrderController`).
- Execute `mvn test` e garanta que todas as 3 camadas da pirâmide passem com sucesso.
- Submeta o pull request seguindo as orientações gerais das atividades.
