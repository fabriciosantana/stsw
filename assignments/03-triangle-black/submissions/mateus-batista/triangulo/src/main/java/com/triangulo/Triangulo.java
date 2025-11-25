package com.triangulo;

public final class Triangulo {

    private Triangulo() {}

    public static String classificar(int a, int b, int c) {
        // Validação de faixa
        if (!entre1e200(a) || !entre1e200(b) || !entre1e200(c)) {
            return "Lados inválidos";
        }

        // Ordena (x <= y <= z)
        int x = a, y = b, z = c;
        if (x > y) { int t = x; x = y; y = t; }
        if (y > z) { int t = y; y = z; z = t; }
        if (x > y) { int t = x; x = y; y = t; }

        // Checagem do triângulo
        if (x + y <= z) {
            return "Não é um triângulo";
        }

        // Classificação
        if (x == y && y == z) {
            return "Equilátero";
        } else if (x == y || y == z) {
            return "Isósceles";
        } else {
            return "Escaleno";
        }
    }

    private static boolean entre1e200(int v) {
        return v >= 1 && v <= 200;
    }
}

