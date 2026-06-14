package triangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes de Classificação do Triângulo")
class TriangleTest {

    private Triangle triangle;

    @BeforeEach
    void setUp() {
        triangle = new Triangle();
    }

    @Test
    @DisplayName("Deve classificar como Equilátero quando todos os lados são iguais")
    void shouldClassifyAsEquilateral() {
        assertEquals("Equilátero", triangle.classify(5, 5, 5));
    }

    @Test
    @DisplayName("Deve classificar como Equilátero com lados iguais a 1 (mínimo)")
    void shouldClassifyAsEquilateralWithMinimumSides() {
        assertEquals("Equilátero", triangle.classify(1, 1, 1));
    }

    @Test
    @DisplayName("Deve classificar como Equilátero com lados iguais a 200 (máximo)")
    void shouldClassifyAsEquilateralWithMaximumSides() {
        assertEquals("Equilátero", triangle.classify(200, 200, 200));
    }

    @Test
    @DisplayName("Deve classificar como Isósceles quando a == b")
    void shouldClassifyAsIsoscelesWhenAEqualsB() {
        assertEquals("Isósceles", triangle.classify(5, 5, 3));
    }

    @Test
    @DisplayName("Deve classificar como Isósceles quando a == c")
    void shouldClassifyAsIsoscelesWhenAEqualsC() {
        assertEquals("Isósceles", triangle.classify(5, 3, 5));
    }

    @Test
    @DisplayName("Deve classificar como Isósceles quando b == c")
    void shouldClassifyAsIsoscelesWhenBEqualsC() {
        assertEquals("Isósceles", triangle.classify(3, 5, 5));
    }

    @Test
    @DisplayName("Deve classificar como Escaleno quando todos os lados são diferentes")
    void shouldClassifyAsScalene() {
        assertEquals("Escaleno", triangle.classify(3, 4, 5));
    }

    @ParameterizedTest(name = "Escaleno: a={0}, b={1}, c={2}")
    @CsvSource({"3, 4, 5", "5, 7, 9", "10, 11, 12", "100, 150, 200"})
    @DisplayName("Deve classificar como Escaleno para múltiplas combinações")
    void shouldClassifyAsScaleneParameterized(int a, int b, int c) {
        assertEquals("Escaleno", triangle.classify(a, b, c));
    }

    @Test
    @DisplayName("Deve retornar 'Não é um triângulo'")
    void shouldReturnNotATriangle() {
        assertEquals("Não é um triângulo", triangle.classify(1, 2, 3));
    }

    @ParameterizedTest(name = "Não triângulo: a={0}, b={1}, c={2}")
    @CsvSource({"1, 2, 3", "5, 1, 1", "10, 3, 3", "1, 10, 2"})
    @DisplayName("Deve rejeitar múltiplas combinações que não formam triângulo")
    void shouldRejectNonTriangles(int a, int b, int c) {
        assertEquals("Não é um triângulo", triangle.classify(a, b, c));
    }

    @Test
    @DisplayName("Deve retornar 'Lados inválidos' para lado zero")
    void shouldReturnInvalidForZeroSide() {
        assertEquals("Lados inválidos", triangle.classify(0, 5, 5));
    }

    @Test
    @DisplayName("Deve retornar 'Lados inválidos' para lado negativo")
    void shouldReturnInvalidForNegativeSide() {
        assertEquals("Lados inválidos", triangle.classify(-5, 0, 5));
    }

    @Test
    @DisplayName("Deve retornar 'Lados inválidos' para lado acima de 200")
    void shouldReturnInvalidForSideAboveMax() {
        assertEquals("Lados inválidos", triangle.classify(201, 5, 5));
    }

    @ParameterizedTest(name = "Inválido: a={0}, b={1}, c={2}")
    @CsvSource({"0, 5, 5", "-1, 5, 5", "5, 0, 5", "5, 5, 0", "201, 5, 5", "5, 201, 5", "5, 5, 201"})
    @DisplayName("Deve rejeitar múltiplas combinações de lados inválidos")
    void shouldRejectInvalidSides(int a, int b, int c) {
        assertEquals("Lados inválidos", triangle.classify(a, b, c));
    }
}