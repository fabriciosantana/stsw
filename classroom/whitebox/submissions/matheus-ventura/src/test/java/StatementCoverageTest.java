import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para Statement Coverage (cobertura de instruções).
 * 
 * Objetivo: Executar as principais instruções/linhas do método com um
 * conjunto mínimo de testes.
 * 
 * Pergunta para orientar:
 * - Um único teste consegue executar várias linhas e ainda assim deixar
 *   decisões importantes mal exploradas?
 */
public class StatementCoverageTest {

    private DiscountCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DiscountCalculator();
    }

    /**
     * Teste 1: Compra com valor que ativa regra 1 e 3
     * Executa as instrções de desconto por valor >= 100 e cupom válido com valor >= 200
     */
    @Test
    void testStatementCoverageHighPurchaseWithCoupon() {
        int discount = calculator.calculateDiscount(false, 250, true, false);
        assertEquals(25, discount); // 10 (regra 1) + 15 (regra 3)
    }

    /**
     * Teste 2: Cliente premium na Black Friday
     * Executa as instruções de desconto premium, Black Friday e limite máximo
     */
    @Test
    void testStatementCoveragePremiumOnBlackFriday() {
        int discount = calculator.calculateDiscount(true, 150, false, true);
        assertEquals(35, discount); // 10 + 5 + 20 (regra 4)
    }

    /**
     * Teste 3: Estado que potencialmente atinge o limite máximo de 40
     */
    @Test
    void testStatementCoverageMaxDiscount() {
        int discount = calculator.calculateDiscount(true, 500, true, true);
        assertEquals(40, discount); // 10 + 5 + 15 + 20 = 50, mas máximo é 40
    }

    /**
     * Teste 4: Compra sem desconto (abaixo de 100)
     * Verifica que o método retorna 0 quando nenhuma condição é atendida
     */
    @Test
    void testStatementCoverageNoDiscount() {
        int discount = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount);
    }

}
