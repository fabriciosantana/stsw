package com.example.triangle;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testes white-box cobrindo todos os ramos e limites da função classificarTriangulo.
 */
public class TrianguloWhiteBoxTest {

    // ---- 1. Lados inválidos (<=0 ou >200) ----
    @Test public void ladosInvalidos_zeroENegativo() {
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(0, 5, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(-1, 5, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 0, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 5, -3));
    }
    @Test public void ladosInvalidos_acimaDoLimite() {
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(201, 5, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 201, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 5, 201));
    }

    // ---- 2. Não é triângulo (desigualdade) ----
    @Test public void naoTriangulo_somaIgualMaior() {
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(1, 2, 3));
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(3, 1, 2)); // permutação
    }
    @Test public void naoTriangulo_somaMenorMaior() {
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(1, 2, 4));
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(10, 1, 1));
    }

    // ---- 3. Equilátero ----
    @Test public void equilatero_basico() {
        assertEquals("Equilátero", TrianguloApp.classificarTriangulo(5, 5, 5));
    }
    @Test public void equilatero_limiteSuperior() {
        assertEquals("Equilátero", TrianguloApp.classificarTriangulo(200, 200, 200));
    }

    // ---- 4. Isósceles ----
    @Test public void isosceles_permutacoes() {
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(5, 5, 3));
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(5, 3, 5));
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(3, 5, 5));
    }
    @Test public void isosceles_boundaryValido() {
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(5, 5, 9)); // soma 5+5>9
    }
    @Test public void isosceles_boundaryInvalido() {
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(5, 5, 10)); // soma == maior
    }

    // ---- 5. Escaleno ----
    @Test public void escaleno_basicoPermutacoes() {
        assertEquals("Escaleno", TrianguloApp.classificarTriangulo(3, 4, 5));
        assertEquals("Escaleno", TrianguloApp.classificarTriangulo(4, 5, 3));
        assertEquals("Escaleno", TrianguloApp.classificarTriangulo(5, 3, 4));
    }

    // ---- 6. Comutatividade (mesmo resultado independente da ordem) ----
    @Test public void comutatividade() {
        String base = TrianguloApp.classificarTriangulo(7, 10, 5);
        assertEquals(base, TrianguloApp.classificarTriangulo(5, 7, 10));
        assertEquals(base, TrianguloApp.classificarTriangulo(10, 5, 7));
    }
}
