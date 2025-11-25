package triangulo;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrianguloTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    // Testes para tamanhoCorreto()
    @LoginTest
    public void testTamanhoCorretoValido() {
        assertTrue(Triangulo.tamanhoCorreto(1));
        assertTrue(Triangulo.tamanhoCorreto(200));
        assertTrue(Triangulo.tamanhoCorreto(100));
    }

    @LoginTest
    public void testTamanhoCorretoInvalido() {
        assertFalse(Triangulo.tamanhoCorreto(0));
        assertFalse(Triangulo.tamanhoCorreto(201));
        assertFalse(Triangulo.tamanhoCorreto(-5));
    }

    // Testes para ehInvalido()
    @LoginTest
    public void testEhInvalidoTrianguloValido() {
        assertFalse(Triangulo.ehInvalido(3, 4, 5));
        assertFalse(Triangulo.ehInvalido(5, 5, 5));
    }

    @LoginTest
    public void testEhInvalidoTrianguloInvalido() {
        assertTrue(Triangulo.ehInvalido(1, 2, 3));
        assertTrue(Triangulo.ehInvalido(10, 1, 2));
    }

    // Testes para classificaTriangulo()
    @LoginTest
    public void testClassificaEquilatero() {
        Triangulo.classificaTriangulo(5, 5, 5);
        assertEquals("EQUILÁTERO\n", outContent.toString());
    }

    @LoginTest
    public void testClassificaIsosceles() {
        Triangulo.classificaTriangulo(5, 5, 3);
        assertEquals("ISÓSCELES\n", outContent.toString());
        outContent.reset();
        Triangulo.classificaTriangulo(5, 3, 5);
        assertEquals("ISÓSCELES\n", outContent.toString());
        outContent.reset();
        Triangulo.classificaTriangulo(3, 5, 5);
        assertEquals("ISÓSCELES\n", outContent.toString());
    }

    @LoginTest
    public void testClassificaEscaleno() {
        Triangulo.classificaTriangulo(3, 4, 5);
        assertEquals("ESCALENO\n", outContent.toString());
    }
}
