import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TrianguloTest {

    @ParameterizedTest
    @CsvSource({
        // Invalid borders
        "0, 10, 10, Lados inválidos",
        "201, 10, 10, Lados inválidos",
        "10, 0, 10, Lados inválidos",
        "10, 201, 10, Lados inválidos",
        "10, 10, 0, Lados inválidos",
        "10, 10, 201, Lados inválidos",
        
        // Boundaries valid testing
        "1, 10, 10, Isósceles",
        "200, 10, 10, Não é um triângulo",
        "10, 200, 10, Não é um triângulo",
        "10, 10, 200, Não é um triângulo",

        // Not a triangle
        "10, 10, 20, Não é um triângulo",
        "10, 20, 10, Não é um triângulo",
        "20, 10, 10, Não é um triângulo",
        "3, 4, 8, Não é um triângulo",

        // Equilateral
        "10, 10, 10, Equilátero",

        // Isosceles
        "10, 10, 15, Isósceles",
        "10, 15, 10, Isósceles",
        "15, 10, 10, Isósceles",

        // Scalene
        "3, 4, 5, Escaleno",
        "10, 12, 14, Escaleno"
    })
    public void testTriangulo(int a, int b, int c, String expected) {
        assertEquals(expected, Triangulo.classificar(a, b, c));
    }
}
