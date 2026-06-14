package com.classroom.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class StatementCoverageTest {

    @Test
    void testFullStatementCoverage() {
        DiscountCalculator calculator = new DiscountCalculator();
        
        // Com este caso, quase todas as linhas de incremento e o teto (40) são executados
        int result = calculator.calculateDiscount(true, 350, true, true);
        assertEquals(40, result);
        
        // Um caso simples para garantir que a lógica de "não entrar" nos IFs também ocorra
        int resultZero = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, resultZero);
    }
}