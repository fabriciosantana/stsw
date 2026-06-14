package br.edu.idp.es.stsw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TriangleTest {

    @Test
    void testEquilateral() {
        assertEquals("Equilateral", Triangle.classify(3, 3, 3));
    }

    @Test
    void testIsosceles() {
        assertEquals("Isosceles", Triangle.classify(3, 3, 2));
    }

    @Test
    void testScalene() {
        assertEquals("Scalene", Triangle.classify(3, 4, 5));
    }

    @Test
    void testInvalidNegative() {
        assertEquals("Invalid", Triangle.classify(-1, 2, 3));
    }

    @Test
    void testInvalidZero() {
        assertEquals("Invalid", Triangle.classify(0, 2, 3));
    }

    @Test
    void testInvalidInequality() {
        assertEquals("Invalid", Triangle.classify(1, 2, 3));
    }

    @Test
    void testBoundaryCase() {
    assertEquals("Invalid", Triangle.classify(2, 2, 4));
    }
}