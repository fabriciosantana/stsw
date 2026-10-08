Feature: Teste de Ponta a Ponta (E2E) - Processamento de Pedido na Piramida de Testes
  Como um cliente da loja virtual
  Eu quero criar um pedido, adicionar produtos e realizar o pagamento
  Para que meu pedido seja confirmado e processado de ponta a ponta

  Scenario Outline: Criar e finalizar um pedido de ponta a ponta
    Given que um pedido com ID "<orderId>" eh criado para o cliente "<cliente>"
    When o produto "<produto>" com preco <preco> e quantidade <quantidade> eh adicionado ao pedido
    And o pagamento eh realizado com o cartao "<cartao>"
    Then o status do pedido deve ser "<statusEsperado>"
    And o valor final pago deve ser <valorFinalEsperado>

    Examples: Fluxos Completos de Pedido (Valores Padrao e com Desconto Automatico)
      | orderId   | cliente      | produto   | preco | quantidade | cartao           | statusEsperado | valorFinalEsperado |
      | ORD-E2E-1 | Kayke Santos | Teclado   | 200.0 | 1          | 1234567890123456 | PAID           | 200.0              |
      | ORD-E2E-2 | Alice Souza  | Notebook  | 800.0 | 1          | 1234567890123456 | PAID           | 720.0              |
