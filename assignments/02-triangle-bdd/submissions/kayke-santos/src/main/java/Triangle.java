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
        if (args.length == 3) {
            try {
                int a = Integer.parseInt(args[0]);
                int b = Integer.parseInt(args[1]);
                int c = Integer.parseInt(args[2]);
                System.out.println(classify(a, b, c));
            } catch (NumberFormatException e) {
                System.out.println("Lados inválidos");
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Digite os três números para definir o triângulo (a b c): ");
            if (scanner.hasNextInt()) {
                int a = scanner.nextInt();
                if (scanner.hasNextInt()) {
                    int b = scanner.nextInt();
                    if (scanner.hasNextInt()) {
                        int c = scanner.nextInt();
                        System.out.println(classify(a, b, c));
                    } else {
                        System.out.println("Lados inválidos");
                    }
                } else {
                    System.out.println("Lados inválidos");
                }
            } else {
                System.out.println("Lados inválidos");
            }
        }
    }
}
