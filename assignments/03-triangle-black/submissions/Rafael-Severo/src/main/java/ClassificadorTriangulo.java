import java.util.Scanner;

public class ClassificadorTriangulo {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Classificador de Triângulos ===");

        System.out.print("Digite o lado A: ");
        int a = scanner.nextInt();

        System.out.print("Digite o lado B: ");
        int b = scanner.nextInt();

        System.out.print("Digite o lado C: ");
        int c = scanner.nextInt();

        String resultado = classificar(a, b, c);

        System.out.println("Resultado: " + resultado);

        scanner.close();
    }

    public static String classificar(int a, int b, int c) {

        if (a < 1 || a > 200 || b < 1 || b > 200 || c < 1 || c > 200) {
            return "Lados inválidos";
        }

        if ((a + b <= c) || (a + c <= b) || (b + c <= a)) {
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

    private static void testar(int a, int b, int c, String esperado) {

        String resultado = classificar(a, b, c);

        if (resultado.equals(esperado)) {
            System.out.println("[OK] (" + a + "," + b + "," + c + ") -> " + resultado);
        } else {
            System.out.println("[FALHA] (" + a + "," + b + "," + c + ") esperado: " + esperado + " retornou: " + resultado);
        }
    }
}