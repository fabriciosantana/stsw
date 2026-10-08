package br.edu.idp.es.stsw.triangle.steps;

import br.edu.idp.es.stsw.triangle.Triangle;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private int a;
    private int b;
    private int c;
    private String resultado;

    @Dado("que os lados fornecidos são {int}, {int} e {int}")
    public void queOsLadosFornecidosSao(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Quando("o classificador de triângulo é executado")
    public void oClassificadorDeTrianguloEExecutado() {
        this.resultado = Triangle.classify(a, b, c);
    }

    @Então("o resultado deve ser {string}")
    public void oResultadoDeveSer(String resultadoEsperado) {
        assertEquals(resultadoEsperado, this.resultado);
    }
}
