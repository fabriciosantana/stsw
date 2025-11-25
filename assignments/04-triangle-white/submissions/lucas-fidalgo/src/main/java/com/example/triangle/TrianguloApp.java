package com.example.triangle;

/**
 * Classificador de triângulos - versão para testes white-box.
 * Regras:
 *  - Lados devem estar entre 1 e 200 (inclusive)
 *  - Não pode violar a desigualdade triangular (a + b > c etc.)
 *  - Classificações: Equilátero, Isósceles, Escaleno, Não é um triângulo, Lados inválidos
 */
public class TrianguloApp {

    public static String classificarTriangulo(int a, int b, int c) {
        // Validação de faixa e positividade
        if (a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c > 200) {
            return "Lados inválidos";
        }
        // Desigualdade triangular
        if (a + b <= c || a + c <= b || b + c <= a) {
            return "Não é um triângulo";
        }
        // Classificação
        if (a == b && b == c) {
            return "Equilátero";
        } else if (a == b || a == c || b == c) {
            return "Isósceles";
        } else {
            return "Escaleno";
        }
    }
}
