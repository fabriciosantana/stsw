package com.triangulo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class TrianguloSteps {

    private String resultado;

    @Quando("eu classifico o triângulo com lados {int}, {int} e {int}")
    public void eu_classifico_o_triangulo_com_lados(int a, int b, int c) {
        resultado = Triangulo.classificar(a, b, c); // chama sua classe de produção
    }

    @Entao("o resultado deve ser {string}")
    public void o_resultado_deve_ser(String esperado) {
        assertEquals(esperado, resultado);
    }
}
