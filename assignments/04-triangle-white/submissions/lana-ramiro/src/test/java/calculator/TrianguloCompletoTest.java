package TrianguloCompleto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class TrianguloCompletoTest {

    // ---- TESTES DE LADOS INVÁLIDOS ----
    @Test
    void ladosInvalidosZeroOuNegativos() {
        assertEquals("Lados invalidos", TrianguloCompleto.classificarTriangulo(0, 5, 5));
        assertEquals("Lados invalidos", TrianguloCompleto.classificarTriangulo(-1, 5, 5));
    }

    @Test
    void ladosInvalidosMaioresQue200() {
        assertEquals("Lados invalidos", TrianguloCompleto.classificarTriangulo(201, 100, 100));
        assertEquals("Lados invalidos", TrianguloCompleto.classificarTriangulo(100, 201, 100));
        assertEquals("Lados invalidos", TrianguloCompleto.classificarTriangulo(100, 100, 201));
    }

    // ---- TESTES DE NÃO FORMAÇÃO DE TRIÂNGULO ----
    @Test
    void naoFormaTrianguloPorDesigualdade() {
        assertEquals("Nao e um triangulo", TrianguloCompleto.classificarTriangulo(1, 2, 3));
        assertEquals("Nao e um triangulo", TrianguloCompleto.classificarTriangulo(5, 1, 2));
        assertEquals("Nao e um triangulo", TrianguloCompleto.classificarTriangulo(10, 5, 5));
    }

    // ---- TESTES DE TRIÂNGULOS VÁLIDOS ----
    @Test
    void trianguloEquilatero() {
        assertEquals("Equilatero", TrianguloCompleto.classificarTriangulo(5, 5, 5));
    }

    @Test
    void trianguloIsosceles() {
        assertEquals("Isosceles", TrianguloCompleto.classificarTriangulo(5, 5, 3));
        assertEquals("Isosceles", TrianguloCompleto.classificarTriangulo(5, 3, 5));
        assertEquals("Isosceles", TrianguloCompleto.classificarTriangulo(3, 5, 5));
    }

    @Test
    void trianguloEscaleno() {
        assertEquals("Escaleno", TrianguloCompleto.classificarTriangulo(4, 5, 6));
    }
}
