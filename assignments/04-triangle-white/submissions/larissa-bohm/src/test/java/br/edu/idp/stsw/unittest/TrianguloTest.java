package br.edu.idp.stsw.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrianguloTest {

<<<<<<< HEAD
    @LoginTest
    void simpleClassificationTest() {
        assertEquals("Isósceles", Triangulo.classificar(5, 5, 3));
=======
    @Nested
    @DisplayName("BVA: Validação de faixa (1..200)")
    class Faixa {
        @ParameterizedTest
        @CsvSource({
            "0,5,5",
            "201,5,5",
            "5,0,5",
            "5,201,5",
            "5,5,0",
            "5,5,201"
        })
        void ladoForaDaFaixaFalhaUnica(int a, int b, int c) {
            assertEquals("Lados inválidos", Triangulo.classificar(a,b,c));
        }

        @ParameterizedTest
        @CsvSource({
            "1,1,1,Equilátero",
            "200,200,200,Equilátero",
            "1,2,2,Isósceles",
            "2,3,4,Escaleno"
        })
        void limitesValidos(int a, int b, int c, String esperado) {
            assertEquals(esperado, Triangulo.classificar(a,b,c));
        }
>>>>>>> 8ed2f9f6285af5a48c798f5215e9ec65d7b24c7a
    }

    @Nested
    @DisplayName("BVA: Desigualdade do triângulo (a+b>c)")
    class Forma {
        @ParameterizedTest
        @CsvSource({
            "2,3,5",
            "3,3,6",
            "4,7,11"
        })
        void bordaInvalida_ab_igual_c(int a, int b, int c) {
            assertEquals("Não é triangulo", Triangulo.classificar(a,b,c));
        }

        @ParameterizedTest
        @CsvSource({
            "2,3,4,Escaleno",
            "3,3,5,Isósceles",
            "5,6,10,Escaleno"
        })
        void minimoValido_ab_maior_c(int a, int b, int c, String esperado) {
            assertEquals(esperado, Triangulo.classificar(a,b,c));
        }
    }

    @Nested
    @DisplayName("Classificações e simetria")
    class Classes {
        @ParameterizedTest
        @CsvSource({"3,3,3", "50,50,50"})
        void equilatero(int a, int b, int c) {
            assertEquals("Equilátero", Triangulo.classificar(a,b,c));
        }

        @ParameterizedTest
        @CsvSource({"5,5,3", "5,3,5", "3,5,5"})
        void isoscelesPermutacoes(int a, int b, int c) {
            assertEquals("Isósceles", Triangulo.classificar(a,b,c));
        }

        @ParameterizedTest
        @CsvSource({"3,4,5", "4,5,6"})
        void escaleno(int a, int b, int c) {
            assertEquals("Escaleno", Triangulo.classificar(a,b,c));
        }
    }

    @Nested
    @DisplayName("Falhas múltiplas")
    class FalhasMultiplas {
        @ParameterizedTest
        @CsvSource({
            "0,1,3",
            "201,2,2",
            "5,0,1000"
        })
        void falhasMultiplas_retornamInvalidSides(int a, int b, int c) {
            assertEquals("Lados inválidos", Triangulo.classificar(a,b,c));
        }
    }
}
