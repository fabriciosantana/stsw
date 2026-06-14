package br.edu.idp.stsw.whitebox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Testes Whitebox - Classificador de Triângulos")
public class ClassificadorTrianguloTest {

    /**
     * ANÁLISE ESTRUTURAL - CAMINHOS IDENTIFICADOS:
     *
     * 1. VALIDAÇÃO DE ENTRADA (Boundary Analysis)
     *    - a < 1: deve retornar "Lados inválidos"
     *    - a > 200: deve retornar "Lados inválidos"
     *    - b < 1: deve retornar "Lados inválidos"
     *    - b > 200: deve retornar "Lados inválidos"
     *    - c < 1: deve retornar "Lados inválidos"
     *    - c > 200: deve retornar "Lados inválidos"
     *
     * 2. DESIGUALDADE TRIANGULAR
     *    - a + b <= c: deve retornar "Não é um triângulo"
     *    - a + c <= b: deve retornar "Não é um triângulo"
     *    - b + c <= a: deve retornar "Não é um triângulo"
     *
     * 3. CLASSIFICAÇÃO (ao passar em validações anteriores)
     *    - a == b && b == c: Equilátero
     *    - a == b (e não equilátero): Isósceles
     *    - a == c (e não equilátero): Isósceles
     *    - b == c (e não equilátero): Isósceles
     *    - todos diferentes: Escaleno
     */

    // ==================== TESTES DE VALIDAÇÃO DE ENTRADA ====================

    @ParameterizedTest(name = "Lado negativo ou zero: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "0, 3, 4, Lados inválidos",      // a = 0
            "-1, 3, 4, Lados inválidos",     // a < 0
            "3, 0, 4, Lados inválidos",      // b = 0
            "3, -1, 4, Lados inválidos",     // b < 0
            "3, 4, 0, Lados inválidos",      // c = 0
            "3, 4, -1, Lados inválidos",     // c < 0
    })
    @DisplayName("Valores menores ou iguais a zero devem ser inválidos")
    void testInvalidNegativeOrZero(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Lado maior que 200: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "201, 3, 4, Lados inválidos",    // a > 200
            "300, 100, 100, Lados inválidos", // a >> 200
            "3, 201, 4, Lados inválidos",    // b > 200
            "3, 4, 201, Lados inválidos",    // c > 200
    })
    @DisplayName("Valores maiores que 200 devem ser inválidos")
    void testInvalidGreaterThan200(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Limites válidos: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "1, 1, 1, Equilátero",      // Mínimo válido: todos = 1
            "200, 200, 200, Equilátero", // Máximo válido: todos = 200
            "2, 2, 3, Isósceles",       // a=b, c diferente, mínimo válido (2+2>3)
            "100, 100, 100, Equilátero", // Meio do intervalo
    })
    @DisplayName("Valores nos limites (1 e 200) devem ser válidos")
    void testValidBoundaries(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    // ==================== TESTES DE DESIGUALDADE TRIANGULAR ====================

    @ParameterizedTest(name = "Desigualdade a+b<=c: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "1, 2, 3, Não é um triângulo",    // a + b = c (exatamente)
            "1, 2, 4, Não é um triângulo",    // a + b < c
            "2, 3, 5, Não é um triângulo",    // a + b = c
            "2, 3, 6, Não é um triângulo",    // a + b < c
    })
    @DisplayName("Quando a+b<=c, não é triângulo válido")
    void testInvalidTriangleABLessThanC(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Desigualdade a+c<=b: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "1, 3, 2, Não é um triângulo",    // a + c = b
            "1, 4, 2, Não é um triângulo",    // a + c < b
            "2, 5, 3, Não é um triângulo",    // a + c = b
            "3, 6, 2, Não é um triângulo",    // a + c < b
    })
    @DisplayName("Quando a+c<=b, não é triângulo válido")
    void testInvalidTriangleACLessThanB(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Desigualdade b+c<=a: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "3, 1, 2, Não é um triângulo",    // b + c = a
            "4, 1, 2, Não é um triângulo",    // b + c < a
            "5, 2, 3, Não é um triângulo",    // b + c = a
            "6, 2, 3, Não é um triângulo",    // b + c < a
    })
    @DisplayName("Quando b+c<=a, não é triângulo válido")
    void testInvalidTriangleBCLessThanA(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    // ==================== TESTES DE CLASSIFICAÇÃO ====================

    @ParameterizedTest(name = "Equilátero: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "1, 1, 1, Equilátero",
            "5, 5, 5, Equilátero",
            "10, 10, 10, Equilátero",
            "99, 99, 99, Equilátero",
            "200, 200, 200, Equilátero",
    })
    @DisplayName("Triângulo com todos os lados iguais é Equilátero")
    void testEquilateralTriangle(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Isósceles (a==b): ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "5, 5, 7, Isósceles",
            "10, 10, 15, Isósceles",
            "3, 3, 4, Isósceles",
            "50, 50, 60, Isósceles",
    })
    @DisplayName("Triângulo com a==b (e c diferente) é Isósceles")
    void testIsoscelesAEqualsB(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Isósceles (a==c): ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "5, 7, 5, Isósceles",
            "10, 15, 10, Isósceles",
            "3, 4, 3, Isósceles",
            "50, 60, 50, Isósceles",
    })
    @DisplayName("Triângulo com a==c (e b diferente) é Isósceles")
    void testIsoscelesAEqualsC(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Isósceles (b==c): ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "7, 5, 5, Isósceles",
            "15, 10, 10, Isósceles",
            "4, 3, 3, Isósceles",
            "60, 50, 50, Isósceles",
    })
    @DisplayName("Triângulo com b==c (e a diferente) é Isósceles")
    void testIsoscelesBEqualsC(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    @ParameterizedTest(name = "Escaleno: ({0}, {1}, {2}) -> {3}")
    @CsvSource({
            "3, 4, 5, Escaleno",      // Triângulo retângulo clássico
            "5, 6, 7, Escaleno",
            "7, 8, 9, Escaleno",
            "10, 12, 15, Escaleno",
            "2, 3, 4, Escaleno",
            "13, 14, 15, Escaleno",
            "6, 8, 9, Escaleno",
    })
    @DisplayName("Triângulo com todos os lados diferentes é Escaleno")
    void testScaleneTriangle(int a, int b, int c, String esperado) {
        assertEquals(esperado, ClassificadorTriangulo.classificar(a, b, c));
    }

    // ==================== TESTES DE CASOS CRÍTICOS ====================

    @Test
    @DisplayName("Caso crítico: Múltiplos lados no limite mínimo")
    void testMultipleLowBoundaries() {
        assertEquals("Equilátero", ClassificadorTriangulo.classificar(1, 1, 1));
        assertEquals("Isósceles", ClassificadorTriangulo.classificar(2, 2, 3));
    }

    @Test
    @DisplayName("Caso crítico: Múltiplos lados no limite máximo")
    void testMultipleHighBoundaries() {
        assertEquals("Equilátero", ClassificadorTriangulo.classificar(200, 200, 200));
        assertEquals("Isósceles", ClassificadorTriangulo.classificar(200, 200, 100));
    }

    @Test
    @DisplayName("Caso crítico: Um lado inválido anula toda a validação")
    void testOneInvalidSideFailsAll() {
        assertEquals("Lados inválidos", ClassificadorTriangulo.classificar(300, 100, 100));
        assertEquals("Lados inválidos", ClassificadorTriangulo.classificar(100, 0, 100));
        assertEquals("Lados inválidos", ClassificadorTriangulo.classificar(100, 100, -50));
    }
}
