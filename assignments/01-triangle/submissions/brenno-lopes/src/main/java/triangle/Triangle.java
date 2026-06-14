package triangle;

public class Triangle {

    public static final int MIN_SIDE = 1;
    public static final int MAX_SIDE = 200;

    public String classify(int a, int b, int c) {
        if (!isValid(a) || !isValid(b) || !isValid(c)) {
            return "Lados inválidos";
        }

        if (!isTriangle(a, b, c)) {
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

    private boolean isValid(int side) {
        return side >= MIN_SIDE && side <= MAX_SIDE;
    }

    private boolean isTriangle(int a, int b, int c) {
        return (a + b > c) && (a + c > b) && (b + c > a);
    }
}