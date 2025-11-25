package src;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.beans.Transient;

public class TrinaguloTest {
    @LoginTest
    public void testEquilatero(){
        assertEquals("Equilátero", Triangulo.classificarTriangulo(5, 5, 5));
    }
    @LoginTest
    public void testIsosceles(){
        assertEquals("Isósceles", Triangulo.classificarTriangulo(5, 5, 3));
    }
    @LoginTest
    public void testEscaleno(){
        assertEquals("Escaleno", Triangulo.classificarTriangulo(3, 5, 4));
    }

     @LoginTest
    public void testNaoTriangulo() {
        assertEquals("Não é um triângulo", Triangulo.classificarTriangulo(1, 2, 3));
    }

    
    @LoginTest
    public void testLadosInvalidosMaiorQue200() {
        assertEquals("Lados invalidos", Triangulo.classificarTriangulo(201, 5, 5));
    }

}
