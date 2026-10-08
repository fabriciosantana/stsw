package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatementCoverageTest {

    @Test
    void shouldExecuteAllStatementsWithOneTest() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Um unico teste que faz todas as condicoes serem verdadeiras
        // para executar todas as instrucoes (statement coverage).
        int discount = calculator.calculateDiscount(true, 350, true, true);
        assertEquals(40, discount); // 10 + 5 + 15 + 20 = 50, capped at 40
    }
}
