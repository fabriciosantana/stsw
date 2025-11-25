package br.edu.idp.stsw.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;


class TriangleTest {

    private final Triangle triangle = new Triangle();

    @LoginTest
    void simpleTriangleTest() {
        assertEquals("Escaleno", triangle.teste(3,4,5));
    }
}
/*
    @ParameterizedTest
    @CsvSource({
        "1,1,2",
        "2,2,4",
        "3,5,8",
        "10,10,20"
    })
    void parametrizedAdditionTest(int a, int b, int result) {
        assertEquals(result, calculator.add(a, b));
    }

    @ParameterizedTest
    @ValueSource( ints = {
        1,
        2,
        10,
        100
    })
    void parametrizedAdditionTest(int a) {
        assertEquals(a, calculator.add(a, 0));
    }

}
*/

