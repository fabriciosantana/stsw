import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Path Coverage (cobertura de caminhos).
 * 
 * Objetivo: Executar caminhos representativos pelo método.
 * 
 * Atenção: Para este exercício, não é necessário enumerar TODOS os caminhos
 * possíveis. O objetivo é mostrar que o número de caminhos cresce rapidamente
 * e que a cobrança por cobertura total pode ficar inviável.
 * 
 * Sugestão de caminhos interessantes:
 * - compra sem nenhum desconto
 * - compra com desconto apenas por valor mínimo
 * - compra com desconto por cliente premium
 * - compra com cupom válido
 * - compra com Black Friday
 * - compra com premium + valor alto
 * - compra que atinge o teto máximo de desconto
 */
public class PathCoverageTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    // ===== Caminho 1: Compra sem nenhum desconto =====
    @Test
    void testPathNoDiscount() {
        // Todas as condições são falsas
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

    // ===== Caminho 2: Desconto apenas por valor mínimo (regra 1) =====
    @Test
    void testPathOnlyMinimumPurchase() {
        // purchaseAmount >= 100, mas sem nenhuma outra condição
        int discount = calculator.calculateDiscount(false, 100, false, false);
        assertEquals(10, discount);
    }

    // ===== Caminho 3: Desconto apenas por cliente premium (regra 2) =====
    @Test
    void testPathOnlyPremium() {
        // premiumCustomer = true, mas purchaseAmount < 100
        int discount = calculator.calculateDiscount(true, 50, false, false);
        assertEquals(5, discount);
    }

    // ===== Caminho 4: Desconto por cliente premium com valor >= 100 =====
    @Test
    void testPathPremiumWithMinimumPurchase() {
        // Combina regra 1 e regra 2
        int discount = calculator.calculateDiscount(true, 100, false, false);
        assertEquals(15, discount); // 10 + 5
    }

    // ===== Caminho 5: Desconto apenas por cupom válido (regra 3) =====
    @Test
    void testPathOnlyValidCoupon() {
        // couponValid = true E purchaseAmount >= 200
        int discount = calculator.calculateDiscount(false, 200, true, false);
        assertEquals(25, discount); // 10 + 15
    }

    // ===== Caminho 6: Desconto apenas por Black Friday (regra 4 via blackFriday) =====
    @Test
    void testPathOnlyBlackFriday() {
        // blackFriday = true, mas sem nada mais
        int discount = calculator.calculateDiscount(false, 50, false, true);
        assertEquals(20, discount);
    }

    // ===== Caminho 7: Desconto por premium + valor alto (regra 4 via premiumCustomer) =====
    @Test
    void testPathPremiumWithHighPurchase() {
        // premiumCustomer = true E purchaseAmount >= 300
        int discount = calculator.calculateDiscount(true, 300, false, false);
        assertEquals(35, discount); // 10 + 5 + 20
    }

    // ===== Caminho 8: Black Friday + cliente premium =====
    @Test
    void testPathBlackFridayWithPremium() {
        // Ambas as condições de regra 4 são verdadeiras (redundância)
        int discount = calculator.calculateDiscount(true, 250, false, true);
        assertEquals(40, discount); // 10 + 5 + 20 = 35, se houver upgrade
        // Verificar se há limite de 40
    }

    // ===== Caminho 9: Tudo verdadeiro (sem limite) =====
    @Test
    void testPathAllConditionsTrue() {
        // Todas as condições são verdadeiras
        int discount = calculator.calculateDiscount(true, 500, true, true);
        assertEquals(40, discount); // 10 + 5 + 15 + 20 = 50, mas capped em 40
    }

    // ===== Caminho 10: Premium + Cupom + valor alto =====
    @Test
    void testPathPremiumWithCouponHighPurchase() {
        // regra 1, 2, 3, e possivelmente 4
        int discount = calculator.calculateDiscount(true, 500, true, false);
        assertEquals(40, discount); // 10 + 5 + 15 + potencial overflow
    }

    // ===== Caminho 11: Desconto que atinge o teto (máximo 40) =====
    @Test
    void testPathMaximumDiscount() {
        // Cenário que maximiza o desconto
        int discount = calculator.calculateDiscount(true, 1000, true, true);
        assertEquals(40, discount);
    }

    // ===== Caminho 12: Desconto perto do limite, mas sem ultrapassar =====
    @Test
    void testPathNearMaximumDiscount() {
        // Soma exata em 40 ou logo abaixo
        int discount = calculator.calculateDiscount(true, 150, true, false);
        // 10 + 5 + 15 = 30 (não ultrapassa)
        assertEquals(30, discount);
    }

    // ===== Caminho 13: Cliente premium em compra baixa com cupom =====
    @Test
    void testPathPremiumLowPurchaseWithCoupon() {
        // premiumCustomer = true E purchaseAmount < 200 (cupom não se aplica)
        int discount = calculator.calculateDiscount(true, 150, true, false);
        assertEquals(15, discount); // 10 + 5 (cupom não aplica, pois amount < 200)
    }

    // ===== Caminho 14: Compra alta sem premium e sem cupom =====
    @Test
    void testPathHighPurchaseWithoutPremiumOrCoupon() {
        // Apenas regra 1 se aplica
        int discount = calculator.calculateDiscount(false, 500, false, false);
        assertEquals(10, discount);
    }

}
