package br.edu.idp.es.stsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("TriangleService — cobertura de statements")
class TriangleServiceTest {

    private TriangleService service;

    @BeforeEach
    void setUp() {
        service = new TriangleService();
    }

    // =========================================================================
    // S1 — D1 verdadeira: a <= 0 || b <= 0 || c <= 0 || a > 200 || b > 200 || c >
    // 200
    // Qualquer valor fora do domínio [1..200] deve executar o return "Invalido".
    // =========================================================================
    @Nested
    @DisplayName("S1 — return \"Invalido\" (D1: lado fora do domínio)")
    class QuandoInvalido {

        @Test
        @DisplayName("a = 0 → executa return \"Invalido\"")
        void ladoAIgualZero() {
            assertEquals("Invalido", service.identificarTriangulo(0, 5, 5));
        }
    }

    // =========================================================================
    // S2 — D2 verdadeira: a >= b+c || b >= a+c || c >= a+b
    // Todos os lados estão no domínio válido, mas violam a desigualdade triangular.
    // =========================================================================
    @Nested
    @DisplayName("S2 — return \"Nao e um triangulo\" (D2: desigualdade triangular violada)")
    class QuandoNaoETriangulo {

        @Test
        @DisplayName("a >= b+c → executa return \"Nao e um triangulo\"")
        void ladoAMaiorQueSomaDosOutros() {
            assertEquals("Nao e um triangulo", service.identificarTriangulo(10, 3, 3));
        }
    }

    // =========================================================================
    // S3 — D3 verdadeira: a == b && b == c
    // D1 e D2 são falsas; os três lados são iguais.
    // =========================================================================
    @Nested
    @DisplayName("S3 — return \"Equilatero\" (D3: três lados iguais)")
    class QuandoEquilatero {

        @Test
        @DisplayName("a == b == c → executa return \"Equilatero\"")
        void tresLadosIguais() {
            assertEquals("Equilatero", service.identificarTriangulo(5, 5, 5));
        }
    }

    // =========================================================================
    // S4 — D4 verdadeira: a == b || a == c || b == c
    // D1, D2 e D3 são falsas; exatamente dois lados são iguais.
    // =========================================================================
    @Nested
    @DisplayName("S4 — return \"Isosceles\" (D4: dois lados iguais)")
    class QuandoIsosceles {

        @Test
        @DisplayName("a == b, b != c → executa return \"Isosceles\"")
        void doisLadosIguais() {
            assertEquals("Isosceles", service.identificarTriangulo(5, 5, 3));
        }
    }

    // =========================================================================
    // S5 — todos os ifs falsos: ramo else implícito
    // D1, D2, D3 e D4 são falsas; os três lados são diferentes entre si.
    // =========================================================================
    @Nested
    @DisplayName("S5 — return \"Escaleno\" (todos os ifs falsos: lados todos diferentes)")
    class QuandoEscaleno {

        @Test
        @DisplayName("a != b != c → executa return \"Escaleno\"")
        void tresLadosDiferentes() {
            assertEquals("Escaleno", service.identificarTriangulo(3, 4, 5));
        }
    }
}