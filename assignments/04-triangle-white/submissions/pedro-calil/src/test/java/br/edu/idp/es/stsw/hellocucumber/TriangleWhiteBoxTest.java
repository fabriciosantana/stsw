package br.edu.idp.es.stsw.hellocucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("White-box: TriangleClassifier.classificar")
public class TriangleWhiteBoxTest {

    private String run(int a, int b, int c) {
        return TriangleClassifier.classificar(a, b, c);
    }

    // ---- 1) Cobertura de limites do intervalo [1..200] ----
    @Nested
    @DisplayName("Intervalo [1..200]")
    class Intervalo {

        @ParameterizedTest(name = "Dentro: {0},{1},{2}")
        @CsvSource({
            "1,1,1",
            "200,200,200",
            "1,200,200",
            "200,1,200",
            "200,200,1"
        })
        void valoresDentroDoIntervalo(int a, int b, int c) {
            // só garante que não cai em "Lados inválidos" e classifica corretamente
            String r = run(a,b,c);
            // qualquer um desses é aceitável (depende dos números):
            // Equilátero, Isósceles ou Escaleno — aqui não validamos tipo específico
            // portanto apenas verificamos que NÃO é "Lados inválidos"
            org.junit.jupiter.api.Assertions.assertNotEquals("Lados inválidos", r);
        }

        @ParameterizedTest(name = "Fora: {0},{1},{2} -> Lados inválidos")
        @CsvSource({
            "0,5,5", "-1,5,5", "201,5,5",
            "5,0,5", "5,-1,5", "5,201,5",
            "5,5,0", "5,5,-1", "5,5,201"
        })
        void valoresForaDoIntervalo(int a, int b, int c) {
            assertEquals("Lados inválidos", run(a,b,c));
        }
    }

    // ---- 2) Cada condição de formaTriangulo falhando ----
    @Nested
    @DisplayName("Desigualdades do triângulo")
    class Desigualdades {

        @LoginTest @DisplayName("Falha (a+b>c): 1,2,3")
        void falhaPrimeira() { assertEquals("Não é um triângulo", run(1,2,3)); }

        @LoginTest @DisplayName("Falha (a+c>b): 1,3,2")
        void falhaSegunda()  { assertEquals("Não é um triângulo", run(1,3,2)); }

        @LoginTest @DisplayName("Falha (b+c>a): 3,1,2")
        void falhaTerceira() { assertEquals("Não é um triângulo", run(3,1,2)); }
    }

    // ---- 3) Ramos de classificação ----
    @Nested
    @DisplayName("Classificação por lados")
    class Classificacao {

        @LoginTest @DisplayName("Equilátero: 5,5,5")
        void equilatero() {
            assertEquals("Equilátero", run(5,5,5));
        }

        @ParameterizedTest(name = "Isósceles: {0},{1},{2}")
        @CsvSource({
            "5,5,3", "5,3,5", "3,5,5", "10,10,15", "1,2,2"
        })
        void isosceles(int a, int b, int c) {
            assertEquals("Isósceles", run(a,b,c));
        }

        @ParameterizedTest(name = "Escaleno: {0},{1},{2}")
        @CsvSource({
            "4,5,6", "7,8,9", "3,4,5"
        })
        void escaleno(int a, int b, int c) {
            assertEquals("Escaleno", run(a,b,c));
        }
    }
}
