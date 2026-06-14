package com.classroom.whitebox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ConditionCoverageTest {

    @Test
    void testConditionCoverage() {
        DiscountCalculator calc = new DiscountCalculator();

        // Foca na Regra 3: (couponValid && purchaseAmount >= 200)
        // Caso A: couponValid=T, purchaseAmount=150 (F) -> Condição composta: F
        calc.calculateDiscount(false, 150, true, false);

        // Caso B: couponValid=F, purchaseAmount=250 (T) -> Condição composta: F
        calc.calculateDiscount(false, 250, false, false);

        // Foca na Regra 4: (blackFriday || (premiumCustomer && purchaseAmount >= 300))
        // Testando as partes da regra composta:
        // Caso C: blackFriday=F, premium=T, amount=250 (F)
        calc.calculateDiscount(true, 250, false, false);
        
        // Caso D: blackFriday=T, premium=F, amount=100 (F)
        calc.calculateDiscount(false, 100, false, true);
    }
}