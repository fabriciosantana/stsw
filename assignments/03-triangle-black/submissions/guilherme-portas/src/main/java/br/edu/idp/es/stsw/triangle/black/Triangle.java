package br.edu.idp.es.stsw.triangle.black;

import java.util.Scanner;

public class Triangle {
    public static boolean validateTriangle(int a, int b, int c) {
        if (a + b <= c || a + c <= b || b + c <= a)
            return false;
        else
            return true;
    }

    public static String classify(int a, int b, int c) {
        if (a < 1 || a > 200 || b < 1 || b > 200 || c < 1 || c > 200)
            return "Lados inválidos";

        if (!validateTriangle(a, b, c))
            return "Não é um triângulo";

        if (a == b && b == c)
            return "Equilátero";

        if (a == b || a == c || b == c)
            return "Isósceles";

        return "Escaleno";
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int a, b, c;
        while(true) {
            System.out.print("Digite os valores de 'a', 'b' e 'c': ");
            a = scanner.nextInt();
            b = scanner.nextInt();
            c = scanner.nextInt();
            if(a < 1 || a > 200 || b < 1 || b > 200 || c < 1 || c > 200) System.out.println("Dados inválidos");
            else break;
        }

        System.out.println(classify(a, b, c));
    }
}
