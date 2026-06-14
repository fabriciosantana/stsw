package com.classroom.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class PathCoverageTest {

    @Test
    void testDiversePaths() {
        DiscountCalculator calc = new DiscountCalculator();

        // Caminho 1: Sem nenhum desconto (Gasto mínimo)
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));

        // Caminho 2: Apenas desconto de valor mínimo (>= 100)
        assertEquals(10, calc.calculateDiscount(false, 100, false, false));

        // Caminho 3: Cliente Premium com compra pequena
        assertEquals(15, calc.calculateDiscount(true, 100, false, false));

        // Caminho 4: Cupom válido em compra de valor alto
        assertEquals(25, calc.calculateDiscount(false, 200, true, false));

        // Caminho 5: Black Friday pura
        assertEquals(30, calc.calculateDiscount(false, 100, false, true));

        // Caminho 6: Bate no teto máximo (40)
        // Premium (5) + Valor (10) + Cupom (15) + BlackFriday (20) = 50 -> Teto 40
        assertEquals(40, calc.calculateDiscount(true, 300, true, true));
    }
}