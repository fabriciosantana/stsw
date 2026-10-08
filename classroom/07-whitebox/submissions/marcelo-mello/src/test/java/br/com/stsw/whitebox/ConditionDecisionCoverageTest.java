package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionDecisionCoverageTest {

    @Test
    void shouldEvaluateAllConditionsAndDecisions() {
        DiscountCalculator calculator = new DiscountCalculator();
        
        // Teste 1: Tudo True -> Avalia todas as condicoes principais como True e o desconto final > 40
        int discount1 = calculator.calculateDiscount(true, 300, true, true);
        assertEquals(40, discount1);
        
        // Teste 2: Tudo False -> Avalia todas as condicoes principais como False e o desconto final > 40 como False
        int discount2 = calculator.calculateDiscount(false, 50, false, false);
        assertEquals(0, discount2);

        // Teste 3: Combinacoes para evitar curto-circuito e cobrir todas as ramificacoes geradas pelo && e ||
        // couponValid = true (RHS executado), purchaseAmount = 150 (<200)
        // blackFriday = false (RHS executado), premiumCustomer = true, purchaseAmount = 150 (<300)
        int discount3 = calculator.calculateDiscount(true, 150, true, false);
        assertEquals(15, discount3); // >=100 (10) + premium (5) = 15

        // Teste 4: Complementar para ramificacoes do OR
        // couponValid = false, purchaseAmount = 350
        // blackFriday = false (RHS executado), premiumCustomer = true, purchaseAmount = 350 (>=300)
        int discount4 = calculator.calculateDiscount(true, 350, false, false);
        assertEquals(35, discount4); // >=100 (10) + premium (5) + OR_branch (20) = 35
    }
}
