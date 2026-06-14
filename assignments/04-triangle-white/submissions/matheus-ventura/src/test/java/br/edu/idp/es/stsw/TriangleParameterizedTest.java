package br.edu.idp.es.stsw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TriangleParameterizedTest {

    @ParameterizedTest
    @CsvSource({
        "3,3,3,Equilateral",
        "3,3,2,Isosceles",
        "4,5,6,Scalene",
        "1,2,3,Invalid",
        "0,2,2,Invalid",
        "-1,2,2,Invalid"
    })
    void testMultipleCases(int a, int b, int c, String expected) {
        assertEquals(expected, Triangle.classify(a, b, c));
    }
}