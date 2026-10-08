package br.edu.idp.es.stsw.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Statement Coverage: cada instrucao executa ao menos uma vez.
 * Conceito de "White box/Statement Coverage.md": um unico caso
 * executa todas as instrucoes, mas isso e insuficiente para
 * validar decisoes.
 */
class StatementCoverageTest {

    @Test
    @DisplayName("Nenhum desconto: todas as instrucoes executadas com decisoes falsas")
    void todasInstrucoesComDecisoesFalsas() {
        assertEquals(0, DiscountCalculator.calculateDiscount(false, 50, false, false));
    }

    @Test
    @DisplayName("Desconto maximo: todas as instrucoes executadas com decisoes verdadeiras")
    void todasInstrucoesComDecisoesVerdadeiras() {
        assertEquals(40, DiscountCalculator.calculateDiscount(true, 500, true, true));
    }
}
