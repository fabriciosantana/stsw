package br.edu.idp.es.stsw.triangle.steps;

import br.edu.idp.es.stsw.triangle.Triangle;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleBlackBoxSteps {

    private int a;
    private int b;
    private int c;
    private String resultado;

    @Dado("que os valores de entrada para os lados são {int}, {int} e {int}")
    public void queOsValoresDeEntradaParaOsLadosSao(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Quando("a função de classificação da caixa-preta for consultada")
    public void aFuncaoDeClassificacaoDaCaixaPretaForConsultada() {
        this.resultado = Triangle.classify(a, b, c);
    }

    @Então("a saída observada deve ser {string}")
    public void aSaidaObservadaDeveSer(String saidaEsperada) {
        assertEquals(saidaEsperada, this.resultado);
    }
}
