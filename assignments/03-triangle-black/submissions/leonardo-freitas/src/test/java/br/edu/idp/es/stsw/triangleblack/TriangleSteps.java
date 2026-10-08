package br.edu.idp.es.stsw.triangleblack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TriangleSteps {

    private Triangle triangle;
    private String result;

    @Given("um classificador de triângulos disponível")
    public void classifierIsAvailable() {
        triangle = new Triangle();
    }

    @When("eu classifico os lados {int}, {int} e {int}")
    public void classifySides(int a, int b, int c) {
        result = triangle.classify(a, b, c);
    }

    @Then("o resultado deve ser {string}")
    public void resultShouldBe(String expected) {
        assertEquals(expected, result);
    }
}
