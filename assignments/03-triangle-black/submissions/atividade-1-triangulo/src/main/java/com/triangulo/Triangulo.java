package com.triangulo;

public class Triangulo {
    public String classificar(int a, int b, int c) {
        // Validação de Limites (BVA)
        if (a < 1 || a > 200 || b < 1 || b > 200 || c < 1 || c > 200) {
            return "Lados inválidos";
        }
        // Validação de Existência
        if (a + b <= c || a + c <= b || b + c <= a) {
            return "Não é um triângulo";
        }
        // Classificação
        if (a == b && b == c) return "Equilátero";
        if (a == b || a == c || b == c) return "Isósceles";
        return "Escaleno";
    }
}