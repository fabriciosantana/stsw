package br.com.filipemarraa.seminario;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SeminarioApplicationTest {

    @Test
    void shouldPrintScoreAndRiskLevel() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        try {
            SeminarioApplication.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        String consoleOutput = output.toString(StandardCharsets.UTF_8);

        assertTrue(consoleOutput.contains("Security score: 92"));
        assertTrue(consoleOutput.contains("Risk level: LOW"));
    }
}

