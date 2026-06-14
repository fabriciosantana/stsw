package matheus;

import java.util.Scanner;

public class Triangulo {

    public static String classificar(int a, int b, int c) {

        if (a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c > 200) {
            return "Lados inválidos";
        }

        if (a + b <= c || a + c <= b || b + c <= a) {
            return "Não é um triângulo";
        }

        if (a == b && b == c) {
            return "Equilátero";
        } else if (a == b || a == c || b == c) {
            return "Isósceles";
        } else {
            return "Escaleno";
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int a = scanner.nextInt();
        int b = scanner.nextInt();
        int c = scanner.nextInt();

        System.out.println(classificar(a, b, c));

        scanner.close();
    }
}