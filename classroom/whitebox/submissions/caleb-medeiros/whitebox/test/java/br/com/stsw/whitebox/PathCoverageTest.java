package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathCoverageTest {

    @Test
    void compraSemNenhumDesconto() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }

    @Test
    void compraComDescontoApenasPorValorMinimo() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(false, 150, false, false);
        assertEquals(10, result);
    }

    @Test
    void compraComDescontoPorClientePremium() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, result);
    }

    @Test
    void compraComCupomValidoEValorMaiorQue200() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(false, 250, true, false);
        assertEquals(25, result); // 10 + 15
    }

    @Test
    void compraComBlackFriday() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, result);
    }

    @Test
    void compraComPremiumEValorAlto() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(true, 350, false, false);
        assertEquals(35, result); // 10 + 5 + 20
    }

    @Test
    void compraQueAtingeTetoMaximo() {
        DiscountCalculator calculator = new DiscountCalculator();
        int result = calculator.calculateDiscount(true, 350, true, true);
        assertEquals(40, result); // 10 + 5 + 15 + 20 = 50 -> 40
    }
}
