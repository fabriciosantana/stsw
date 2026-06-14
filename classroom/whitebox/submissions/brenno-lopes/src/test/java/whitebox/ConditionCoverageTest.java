package whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Condition Coverage (Cobertura de condicoes)
 *
 * Objetivo: cada condicao atomica dentro das decisoes compostas deve
 * assumir true e false pelo menos uma vez.
 *
 * Condicoes atomicas nas decisoes compostas:
 *   D3: couponValid (C1) && purchaseAmount >= 200 (C2)
 *   D4: blackFriday (C3) || (premiumCustomer (C4) && purchaseAmount >= 300 (C5))
 */
@DisplayName("Condition Coverage")
class ConditionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    @DisplayName("C1=true, C2=true — cupom valido e valor >= 200")
    void couponTrueAmountAbove200() {
        // valor=250, premium=false, cupom=true, blackFriday=false
        // D1(+10), D3(+15) → 25
        int result = calculator.calculateDiscount(false, 250, true, false);
        assertEquals(25, result);
    }

    @Test
    @DisplayName("C1=false, C2=false — cupom invalido e valor < 200")
    void couponFalseAmountBelow200() {
        // valor=150, premium=false, cupom=false, blackFriday=false
        // D1(+10) → 10
        int result = calculator.calculateDiscount(false, 150, false, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("C1=true, C2=false — cupom valido mas valor < 200")
    void couponTrueAmountBelow200() {
        // valor=150, premium=false, cupom=true, blackFriday=false
        // D1(+10) → 10
        int result = calculator.calculateDiscount(false, 150, true, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("C1=false, C2=true — cupom invalido mas valor >= 200")
    void couponFalseAmountAbove200() {
        // valor=250, premium=false, cupom=false, blackFriday=false
        // D1(+10) → 10
        int result = calculator.calculateDiscount(false, 250, false, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("C3=true, C4=true, C5=true — Black Friday + premium + valor >= 300")
    void blackFridayTruePremiumTrueAmountAbove300() {
        // valor=300, premium=true, cupom=false, blackFriday=true
        // D1(+10), D2(+5), D4(+20) → 35
        int result = calculator.calculateDiscount(true, 300, false, true);
        assertEquals(35, result);
    }

    @Test
    @DisplayName("C3=false, C4=false, C5=false — sem Black Friday, sem premium, valor < 300")
    void blackFridayFalsePremiumFalseAmountBelow300() {
        // valor=50, premium=false, cupom=false, blackFriday=false
        // Nenhum desconto → 0
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("C3=false, C4=true, C5=false — sem Black Friday, premium mas valor < 300")
    void premiumTrueAmountBelow300() {
        // valor=150, premium=true, cupom=false, blackFriday=false
        // D1(+10), D2(+5) → 15
        int result = calculator.calculateDiscount(true, 150, false, false);
        assertEquals(15, result);
    }

    @Test
    @DisplayName("C3=true, C4=false, C5=false — Black Friday sem premium")
    void blackFridayTruePremiumFalse() {
        // valor=50, premium=false, cupom=false, blackFriday=true
        // D4(+20) → 20
        int result = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, result);
    }
}
