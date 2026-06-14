package whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Statement Coverage (Cobertura de instrucoes)
 *
 * Objetivo: executar todas as instrucoes (linhas) do metodo com o menor
 * numero possivel de casos de teste.
 *
 * Um unico teste que ativa todas as condicoes ja percorre todas as linhas,
 * mas adicionamos um segundo teste para cobrir o caminho em que nenhum
 * desconto e aplicado e o teto nao e atingido.
 */
@DisplayName("Statement Coverage")
class StatementCoverageTest {

    private final DiscountCalculator calculator = new DiscountCalculator();

    @Test
    @DisplayName("Todos os descontos ativos — cobre todas as instrucoes e o teto")
    void allDiscountsActive() {
        // premium=true, valor=300, cupom=true, blackFriday=true
        // Regra 1: +10, Regra 2: +5, Regra 3: +15, Regra 4: +20 = 50 → teto 40
        int result = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, result);
    }

    @Test
    @DisplayName("Nenhum desconto — cobre os ramos false de cada if")
    void noDiscountApplied() {
        // premium=false, valor=50, cupom=false, blackFriday=false
        // Nenhuma regra ativada → 0
        int result = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, result);
    }
}
