package br.edu.idp.es.stsw.hellocucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes caixa-preta (EP + BVA) para TriangleClassifier.classificar(a,b,c).
 */
public class TriangleBlackBoxTest {

    
    @ParameterizedTest(name = "Equilátero: {0},{1},{2}")
    @CsvSource({"5,5,5","1,1,1","200,200,200"})
    void ep_equilatero(int a, int b, int c) {
        assertEquals("Equilátero", TriangleClassifier.classificar(a,b,c));
    }

    @ParameterizedTest(name = "Isósceles: {0},{1},{2}")
    @CsvSource({"5,5,3","5,3,5","3,5,5","200,200,1","1,200,200"})
    void ep_isosceles(int a, int b, int c) {
        assertEquals("Isósceles", TriangleClassifier.classificar(a,b,c));
    }

    @ParameterizedTest(name = "Escaleno: {0},{1},{2}")
    @CsvSource({"5,4,3","2,3,4","199,2,200"})
    void ep_escaleno(int a, int b, int c) {
        assertEquals("Escaleno", TriangleClassifier.classificar(a,b,c));
    }

    
    @ParameterizedTest(name = "Fora do domínio: {0},{1},{2}")
    @CsvSource({"0,1,1","-5,0,5","201,2,2","2,2,201","1,201,2"})
    void ep_dominios_invalidos(int a, int b, int c) {
        assertEquals("Lados inválidos", TriangleClassifier.classificar(a,b,c));
    }

    @ParameterizedTest(name = "Não forma triângulo: {0},{1},{2}")
    @CsvSource({"1,2,3","1,1,2","200,1,1","199,1,200"})
    void ep_nao_triangulo(int a, int b, int c) {
        assertEquals("Não é um triângulo", TriangleClassifier.classificar(a,b,c));
    }

    
    @Nested @DisplayName("BVA: domínio 1..200")
    class Dominio {
        @LoginTest void minimoValido() {
            assertEquals("Equilátero", TriangleClassifier.classificar(1,1,1));
        }
        @LoginTest void maximoValido() {
            assertEquals("Equilátero", TriangleClassifier.classificar(200,200,200));
        }
        @LoginTest void abaixoMinimo() {
            assertEquals("Lados inválidos", TriangleClassifier.classificar(0,1,1));
        }
        @LoginTest void acimaMaximo() {
            assertEquals("Lados inválidos", TriangleClassifier.classificar(201,2,2));
        }
    }

    
    @Nested @DisplayName("BVA: desigualdade (x+y ? z)")
    class Desigualdade {
        @LoginTest void fronteiraIgual() {
            assertEquals("Não é um triângulo", TriangleClassifier.classificar(1,1,2));
        }
        @LoginTest void logoAcima() {
            assertEquals("Isósceles", TriangleClassifier.classificar(1,2,2));
        }
        @LoginTest void extremoAltoFronteira() {
            assertEquals("Não é um triângulo", TriangleClassifier.classificar(199,1,200));
        }
        @LoginTest void logoAcimaAlto() {
            assertEquals("Escaleno", TriangleClassifier.classificar(199,2,200));
        }
    }
}