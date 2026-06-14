package whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Decision Coverage (Cobertura de decisoes)
 *
 * Objetivo: fazer cada decisao (if) do metodo assumir true e false
 * pelo menos uma vez.
 *
 * Decisoes do metodo:
 *   D1: purchaseAmount >= 100
 *   D2: premiumCustomer
 *   D3: couponValid && purchaseAmount >= 200
 *   D4: blackFriday || (premiumCustomer && purchaseAmount >= 300)
 *   D5: discount > 40
 */
@DisplayName("Decision Coverage")
class DecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    @DisplayName("D1 true, D2 false, D3 false, D4 false, D5 false — desconto apenas por valor")
    void onlyPurchaseAmountDiscount() {
        // valor=150, premium=false, cupom=false, blackFriday=false
        // D1=true(+10), D2=false, D3=false, D4=false → 10
        int result = calculator.calculateDiscount(false, 150, false, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("D1 false, D2 true, D3 false, D4 false, D5 false — desconto apenas por premium")
    void onlyPremiumDiscount() {
        // valor=50, premium=true, cupom=false, blackFriday=false
        // D1=false, D2=true(+5), D3=false(valor<200), D4=false(valor<300) → 5
        int result = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, result);
    }

    @Test
    @DisplayName("D3 true — cupom valido com valor >= 200")
    void couponWithHighValue() {
        // valor=200, premium=false, cupom=true, blackFriday=false
        // D1=true(+10), D2=false, D3=true(+15), D4=false → 25
        int result = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, result);
    }

    @Test
    @DisplayName("D4 true via Black Friday")
    void blackFridayDiscount() {
        // valor=50, premium=false, cupom=false, blackFriday=true
        // D1=false, D2=false, D3=false, D4=true(+20) → 20
        int result = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, result);
    }

    @Test
    @DisplayName("D5 true — desconto ultrapassa o teto de 40")
    void discountExceedsCap() {
        // valor=300, premium=true, cupom=true, blackFriday=true
        // D1=true(+10), D2=true(+5), D3=true(+15), D4=true(+20) = 50 → teto 40
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }
}
