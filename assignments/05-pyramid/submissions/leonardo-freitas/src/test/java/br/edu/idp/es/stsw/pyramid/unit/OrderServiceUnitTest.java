package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.domain.Order;
import br.edu.idp.es.stsw.pyramid.domain.Product;
import br.edu.idp.es.stsw.pyramid.repository.ProductRepository;
import br.edu.idp.es.stsw.pyramid.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Base da pirâmide — testes unitários")
class OrderServiceUnitTest {

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(productRepository);
    }

    @Test
    void deveProcessarPedidoValido() {
        Product product = new Product(1L, "Teclado", 250.00, 5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        double total = orderService.placeOrder(new Order(1L, 2));

        assertEquals(500.00, total, 0.001);
        assertEquals(3, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void deveRejeitarQuantidadeZero() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(new Order(1L, 0))
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void deveRejeitarQuantidadeNegativa() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(new Order(1L, -1))
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void deveRejeitarProdutoInexistente() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.placeOrder(new Order(99L, 1))
        );
    }

    @Test
    void deveRejeitarEstoqueInsuficiente() {
        Product product = new Product(2L, "Monitor", 1000.00, 1);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));

        assertThrows(
                IllegalStateException.class,
                () -> orderService.placeOrder(new Order(2L, 2))
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deveRejeitarProdutoComPrecoNegativo() {
        Product product = new Product(3L, "Mouse", -1.00, 3);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.registerProduct(product)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deveRejeitarProdutoComEstoqueNegativo() {
        Product product = new Product(4L, "Headset", 300.00, -1);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.registerProduct(product)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void deveRejeitarProdutoSemNome() {
        Product product = new Product(5L, " ", 100.00, 2);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.registerProduct(product)
        );

        verify(productRepository, never()).save(any());
    }
}
