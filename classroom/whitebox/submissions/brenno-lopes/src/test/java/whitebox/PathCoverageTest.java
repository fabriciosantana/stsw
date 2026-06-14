package whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Path Coverage (Cobertura de caminhos)
 *
 * Objetivo: exercitar caminhos representativos pelo metodo.
 *
 * Com 5 decisoes independentes, existem ate 2^5 = 32 caminhos possiveis.
 * Aqui selecionamos caminhos representativos que demonstram como o numero
 * de combinacoes cresce rapidamente, tornando a cobertura total de
 * caminhos inviavel em cenarios reais.
 */
@DisplayName("Path Coverage")
class PathCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    @DisplayName("Caminho 1: nenhum desconto aplicado")
    void noDiscount() {
        // premium=false, valor=50, cupom=false, blackFriday=false
        // Caminho: D1-F → D2-F → D3-F → D4-F → D5-F → return 0
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("Caminho 2: desconto apenas por valor minimo (>= 100)")
    void onlyMinimumPurchase() {
        // premium=false, valor=100, cupom=false, blackFriday=false
        // Caminho: D1-T(+10) → D2-F → D3-F → D4-F → D5-F → return 10
        int result = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("Caminho 3: desconto apenas por cliente premium")
    void onlyPremium() {
        // premium=true, valor=50, cupom=false, blackFriday=false
        // Caminho: D1-F → D2-T(+5) → D3-F → D4-F → D5-F → return 5
        int result = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("Caminho 4: desconto por cupom valido com valor >= 200")
    void couponDiscount() {
        // premium=false, valor=200, cupom=true, blackFriday=false
        // Caminho: D1-T(+10) → D2-F → D3-T(+15) → D4-F → D5-F → return 25
        int result = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, result);
    }

    @Test
    @DisplayName("Caminho 5: desconto por Black Friday isolado")
    void blackFridayOnly() {
        // premium=false, valor=50, cupom=false, blackFriday=true
        // Caminho: D1-F → D2-F → D3-F → D4-T(+20) → D5-F → return 20
        int result = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, result);
    }

    @Test
    @DisplayName("Caminho 6: premium + valor >= 300 (sem Black Friday)")
    void premiumHighAmount() {
        // premium=true, valor=300, cupom=false, blackFriday=false
        // Caminho: D1-T(+10) → D2-T(+5) → D3-F → D4-T(+20) → D5-F → return 35
        int result = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, result);
    }

    @Test
    @DisplayName("Caminho 7: todos os descontos — atinge o teto de 40")
    void allDiscountsMaxCap() {
        // premium=true, valor=300, cupom=true, blackFriday=true
        // Caminho: D1-T(+10) → D2-T(+5) → D3-T(+15) → D4-T(+20) = 50 → D5-T → return 40
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }

    @Test
    @DisplayName("Caminho 8: premium + valor minimo sem atingir teto")
    void premiumWithMinimumPurchase() {
        // premium=true, valor=100, cupom=false, blackFriday=false
        // Caminho: D1-T(+10) → D2-T(+5) → D3-F → D4-F → D5-F → return 15
        int result = calculator.calculateDiscount(true, 100, false, false);
        assertEquals(15, result);
    }

    @Test
    @DisplayName("Caminho 9: Black Friday + valor alto + cupom (sem premium)")
    void blackFridayCouponHighAmount() {
        // premium=false, valor=250, cupom=true, blackFriday=true
        // Caminho: D1-T(+10) → D2-F → D3-T(+15) → D4-T(+20) = 45 → D5-T → return 40
        int result = calculator.calculateDiscount(false, 250, true, true);
        assertEquals(40, result);
    }

    @Test
    @DisplayName("Caminho 10: desconto exatamente no teto (40) sem ultrapassar")
    void exactlyCap() {
        // premium=true, valor=200, cupom=true, blackFriday=false
        // Caminho: D1-T(+10) → D2-T(+5) → D3-T(+15) → D4-F = 30 → D5-F → return 30
        int result = calculator.calculateDiscount(true, 200, true, false);
        assertEquals(30, result);
    }
}
