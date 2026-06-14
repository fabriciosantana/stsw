package br.edu.idp.es.stsw.steps;

import br.edu.idp.es.stsw.Triangle;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private String resultado;
    private final Triangle triangle = new Triangle();

    @When("eu classifico um triangulo com lados {int}, {int}, {int}")
    public void eu_classifico_um_triangulo(Integer a, Integer b, Integer c) {
        resultado = triangle.classify(a, b, c);
    }

    @Then("o resultado deve ser {string}")
    public void o_resultado_deve_ser(String esperado) {
        assertEquals(esperado, resultado);
    }
}