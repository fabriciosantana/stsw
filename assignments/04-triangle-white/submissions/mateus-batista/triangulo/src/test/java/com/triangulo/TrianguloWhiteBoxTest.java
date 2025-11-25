package com.triangulo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrianguloWhiteBoxTest {

    // 1) FAIXA 1..200  --------------------------------------------------------
    @Nested
    @DisplayName("Faixa (1..200) e condição composta a||b||c")
    class Faixa {

        @ParameterizedTest(name = "Fora da faixa: ({0},{1},{2}) -> Lados inválidos")
        @CsvSource({
            "0,1,1",      // a inválido (0)
            "-1,2,3",     // a inválido (negativo)
            "1,0,1",      // b inválido (0)
            "2,-5,3",     // b inválido (negativo)
            "1,1,0",      // c inválido (0)
            "200,200,201" // c inválido (>200)
        })
        void foraDaFaixa(int a, int b, int c) {
            assertEquals("Lados inválidos", Triangulo.classificar(a, b, c));
        }

        @Test
        @DisplayName("Todos dentro da faixa: passa para próximas regras")
        void dentroDaFaixaNaoRetornaInvalido() {
            assertNotEquals("Lados inválidos", Triangulo.classificar(1, 1, 1));
        }
    }

    // 2) ORDENACAO x>y ; y>z ; x>y  ------------------------------------------
    @Nested
    @DisplayName("Ordenação (exercitar ramos x>y, y>z, x>y)")
    class Ordenacao {

        @Test @DisplayName("Nenhum swap (F,F,F)")
        void nenhumSwap() {
            assertEquals("Escaleno", Triangulo.classificar(2, 3, 4));
        }

        @Test @DisplayName("Apenas 1º swap (T,F,F) - a>b")
        void apenasPrimeiroSwap() {
            assertEquals("Escaleno", Triangulo.classificar(2, 1, 3));
        }

        @Test @DisplayName("Apenas 2º swap (F,T,F) - b>c")
        void apenasSegundoSwap() {
            assertEquals("Escaleno", Triangulo.classificar(1, 3, 2));
        }

        @Test @DisplayName("Todos swaps (T,T,T) -> vira 1,2,3 (degenerado)")
        void todosSwaps() {
            assertEquals("Não é um triângulo", Triangulo.classificar(3, 2, 1));
        }
    }

    // 3) EXISTENCIA x+y<=z  ---------------------------------------------------
    @Nested
    @DisplayName("Existência do triângulo (x+y<=z)")
    class Existencia {

        @ParameterizedTest(name = "Degenerado: ({0},{1},{2}) -> Não é um triângulo")
        @CsvSource({
            "1,1,2", // igualdade
            "2,3,5"  // igualdade
        })
        void degenerado(int a, int b, int c) {
            assertEquals("Não é um triângulo", Triangulo.classificar(a, b, c));
        }

        @Test
        @DisplayName("Logo após a fronteira: válido")
        void aposFronteiraValido() {
            assertEquals("Escaleno", Triangulo.classificar(2, 3, 4));
        }
    }

    // 4) CLASSIFICACAO  -------------------------------------------------------
    @Nested
    @DisplayName("Classificação (Equilátero, Isósceles, Escaleno)")
    class Classificacao {

        @Test
        void equilatero() {
            assertEquals("Equilátero", Triangulo.classificar(200, 200, 200)); // teto da faixa
        }

        @ParameterizedTest(name = "Isósceles: ({0},{1},{2})")
        @CsvSource({
            "3,3,4", // x==y após ordenação
            "4,3,3", // y==z após ordenação
            "3,4,3"  // idem
        })
        void isosceles(int a, int b, int c) {
            assertEquals("Isósceles", Triangulo.classificar(a, b, c));
        }

        @ParameterizedTest(name = "Escaleno: ({0},{1},{2})")
        @CsvSource({
            "3,4,5",
            "2,3,4"
        })
        void escaleno(int a, int b, int c) {
            assertEquals("Escaleno", Triangulo.classificar(a, b, c));
        }
    }
}

