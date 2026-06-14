package app;

public class Triangle {

    public String classify(int a, int b, int c) {
        if (!isValidSide(a) || !isValidSide(b) || !isValidSide(c)) {
            return "Lados invalidos";
        }
        if (!isValidTriangle(a, b, c)) {
            return "Nao eh um triangulo";
        }
        if (a == b && b == c) {
            return "Equilatero";
        }
        if (a == b || b == c || a == c) {
            return "Isosceles";
        }
        return "Escaleno";
    }

    private boolean isValidSide(int side) {
        return side >= 1 && side <= 200;
    }

    private boolean isValidTriangle(int a, int b, int c) {
        return (a + b > c) && (a + c > b) && (b + c > a);
    }
}
