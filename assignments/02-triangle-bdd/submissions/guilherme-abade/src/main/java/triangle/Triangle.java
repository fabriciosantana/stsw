package triangle;

import java.util.Scanner;

public class Triangle {
    public static String classify(int a, int b, int c) {
        if (a < 1 || a > 200 || b < 1 || b > 200 || c < 1 || c > 200) {
            return "Lados inválidos";
        }
        if (a + b <= c || a + c <= b || b + c <= a) {
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

    public static void main(String[] args) {
        String[] sides = args;
        if (args.length == 0) {
            Scanner input = new Scanner(System.in);
            sides = new String[3];
            for (int i = 0; i < 3; i++) {
                if (!input.hasNext()) {
                    System.out.println("Lados inválidos");
                    return;
                }
                sides[i] = input.next();
            }
        }
        if (sides.length != 3) {
            System.out.println("Lados inválidos");
            return;
        }
        try {
            System.out.println(classify(Integer.parseInt(sides[0]), Integer.parseInt(sides[1]),
                    Integer.parseInt(sides[2])));
        } catch (NumberFormatException exception) {
            System.out.println("Lados inválidos");
        }
    }
}
