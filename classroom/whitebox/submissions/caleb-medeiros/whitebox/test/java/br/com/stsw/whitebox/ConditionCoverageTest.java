package br.com.stsw.whitebox;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConditionCoverageTest {

    @Test
    void testConditionSet1() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Para a decisao composta 1: coupon=false, valor>=200=true (F && T = F)
        // Para a decisao composta 2: bf=true, (premium=false && valor>=300(250)=false) -> (T || (F && F) = T)
        int result = calculator.calculateDiscount(false, 250, false, true);
        // 10 + 20 = 30
        assertEquals(30, result);
    }

    @Test
    void testConditionSet2() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Para a decisao composta 1: coupon=true, valor>=200=true (T && T = T)
        // Para a decisao composta 2: bf=false, (premium=true && valor>=300=true) -> (F || (T && T) = T)
        int result = calculator.calculateDiscount(true, 350, true, false);
        // 10 + 5 + 15 + 20 = 50 -> 40
        assertEquals(40, result);
    }

    @Test
    void testConditionSet3() {
        DiscountCalculator calculator = new DiscountCalculator();
        // Cobre a condicao que faltou: coupon=true, valor>=200=false (T && F = F)
        int result = calculator.calculateDiscount(false, 150, true, false);
        // 10
        assertEquals(10, result);
    }
}
