package br.edu.idp.es.stsw.hellocucumber.steps;

import static org.junit.Assert.assertEquals;

import br.edu.idp.es.stsw.hellocucumber.Triangulo;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;

public class TrianguloStepsPT {

    private int a, b, c;
    private String resultado;

    @Dado("que eu tenho os lados {int}, {int} e {int}")
    public void que_eu_tenho_os_lados(Integer a, Integer b, Integer c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Quando("eu classifico o triângulo")
    public void eu_classifico_o_triangulo() {
        resultado = Triangulo.classificar(a, b, c);
    }

    @Entao("o resultado deve ser {string}")
    public void o_resultado_deve_ser(String esperado) {
        assertEquals(esperado, resultado);
    }
}
