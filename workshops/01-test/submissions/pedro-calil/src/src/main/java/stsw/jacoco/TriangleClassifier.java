package stsw.jacoco;

public class TriangleClassifier {

    public enum Tipo {
        EQUILATERO, ISOSCELES, ESCALENO, INVALIDO, FORA_DO_INTERVALO
    }

    /**
     * Classifica um triângulo a partir dos lados a, b, c.
     * Regras do curso:
     * - Cada lado deve estar no intervalo [1, 200].
     * - Desigualdade triangular: a < b + c, b < a + c, c < a + b.
     */
    public static Tipo classificar(int a, int b, int c) {
        if (!entre1e200(a) || !entre1e200(b) || !entre1e200(c)) {
            return Tipo.FORA_DO_INTERVALO;
        }
        if (!trianguloValido(a, b, c)) {
            return Tipo.INVALIDO;
        }
        if (a == b && b == c) return Tipo.EQUILATERO;
        if (a == b || a == c || b == c) return Tipo.ISOSCELES;
        return Tipo.ESCALENO;
    }

    static boolean trianguloValido(int a, int b, int c) {
        // Use < (e não <=) para evitar soma igual ao terceiro lado
        return a < b + c && b < a + c && c < a + b;
    }

    static boolean entre1e200(int x) {
        return x >= 1 && x <= 200;
    }
}
