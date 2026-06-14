package br.edu.idp.es.stsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PathCoverageTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    // P1: sem nenhum desconto (compra baixa, cliente comum, sem cupom, sem BF)
    @Test
    void p1_semNenhumDesconto() {
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));
    }

    // P2: somente regra 1 ativa
    @Test
    void p2_descontoApenasValorMinimo() {
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));
    }

    // P3: somente regra 2 ativa (premium, compra baixa)
    @Test
    void p3_descontoPorClientePremium() {
        assertEquals(5, calc.calculateDiscount(true, 50, false, false));
    }

    // P4: regras 1 e 3 ativas (cupom valido + valor >= 200)
    @Test
    void p4_descontoPorCupomValido() {
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));
        // +10 +15
    }

    // P5: regras 1 e 4 ativas via Black Friday
    @Test
    void p5_descontoPorBlackFriday() {
        assertEquals(30, calc.calculateDiscount(false, 100, false, true));
        // +10 +20
    }

    // P6: regras 1, 2 e 4 ativas (premium com compra >= 300)
    @Test
    void p6_premiumComCompraAlta() {
        assertEquals(35, calc.calculateDiscount(true, 300, false, false));
        // +10 +5 +20
    }

    // P7: todas as regras ativas, teto de 40 aplicado
    @Test
    void p7_tetoMaximoDeDesconto() {
        // +10 +5 +15 +20 = 50 -> cap em 40
        assertEquals(40, calc.calculateDiscount(true, 350, true, false));
    }
}
