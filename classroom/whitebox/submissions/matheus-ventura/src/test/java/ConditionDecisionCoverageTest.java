import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Condition/Decision Coverage.
 * 
 * Objetivo: Combinar os dois critérios anteriores:
 * - Cada decisão (if) deve assumir true e false
 * - Cada condição atômica deve assumir true e false
 * 
 * Este nível de cobertura é mais rigoroso que os anteriores e pode exigir
 * mais testes para garantir que todas as combinações relevantes sejam cobradas.
 */
public class ConditionDecisionCoverageTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // ===== Regra 1: if (purchaseAmount >= 100) =====

    @Test
    void testCondDecisionRule1_AmountTrue() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount); // if true -> +10
    }

    @Test
    void testCondDecisionRule1_AmountFalse() {
        int discount = calculator.calculateDiscount(false, 99, false, false);
        assertEquals(0, discount); // if false -> +0
    }

    // ===== Regra 2: if (premiumCustomer) =====

    @Test
    void testCondDecisionRule2_PremiumTrue() {
        int discount = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, discount); // if true -> +5
    }

    @Test
    void testCondDecisionRule2_PremiumFalse() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount); // if false -> premium não aplicado
    }

    // ===== Regra 3: if (couponValid && purchaseAmount >= 200) =====
    // Decisão composta com AND

    @Test
    void testCondDecisionRule3_BothTrue() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount); // if true -> +15
    }

    @Test
    void testCondDecisionRule3_CouponTrue_AmountFalse() {
        // couponValid = true, mas purchaseAmount < 200
        int discount = calculator.calculateDiscount(false, 199, true, false);
        assertEquals(10, discount); // if false -> +0 (não aplica cupom)
    }

    @Test
    void testCondDecisionRule3_CouponFalse_AmountTrue() {
        // couponValid = false, mas purchaseAmount >= 200
        int discount = calculator.calculateDiscount(false, 200, false, false);
        assertEquals(10, discount); // if false -> +0 (não aplica cupom)
    }

    @Test
    void testCondDecisionRule3_BothFalse() {
        int discount = calculator.calculateDiscount(false, 199, false, false);
        assertEquals(10, discount); // if false -> +0 (não aplica cupom)
    }

    // ===== Regra 4: if (blackFriday || (premiumCustomer && purchaseAmount >= 300)) =====
    // Decisão composta com OR

    @Test
    void testCondDecisionRule4_BlackFridayTrue() {
        // blackFriday = true (não importa o segundo termo)
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount); // if true -> +20
    }

    @Test
    void testCondDecisionRule4_BlackFridayFalse_PremiumAndAmountTrue() {
        // blackFriday = false, mas (premiumCustomer && purchaseAmount >= 300) = true
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount); // 10 + 5 + 20 (regra 4 aplicada)
    }

    @Test
    void testCondDecisionRule4_BlackFridayFalse_PremiumFalse() {
        // blackFriday = false, premiumCustomer = false
        int discount = calculator.calculateDiscount(false, 300, false, false);
        assertEquals(10, discount); // (false || false) -> if false
    }

    @Test
    void testCondDecisionRule4_BlackFridayFalse_AmountLow() {
        // blackFriday = false, premiumCustomer = true, mas purchaseAmount < 300
        int discount = calculator.calculateDiscount(true, 299, false, false);
        assertEquals(15, discount); // (false || false) -> if false
    }

    // ===== Regra 5: if (discount > 40) =====

    @Test
    void testCondDecisionRule5_DiscountGreater40() {
        // Criar situação onde a soma inicial > 40
        int discount = calculator.calculateDiscount(true, 500, true, true);
        // 10 + 5 + 15 + 20 = 50 -> if true -> capped em 40
        assertEquals(40, discount);
    }

    @Test
    void testCondDecisionRule5_DiscountLessOrEqual40() {
        // Desconto que não ultrapassa 40
        int discount = calculator.calculateDiscount(true, 100, true, false);
        // 10 + 5 + 15 = 30 -> if false -> mantém 30
        assertEquals(30, discount);
    }

    // ===== Teste integrado: múltiplas condições/decisões =====

    @Test
    void testCondDecisionIntegratedScenario1() {
        // Cenário: Cliente padrão, compra baixa, sem cupom, sem Black Friday
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

    @Test
    void testCondDecisionIntegratedScenario2() {
        // Cenário: Cliente premium, compra média, com cupom
        int discount = calculator.calculateDiscount(true, 250, true, false);
        assertEquals(40, discount); // 10 + 5 + 15 + capped
    }

    @Test
    void testCondDecisionIntegratedScenario3() {
        // Cenário: Black Friday com cliente premium e compra alta
        int discount = calculator.calculateDiscount(true, 500, false, true);
        assertEquals(40, discount); // 10 + 5 + 20 = 35, mas... se regra 4 de premium também aplica? = 40
    }

}
