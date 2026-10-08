import java.util.Scanner;

public class Triangulo {

    public static String classificar(int a, int b, int c) {
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

    public static String classify(int a, int b, int c) {
        return classificar(a, b, c);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if (args != null && args.length == 3) {
            try {
                int a = Integer.parseInt(args[0]);
                int b = Integer.parseInt(args[1]);
                int c = Integer.parseInt(args[2]);
                System.out.println(classificar(a, b, c));
            } catch (Exception e) {
                System.out.println("Lados inválidos");
            }
        } else if (sc.hasNextInt()) {
            int a = sc.nextInt();
            int b = sc.nextInt();
            int c = sc.nextInt();
            System.out.println(classificar(a, b, c));
        }
        sc.close();
    }
}
