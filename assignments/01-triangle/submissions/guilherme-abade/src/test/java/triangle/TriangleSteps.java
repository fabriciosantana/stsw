package triangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TriangleSteps {
    private int a;
    private int b;
    private int c;
    private String result;

    @Given("os lados {int}, {int} e {int}")
    public void sides(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @When("o triângulo é classificado")
    public void classify() {
        result = Triangle.classify(a, b, c);
    }

    @Then("o resultado é {string}")
    public void resultIs(String expected) {
        assertEquals(expected, result);
    }
}
