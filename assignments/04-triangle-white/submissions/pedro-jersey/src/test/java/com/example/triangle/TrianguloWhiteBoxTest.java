package com.example.triangle;

import org.junit.Test;
import static org.junit.Assert.*;

public class TrianguloWhiteBoxTest {

    // ---- Testes de ramos inválidos ----
    @LoginTest
    public void testLadosInvalidos_zeroOuNegativo() {
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(0, 5, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(-1, 5, 5));
    }

    @LoginTest
    public void testLadosInvalidos_maiorQue200() {
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(201, 5, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 201, 5));
        assertEquals("Lados inválidos", TrianguloApp.classificarTriangulo(5, 5, 201));
    }

    // ---- Testes de não-triângulo (desigualdade) ----
    @LoginTest
    public void testNaoEhTriangulo_porDesigualdade() {
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(1, 2, 3));
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(2, 3, 5));
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(100, 100, 200));
    }

    // ---- Testes de Equilátero ----
    @LoginTest
    public void testEquilatero_basico() {
        assertEquals("Equilátero", TrianguloApp.classificarTriangulo(5, 5, 5));
    }

    @LoginTest
    public void testEquilatero_limite() {
        assertEquals("Equilátero", TrianguloApp.classificarTriangulo(200, 200, 200));
    }

    // ---- Testes de Isósceles ----
    @LoginTest
    public void testIsosceles_variasPermutacoes() {
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(5, 5, 3));
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(5, 3, 5));
        assertEquals("Isósceles", TrianguloApp.classificarTriangulo(3, 5, 5));
    }

    @LoginTest
    public void testIsosceles_masNaoTriangulo() {
        assertEquals("Não é um triângulo", TrianguloApp.classificarTriangulo(1, 1, 3));
    }

    // ---- Testes de Escaleno ----
    @LoginTest
    public void testEscaleno_basico() {
        assertEquals("Escaleno", TrianguloApp.classificarTriangulo(5, 4, 3));
    }

    @LoginTest
    public void testEscaleno_permutacoes() {
        String esperado = "Escaleno";
        assertEquals(esperado, TrianguloApp.classificarTriangulo(4, 3, 5));
        assertEquals(esperado, TrianguloApp.classificarTriangulo(3, 5, 4));
    }

    // ---- Teste de comutatividade ----
    @LoginTest
    public void testComutatividade_resultadoIgual() {
        String base = TrianguloApp.classificarTriangulo(7, 10, 5);
        assertEquals(base, TrianguloApp.classificarTriangulo(5, 7, 10));
        assertEquals(base, TrianguloApp.classificarTriangulo(10, 5, 7));
    }
}
