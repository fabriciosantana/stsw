package com.triangulo;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrianguloSteps {
    private Triangulo triangulo = new Triangulo();
    private int l1, l2, l3;
    private String resultadoReal;

    @Dado("que os lados do triângulo são {int}, {int} e {int}")
    public void configurarLados(int l1, int l2, int l3) {
        this.l1 = l1;
        this.l2 = l2;
        this.l3 = l3;
    }

    @Quando("eu executo a classificação")
    public void executarClassificacao() {
        resultadoReal = triangulo.classificar(l1, l2, l3);
    }

    @Então("o resultado deve ser {string}")
    public void verificarResultado(String resultadoEsperado) {
        assertEquals(resultadoEsperado, resultadoReal);
    }
}