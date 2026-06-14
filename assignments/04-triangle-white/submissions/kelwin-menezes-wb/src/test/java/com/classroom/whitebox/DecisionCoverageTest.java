package com.classroom.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class DecisionCoverageTest {

    @Test
    void testAllDecisions() {
        DiscountCalculator calculator = new DiscountCalculator();

        // Caso onde todos os IFs de acréscimo são TRUE e o IF do teto é TRUE
        assertEquals(40, calculator.calculateDiscount(true, 300, true, true));

        // Caso onde todos os IFs são FALSE (inclusive o do teto)
        assertEquals(0, calculator.calculateDiscount(false, 50, false, false));
    }
}