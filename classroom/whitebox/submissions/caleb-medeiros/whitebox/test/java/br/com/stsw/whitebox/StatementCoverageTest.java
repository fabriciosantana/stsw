package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementCoverageTest {
    
    @Test
    void shouldExecuteAllStatements() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre todas as instrucoes, incluindo o if (discount > 40)
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }
}
