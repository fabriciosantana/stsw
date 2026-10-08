import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OrderE2ESteps {

    private final OrderRepository repository = new OrderRepository();
    private final PaymentService paymentService = new PaymentService();
    private final OrderController controller = new OrderController(repository, paymentService);

    private String currentOrderId;

    @Given("que um pedido com ID {string} eh criado para o cliente {string}")
    public void queUmPedidoComIDEhCriadoParaOCliente(String orderId, String cliente) {
        this.currentOrderId = orderId;
        controller.createOrder(orderId, cliente);
    }

    @When("o produto {string} com preco {double} e quantidade {int} eh adicionado ao pedido")
    public void oProdutoComPrecoEQuantidadeEhAdicionadoAoPedido(String produto, double preco, int quantidade) {
        boolean added = controller.addItemToOrder(currentOrderId, produto, preco, quantidade);
        assertTrue(added);
    }

    @And("o pagamento eh realizado com o cartao {string}")
    public void oPagamentoEhRealizadoComOCartao(String cartao) {
        boolean paid = controller.payOrder(currentOrderId, cartao);
        assertTrue(paid);
    }

    @Then("o status do pedido deve ser {string}")
    public void oStatusDoPedidoDeveSer(String statusEsperado) {
        Optional<Order> optionalOrder = controller.getOrderDetails(currentOrderId);
        assertTrue(optionalOrder.isPresent());
        assertEquals(Order.Status.valueOf(statusEsperado), optionalOrder.get().getStatus());
    }

    @And("o valor final pago deve ser {double}")
    public void oValorFinalPagoDeveSer(double valorFinalEsperado) {
        Optional<Order> optionalOrder = controller.getOrderDetails(currentOrderId);
        assertTrue(optionalOrder.isPresent());
        assertEquals(valorFinalEsperado, optionalOrder.get().getTotalAmount(), 0.01);
    }
}
