import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Decision Coverage (cobertura de decisões).
 * 
 * Objetivo: Cada decisão (if) do método deve assumir verdadeiro (true) e
 * falso (false) pelo menos uma vez.
 * 
 * Perguntas para orientar:
 * - O if (purchaseAmount >= 100) já foi verdadeiro e falso?
 * - O if (premiumCustomer) já foi verdadeiro e falso?
 * - As decisões compostas também tiveram seus dois desfechos?
 */
public class DecisionCoverageTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // ===== Regra 1: purchaseAmount >= 100 =====

    @Test
    void testDecisionPurchaseAmountGreaterOrEqualTo100_True() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    @Test
    void testDecisionPurchaseAmountGreaterOrEqualTo100_False() {
        int discount = calculator.calculateDiscount(false, 99, false, false);
        assertEquals(0, discount);
    }

    // ===== Regra 2: premiumCustomer =====

    @Test
    void testDecisionPremiumCustomer_True() {
        int discount = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, discount);
    }

    @Test
    void testDecisionPremiumCustomer_False() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    // ===== Regra 3: couponValid && purchaseAmount >= 200 =====

    @Test
    void testDecisionCouponAndAmount_True() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount); // 10 + 15
    }

    @Test
    void testDecisionCouponAndAmount_False_CouponInvalid() {
        int discount = calculator.calculateDiscount(false, 200, false, false);
        assertEquals(10, discount);
    }

    @Test
    void testDecisionCouponAndAmount_False_AmountLow() {
        int discount = calculator.calculateDiscount(false, 199, true, false);
        assertEquals(10, discount);
    }

    // ===== Regra 4: blackFriday || (premiumCustomer && purchaseAmount >= 300) =====

    @Test
    void testDecisionRule4_True_BlackFriday() {
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount);
    }

    @Test
    void testDecisionRule4_True_PremiumAndHighAmount() {
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount); // 10 + 5 + 20
    }

    @Test
    void testDecisionRule4_False() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    // ===== Regra 5: discount > 40 =====

    @Test
    void testDecisionMaxDiscount_True() {
        int discount = calculator.calculateDiscount(true, 500, true, true);
        assertEquals(40, discount); // Seria > 40, mas capped em 40
    }

    @Test
    void testDecisionMaxDiscount_False() {
        int discount = calculator.calculateDiscount(true, 100, true, false);
        // 10 + 5 + 15 = 30 (não ultrapassa 40)
        assertEquals(30, discount);
    }

}
