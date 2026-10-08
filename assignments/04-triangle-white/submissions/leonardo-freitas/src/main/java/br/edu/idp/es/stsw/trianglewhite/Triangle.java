package br.edu.idp.es.stsw.trianglewhite;

public class Triangle {

    private static final int MIN_SIDE = 1;
    private static final int MAX_SIDE = 200;

    public String classify(int a, int b, int c) {
        if (!isWithinAllowedRange(a) || !isWithinAllowedRange(b) || !isWithinAllowedRange(c)) {
            return "Lados inválidos";
        }

        if ((long) a + b <= c || (long) a + c <= b || (long) b + c <= a) {
            return "Não é um triângulo";
        }

        if (a == b && b == c) {
            return "Equilátero";
        }

        if (a == b || a == c || b == c) {
            return "Isósceles";
        }

        return "Escaleno";
    }

    private boolean isWithinAllowedRange(int side) {
        return side >= MIN_SIDE && side <= MAX_SIDE;
    }
}
