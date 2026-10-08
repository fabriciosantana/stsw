package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PathCoverageTest {

    @Test
    void testVariousPaths() {
        DiscountCalculator calculator = new DiscountCalculator();

        // Caminhos interessantes sugeridos:

        // - compra sem nenhum desconto
        assertEquals(0, calculator.calculateDiscount(false, 50, false, false));

        // - compra com desconto apenas por valor minimo
        assertEquals(10, calculator.calculateDiscount(false, 150, false, false));

        // - compra com desconto por cliente premium (abaixo de 100)
        assertEquals(5, calculator.calculateDiscount(true, 50, false, false));

        // - compra com cupom valido (e atingindo o valor minimo do cupom de 200 mas nao de 300)
        // Aqui o valor 250 da desconto de >=100 (10) e cupom (15) = 25
        assertEquals(25, calculator.calculateDiscount(false, 250, true, false));

        // - compra com Black Friday (abaixo de 100)
        assertEquals(20, calculator.calculateDiscount(false, 50, false, true));

        // - compra com premium + valor alto (atinge 300 mas nao tem cupom nem black friday)
        // >=100 (10) + premium (5) + premium>=300 (20) = 35
        assertEquals(35, calculator.calculateDiscount(true, 350, false, false));

        // - compra que atinge o teto maximo de desconto
        // Tudo true, total = 10 + 5 + 15 + 20 = 50 -> teto de 40
        assertEquals(40, calculator.calculateDiscount(true, 300, true, true));
    }
}
