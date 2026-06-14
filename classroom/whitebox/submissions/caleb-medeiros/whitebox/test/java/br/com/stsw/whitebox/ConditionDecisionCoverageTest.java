package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionDecisionCoverageTest {

    @Test
    void testConditionDecisionSet1() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre decisoes com true, considerando short-circuit
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }

    @Test
    void testConditionDecisionSet2() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre decisoes com false, e condicoes nao cobertas no set 1 ganham false
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }
    
    @Test
    void testConditionDecisionSet3() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Sem Black Friday para forçar a avaliacao da condicao da direita do 'ou'
        int result = calculator.calculateDiscount(true, 350, false, false);
        assertEquals(35, result);
    }
}
