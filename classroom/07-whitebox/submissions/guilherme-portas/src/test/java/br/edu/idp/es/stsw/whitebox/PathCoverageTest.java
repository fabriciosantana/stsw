package br.edu.idp.es.stsw.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Path Coverage: caminhos representativos, sem enumerar todos.
 * O numero de caminhos explode (Multiple-condition Coverage.md),
 * por isso o README pede apenas os 7 caminhos interessantes.
 */
class PathCoverageTest {

    @Test
    @DisplayName("Caminho 1: compra sem nenhum desconto")
    void compraSemDesconto() {
        assertEquals(0, DiscountCalculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    @DisplayName("Caminho 2: desconto apenas por valor minimo")
    void descontoApenasPorValorMinimo() {
        assertEquals(10, DiscountCalculator.calculateDiscount(false, 100, false, false));
    }

    @Test
    @DisplayName("Caminho 3: desconto por cliente premium")
    void descontoPorClientePremium() {
        assertEquals(5, DiscountCalculator.calculateDiscount(true, 50, false, false));
    }

    @Test
    @DisplayName("Caminho 4: compra com cupom valido")
    void compraComCupomValido() {
        assertEquals(25, DiscountCalculator.calculateDiscount(false, 200, true, false));
    }

    @Test
    @DisplayName("Caminho 5: compra com Black Friday")
    void compraComBlackFriday() {
        assertEquals(30, DiscountCalculator.calculateDiscount(false, 100, false, true));
    }

    @Test
    @DisplayName("Caminho 6: compra com premium + valor alto")
    void compraPremiumComValorAlto() {
        assertEquals(35, DiscountCalculator.calculateDiscount(true, 300, false, false));
    }

    @Test
    @DisplayName("Caminho 7: compra que atinge o teto maximo de desconto")
    void compraComTetoMaximo() {
        assertEquals(40, DiscountCalculator.calculateDiscount(true, 500, true, true));
    }
}
