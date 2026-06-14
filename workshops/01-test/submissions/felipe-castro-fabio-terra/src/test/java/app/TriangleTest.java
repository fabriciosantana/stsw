package app;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Listeners(ExtentReportListener.class)
public class TriangleTest {

    private final Triangle triangle = new Triangle();

    // --- Lados invalidos ---

    @Test(priority = 1, groups = "invalidos")
    public void testLadoZeroEhInvalido() {
        Assert.assertEquals(triangle.classify(0, 3, 3), "Lados invalidos");
    }

    @Test(priority = 1, groups = "invalidos")
    public void testLadoNegativoEhInvalido() {
        Assert.assertEquals(triangle.classify(-1, 3, 3), "Lados invalidos");
    }

    @Test(priority = 1, groups = "invalidos")
    public void testLadoAcimaDoLimiteEhInvalido() {
        Assert.assertEquals(triangle.classify(201, 3, 3), "Lados invalidos");
    }

    // --- Nao eh triangulo ---

    @Test(priority = 2, groups = "invalidos")
    public void testSomaDeDoisLadosIgualAoTerceiro() {
        Assert.assertEquals(triangle.classify(1, 2, 3), "Nao eh um triangulo");
    }

    @Test(priority = 2, groups = "invalidos")
    public void testSomaDeDoisLadosMenorQueOTerceiro() {
        Assert.assertEquals(triangle.classify(1, 2, 10), "Nao eh um triangulo");
    }

    // --- Equilatero ---

    @Test(priority = 3, groups = "classificacao")
    public void testEquilatero() {
        Assert.assertEquals(triangle.classify(5, 5, 5), "Equilatero");
    }

    @Test(priority = 3, groups = "classificacao")
    public void testEquilateroLadoUm() {
        Assert.assertEquals(triangle.classify(1, 1, 1), "Equilatero");
    }

    // --- Isosceles ---

    @Test(priority = 4, groups = "classificacao")
    public void testIsoscelesABIguais() {
        Assert.assertEquals(triangle.classify(5, 5, 3), "Isosceles");
    }

    @Test(priority = 4, groups = "classificacao")
    public void testIsoscelesBCIguais() {
        Assert.assertEquals(triangle.classify(3, 5, 5), "Isosceles");
    }

    @Test(priority = 4, groups = "classificacao")
    public void testIsoscelesACIguais() {
        Assert.assertEquals(triangle.classify(5, 3, 5), "Isosceles");
    }

    // --- Escaleno ---

    @Test(priority = 5, groups = "classificacao")
    public void testEscaleno() {
        Assert.assertEquals(triangle.classify(3, 4, 5), "Escaleno");
    }

    @Test(priority = 5, groups = "classificacao")
    public void testEscalenoOutrosValores() {
        Assert.assertEquals(triangle.classify(7, 10, 12), "Escaleno");
    }

    // --- DataProvider ---

    @DataProvider(name = "casosValidos")
    public Object[][] casosValidos() {
        return new Object[][] {
            { 3, 3, 3, "Equilatero" },
            { 3, 3, 2, "Isosceles" },
            { 3, 4, 5, "Escaleno" },
            { 1, 1, 1, "Equilatero" },
            { 200, 200, 200, "Equilatero" },
        };
    }

    @Test(priority = 6, dataProvider = "casosValidos", groups = "classificacao")
    public void testCasosValidos(int a, int b, int c, String esperado) {
        Assert.assertEquals(triangle.classify(a, b, c), esperado);
    }

    // --- expectedExceptions: verifica que NullPointerException NAO ocorre ---
    // (demonstracao de expectedExceptions com um caso artificial)

    @Test(
        priority = 7,
        expectedExceptions = IllegalArgumentException.class,
        groups = "excecoes"
    )
    public void testExpectedExceptionDemo() {
        // Demonstra @Test(expectedExceptions): o teste passa SE a excecao for lancada
        throw new IllegalArgumentException(
            "Excecao esperada para fins de demonstracao"
        );
    }

    // --- timeOut: falha se demorar mais de 1000ms ---

    @Test(priority = 8, timeOut = 1000, groups = "performance")
    public void testClassifyDentroDoTempo() {
        for (int i = 0; i < 10000; i++) {
            triangle.classify(3, 4, 5);
        }
    }

    // --- enabled = false: teste desabilitado (nao sera executado) ---

    @Test(priority = 9, enabled = false, groups = "classificacao")
    public void testDesabilitado() {
        // Este teste nao sera executado — demonstra @Test(enabled = false)
        Assert.assertEquals(triangle.classify(3, 3, 3), "Escaleno");
    }

    // --- Testes que falham intencionalmente (demonstracao de falhas no relatorio) ---

    // @Test(priority = 11, groups = "falhas")
    // public void testFalhaClassificacaoErrada() {
    //     // Espera Escaleno mas triangulo (3,3,3) eh Equilatero
    //     Assert.assertEquals(
    //         triangle.classify(3, 3, 3),
    //         "Escaleno",
    //         "FALHA INTENCIONAL: classificacao errada para demonstracao"
    //     );
    // }

    // @Test(priority = 11, groups = "falhas")
    // public void testFalhaLadoInvalidoNaoDetectado() {
    //     // Espera que lado 0 seja valido — mas nao eh
    //     Assert.assertEquals(
    //         triangle.classify(0, 3, 3),
    //         "Equilatero",
    //         "FALHA INTENCIONAL: lado invalido deveria ser detectado"
    //     );
    // }

    // @Test(priority = 11, groups = "falhas")
    // public void testFalhaIsoscelesConfundidoComEscaleno() {
    //     // Espera Escaleno mas (5,5,3) eh Isosceles
    //     Assert.assertEquals(
    //         triangle.classify(5, 5, 3),
    //         "Escaleno",
    //         "FALHA INTENCIONAL: isosceles confundido com escaleno"
    //     );
    // }

    // --- SoftAssert: acumula falhas sem parar na primeira ---

    @Test(priority = 10, groups = "classificacao")
    public void testSoftAssert() {
        SoftAssert sa = new SoftAssert();

        sa.assertEquals(
            triangle.classify(3, 3, 3),
            "Equilatero",
            "Deveria ser Equilatero"
        );
        sa.assertEquals(
            triangle.classify(3, 3, 2),
            "Isosceles",
            "Deveria ser Isosceles"
        );
        sa.assertEquals(
            triangle.classify(3, 4, 5),
            "Escaleno",
            "Deveria ser Escaleno"
        );
        sa.assertEquals(
            triangle.classify(0, 3, 3),
            "Lados invalidos",
            "Deveria ser invalido"
        );

        sa.assertAll(); // so falha aqui, acumulando todos os erros acima
    }
}
