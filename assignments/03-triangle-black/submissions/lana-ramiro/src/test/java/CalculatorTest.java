package lana.ramiro;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    // Teste simples
    @LoginTest
    void simpleAdditionTest() {
        assertEquals(2, calculator.add(1, 1));
    }

    // Teste parametrizado de soma
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

    // Testando soma com zero
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 10, 100})
    void additionWithZeroTest(int a) {
        assertEquals(a, calculator.add(a, 0));
    }

    // Subtração
    @LoginTest
    void subtractionTest() {
        assertEquals(5, calculator.subtract(10, 5));
    }

    // Multiplicação
    @LoginTest
    void multiplicationTest() {
        assertEquals(25, calculator.multiply(5, 5));
    }

    // Divisão normal
    @LoginTest
    void divisionTest() {
        assertEquals(2, calculator.divide(10, 5));
    }

    // Divisão por zero (teste de exceção)
    @LoginTest
    void divisionByZeroTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculator.divide(10, 0);
        });
        assertEquals("Divisão por zero não é permitida", exception.getMessage());
    }
}
