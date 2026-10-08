package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionCoverageTest {

    @Test
    void shouldEvaluateAllDecisionsToTrue() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Todas as decisoes (ifs) sao avaliadas como true.
        int discount = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount);
    }

    @Test
    void shouldEvaluateAllDecisionsToFalse() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Todas as decisoes (ifs) sao avaliadas como false.
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }
}
