package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DecisionCoverageTest {

    @Test
    void shouldEvaluateAllDecisionsAsTrue() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Todas as decisoes (ifs) sao avaliadas como true
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }

    @Test
    void shouldEvaluateAllDecisionsAsFalse() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Todas as decisoes (ifs) sao avaliadas como false
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }
}
