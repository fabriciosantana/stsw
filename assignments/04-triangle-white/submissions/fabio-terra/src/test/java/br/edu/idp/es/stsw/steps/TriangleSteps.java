package br.edu.idp.es.stsw.steps;

import br.edu.idp.es.stsw.TriangleService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private int a, b, c;
    private String resultado;
    private final TriangleService service = new TriangleService();

    @Given("que os lados informados são {int}, {int} e {int}")
    public void queOsLadosInformadosSao(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @When("eu executo a identificação")
    public void euExecutoAIdentificacao() {
        resultado = service.identificarTriangulo(a, b, c);
    }

    @Then("o sistema retorna {string}")
    public void oSistemaRetorna(String esperado) {
        assertEquals(esperado, resultado);
    }
}