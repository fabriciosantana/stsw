package com.exemplo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TrianguloTest {
    @Test void equilatero() { assertEquals("Equilátero", Triangulo.tipoTriangulo(5,5,5)); }
    @Test void isosceles()  { assertEquals("Isósceles",  Triangulo.tipoTriangulo(5,5,3)); }
    @Test void escaleno()   { assertEquals("Escaleno",   Triangulo.tipoTriangulo(5,4,3)); }
    @Test void invalido()   { assertEquals("Lados inválidos", Triangulo.tipoTriangulo(-5,0,5)); }
    @Test void naoTri()     { assertEquals("Não é um triângulo", Triangulo.tipoTriangulo(1,2,3)); }
}
