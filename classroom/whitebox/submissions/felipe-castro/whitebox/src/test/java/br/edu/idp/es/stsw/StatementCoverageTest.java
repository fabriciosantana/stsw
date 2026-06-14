package br.edu.idp.es.stsw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementCoverageTest {

    private final DiscountCalculator calc = new DiscountCalculator();

    @Test
    void deveExecutarTodasAsInstrucoesPrincipais() {
        assertEquals(40, calc.calculateDiscount(true, 350, true, false));
    }

    @Test
    void devePularTodasAsRegrasParaCompraAbaixoDoMinimo() {
        // Nenhuma regra ativada -> desconto = 0
        assertEquals(0, calc.calculateDiscount(false, 50, false, false));
    }
}
