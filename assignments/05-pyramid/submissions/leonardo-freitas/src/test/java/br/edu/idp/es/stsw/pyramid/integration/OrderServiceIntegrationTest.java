package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.domain.Order;
import br.edu.idp.es.stsw.pyramid.domain.Product;
import br.edu.idp.es.stsw.pyramid.repository.InMemoryProductRepository;
import br.edu.idp.es.stsw.pyramid.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Meio da pirâmide — testes de integração")
class OrderServiceIntegrationTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(new InMemoryProductRepository());
    }

    @Test
    void deveCadastrarProcessarPedidoEAtualizarEstoque() {
        orderService.registerProduct(new Product(10L, "Notebook", 3500.00, 4));

        double total = orderService.placeOrder(new Order(10L, 2));

        assertEquals(7000.00, total, 0.001);
        assertEquals(2, orderService.stockOf(10L));
    }

    @Test
    void pedidosSucessivosDevemCompartilharEstadoDoRepositorio() {
        orderService.registerProduct(new Product(20L, "SSD", 500.00, 5));

        orderService.placeOrder(new Order(20L, 2));
        orderService.placeOrder(new Order(20L, 1));

        assertEquals(2, orderService.stockOf(20L));
    }

    @Test
    void deveRecusarSegundoPedidoQuandoEstoqueAcabar() {
        orderService.registerProduct(new Product(30L, "Webcam", 400.00, 1));

        orderService.placeOrder(new Order(30L, 1));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(new Order(30L, 1))
        );

        assertEquals(0, orderService.stockOf(30L));
    }
}
