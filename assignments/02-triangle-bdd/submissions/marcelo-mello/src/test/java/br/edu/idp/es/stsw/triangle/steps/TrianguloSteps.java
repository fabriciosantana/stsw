package br.edu.idp.es.stsw.triangle.steps;

import br.edu.idp.es.stsw.triangle.Triangulo;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrianguloSteps {

    private String classificacao;

    @Quando("eu classifico o triângulo com lados {int}, {int}, e {int}")
    public void eu_classifico_triangulo(int a, int b, int c) {
        classificacao = Triangulo.classificar(a, b, c);
    }

    @Então("a classificação deve ser {string}")
    public void a_classificacao_deve_ser(String resultadoEsperado) {
        assertEquals(resultadoEsperado, classificacao);
    }
}
