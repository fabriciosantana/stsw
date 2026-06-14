import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Condition Coverage (cobertura de condições).
 * 
 * Objetivo: Cada condição atômica (sub-expressão) deve assumir true e false,
 * independentemente do resultado da decisão composta.
 * 
 * Condições atômicas identificadas:
 * - purchaseAmount >= 100
 * - premiumCustomer
 * - couponValid
 * - purchaseAmount >= 200
 * - blackFriday
 * - purchaseAmount >= 300
 * - discount > 40
 * 
 * Importante: Cobrir condições não significa necessariamente cobrir todas
 * as combinações possíveis.
 */
public class ConditionCoverageTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // ===== Condição: purchaseAmount >= 100 =====

    @Test
    void testConditionPurchaseAmount100_True() {
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertTrue(discount >= 10); // Pelo menos a regra 1 foi aplicada
        assertEquals(10, discount);
    }

    @Test
    void testConditionPurchaseAmount100_False() {
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

    // ===== Condição: premiumCustomer =====

    @Test
    void testConditionPremiumCustomer_True() {
        int discount = calculator.calculateDiscount(true, 150, false, false);
        assertTrue(discount >= 5); // Premium foi aplicado
        assertEquals(15, discount); // 10 + 5
    }

    @Test
    void testConditionPremiumCustomer_False() {
        int discount = calculator.calculateDiscount(false, 150, false, false);
        assertEquals(10, discount); // Apenas regra 1
    }

    // ===== Condição: couponValid =====

    @Test
    void testConditionCouponValid_True() {
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertTrue(discount >= 15); // Cupom foi aplicado
        assertEquals(25, discount); // 10 + 15
    }

    @Test
    void testConditionCouponValid_False() {
        int discount = calculator.calculateDiscount(false, 200, false, false);
        assertEquals(10, discount); // Cupom não aplicado
    }

    // ===== Condição: purchaseAmount >= 200 =====

    @Test
    void testConditionPurchaseAmount200_True() {
        // Quando coupon é true e amount >= 200, deve aplicar cupom
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount); // 10 + 15
    }

    @Test
    void testConditionPurchaseAmount200_False() {
        // Quando amount < 200, cupom não se aplica mesmo que válido
        int discount = calculator.calculateDiscount(false, 199, true, false);
        assertEquals(10, discount);
    }

    // ===== Condição: blackFriday =====

    @Test
    void testConditionBlackFriday_True() {
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount); // Apenas Black Friday
    }

    @Test
    void testConditionBlackFriday_False() {
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

    // ===== Condição: purchaseAmount >= 300 (na regra 4) =====

    @Test
    void testConditionPurchaseAmount300_True() {
        // Premium = true E amount >= 300 (sem Black Friday)
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount); // 10 + 5 + 20
    }

    @Test
    void testConditionPurchaseAmount300_False() {
        // Premium = true E amount < 300 (sem Black Friday)
        int discount = calculator.calculateDiscount(true, 299, false, false);
        assertEquals(15, discount); // 10 + 5
    }

    // ===== Condição: discount > 40 =====

    @Test
    void testConditionMaxDiscount_True() {
        // Configurar para exceder 40
        int discount = calculator.calculateDiscount(true, 500, true, true);
        assertEquals(40, discount); // Limitado a 40
    }

    @Test
    void testConditionMaxDiscount_False() {
        // Desconto que não ultrapassa 40
        int discount = calculator.calculateDiscount(true, 100, false, false);
        assertEquals(15, discount); // 10 + 5 (não ultrapassa 40)
    }

}
