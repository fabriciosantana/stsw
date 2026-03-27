package com.example.whitebox;
import org.junit.jupiter.api.Test;

import com.example.whitebox.DiscountCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.Transient;

public class StatementCoverageTest {

    private final DiscountCalculator discountCalculator = new DiscountCalculator();

    @Test
    void shouldExecuteMainStatements() {
        int discount = discountCalculator.calculateDiscount(true, 300, true, false);
        assertEquals(40, discount);
    }
    
}
