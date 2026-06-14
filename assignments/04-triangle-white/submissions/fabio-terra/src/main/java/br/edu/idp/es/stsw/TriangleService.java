package br.edu.idp.es.stsw;

public class TriangleService {
    public String identificarTriangulo(int a, int b, int c) {
        if (a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c > 200) {
            return "Invalido";
        }
        if (a >= b + c || b >= a + c || c >= a + b) {
            return "Nao e um triangulo";
        }
        if (a == b && b == c) {
            return "Equilatero";
        }
        if (a == b || a == c || b == c) {
            return "Isosceles";
        }
        return "Escaleno";
    }
}