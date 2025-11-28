package com.exemplo;

import io.cucumber.java.en.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrianguloSteps {
    private int a,b,c;
    private String resultado;

    @Given("os lados do triângulo são {int}, {int}, {int}")
    public void lados(int x, int y, int z) { a=x; b=y; c=z; }

    @When("verifico o tipo do triângulo")
    public void verifico() { resultado = Triangulo.tipoTriangulo(a,b,c); }

    @Then("o resultado deve ser {string}")
    public void resultado(String esperado) { assertEquals(esperado, resultado); }
}
