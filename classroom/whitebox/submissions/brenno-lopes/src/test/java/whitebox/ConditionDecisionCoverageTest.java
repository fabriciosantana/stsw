package whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Condition/Decision Coverage (Cobertura de condicao/decisao)
 *
 * Objetivo: combinar os dois criterios anteriores — cada decisao deve
 * assumir true e false, e cada condicao atomica tambem deve assumir
 * true e false.
 *
 * Decisoes e condicoes atomicas:
 *   D1: purchaseAmount >= 100
 *   D2: premiumCustomer
 *   D3: couponValid (C1) && purchaseAmount >= 200 (C2)
 *   D4: blackFriday (C3) || (premiumCustomer (C4) && purchaseAmount >= 300 (C5))
 *   D5: discount > 40
 */
@DisplayName("Condition/Decision Coverage")
class ConditionDecisionCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    @DisplayName("Todas as decisoes true, todas as condicoes true — teto atingido")
    void allTrue() {
        // premium=true, valor=300, cupom=true, blackFriday=true
        // D1=T, D2=T, D3=T(C1=T,C2=T), D4=T(C3=T,C4=T,C5=T), D5=T → 40
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }

    @Test
    @DisplayName("Todas as decisoes false, todas as condicoes false")
    void allFalse() {
        // premium=false, valor=50, cupom=false, blackFriday=false
        // D1=F, D2=F, D3=F(C1=F,C2=F), D4=F(C3=F,C4=F,C5=F), D5=F → 0
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }

    @Test
    @DisplayName("D3 false com C1=true e C2=false — cupom valido mas valor baixo")
    void couponValidLowAmount() {
        // premium=false, valor=150, cupom=true, blackFriday=false
        // D1=T, D2=F, D3=F(C1=T,C2=F), D4=F → 10
        int result = calculator.calculateDiscount(false, 150, true, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("D3 false com C1=false e C2=true — cupom invalido mas valor alto")
    void couponInvalidHighAmount() {
        // premium=false, valor=250, cupom=false, blackFriday=false
        // D1=T, D2=F, D3=F(C1=F,C2=T), D4=F → 10
        int result = calculator.calculateDiscount(false, 250, false, false);
        assertEquals(10, result);
    }

    @Test
    @DisplayName("D4 true apenas via segundo operando — premium + valor >= 300 sem Black Friday")
    void premiumHighAmountNoBlackFriday() {
        // premium=true, valor=300, cupom=false, blackFriday=false
        // D1=T(+10), D2=T(+5), D3=F, D4=T(C3=F,C4=T,C5=T)(+20) → 35
        int result = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, result);
    }

    @Test
    @DisplayName("D4 false com C4=true e C5=false — premium mas valor < 300")
    void premiumLowAmountNoBlackFriday() {
        // premium=true, valor=150, cupom=false, blackFriday=false
        // D1=T(+10), D2=T(+5), D3=F, D4=F(C3=F,C4=T,C5=F) → 15
        int result = calculator.calculateDiscount(true, 150, false, false);
        assertEquals(15, result);
    }
}
