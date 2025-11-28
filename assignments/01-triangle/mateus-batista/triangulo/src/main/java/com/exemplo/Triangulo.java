package com.exemplo;

public class Triangulo {
    public static String tipoTriangulo(int a, int b, int c) {
        if (a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c > 200) return "Lados inválidos";
        if (!(a + b > c && a + c > b && b + c > a)) return "Não é um triângulo";
        if (a == b && b == c) return "Equilátero";
        if (a == b || a == c || b == c) return "Isósceles";
        return "Escaleno";
    }
}
