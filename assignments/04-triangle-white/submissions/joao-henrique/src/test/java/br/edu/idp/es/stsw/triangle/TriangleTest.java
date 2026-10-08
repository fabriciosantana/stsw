package br.edu.idp.es.stsw.triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class TriangleTest {

    @ParameterizedTest
    @CsvSource({
        "0, 10, 10",
        "-5, 10, 10",
        "201, 10, 10",
        "10, 0, 10",
        "10, -2, 10",
        "10, 201, 10",
        "10, 10, 0",
        "10, 10, -1",
        "10, 10, 201"
    })
    void testLadosInvalidos(int a, int b, int c) {
        assertEquals("Lados inválidos", Triangle.classify(a, b, c));
    }

    @ParameterizedTest
    @CsvSource({
        "1, 2, 3",
        "1, 2, 4",
        "1, 3, 2",
        "1, 4, 2",
        "3, 1, 2",
        "4, 1, 2"
    })
    void testNaoTriangulo(int a, int b, int c) {
        assertEquals("Não é um triângulo", Triangle.classify(a, b, c));
    }

    @Test
    void testEquilatero() {
        assertEquals("Equilátero", Triangle.classify(10, 10, 10));
        assertEquals("Equilátero", Triangle.classify(1, 1, 1));
        assertEquals("Equilátero", Triangle.classify(200, 200, 200));
    }

    @Test
    void testIsosceles() {
        assertEquals("Isósceles", Triangle.classify(10, 10, 6));
        assertEquals("Isósceles", Triangle.classify(10, 6, 10));
        assertEquals("Isósceles", Triangle.classify(6, 10, 10));
    }

    @Test
    void testEscaleno() {
        assertEquals("Escaleno", Triangle.classify(6, 8, 10));
        assertEquals("Escaleno", Triangle.classify(100, 120, 150));
    }

    @Test
    void testClassificarAlias() {
        assertEquals("Equilátero", Triangle.classificar(5, 5, 5));
        assertNotNull(new Triangle());
    }

    @Test
    void testMainComArgs() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(out));
            Triangle.main(new String[]{"3", "4", "5"});
            assertEquals("Escaleno\n", out.toString());
        } finally {
            System.setOut(original);
        }
    }

    @Test
    void testMainComArgsInvalidos() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(out));
            Triangle.main(new String[]{"abc", "4", "5"});
            assertEquals("Lados inválidos\n", out.toString());
        } finally {
            System.setOut(original);
        }
    }

    @Test
    void testMainInterativoValido() {
        InputStream originalIn = System.in;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream("5 5 5\n".getBytes()));
            System.setOut(new PrintStream(out));
            Triangle.main(new String[]{});
            assertEquals("Equilátero\n", out.toString());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    void testMainInterativoInvalido() {
        InputStream originalIn = System.in;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        try {
            System.setIn(new ByteArrayInputStream("abc\n".getBytes()));
            System.setOut(new PrintStream(out));
            Triangle.main(null);
            assertEquals("Lados inválidos\n", out.toString());
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
