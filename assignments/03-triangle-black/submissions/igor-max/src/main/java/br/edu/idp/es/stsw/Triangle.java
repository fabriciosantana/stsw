package br.edu.idp.es.stsw;

public class Triangle {

    public String classify(int a, int b, int c) {

        if (a < 1 || b < 1 || c < 1 || a > 200 || b > 200 || c > 200) {
            return "Lados inválidos";
        }

        if (!(a + b > c) || !(a + c > b) || !(b + c > a)) {
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
}