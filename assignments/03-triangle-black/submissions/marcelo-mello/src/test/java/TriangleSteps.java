import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private int sideA;
    private int sideB;
    private int sideC;
    private String classificationResult;

    @Given("os lados do triangulo sao {int}, {int} e {int}")
    public void os_lados_do_triangulo_sao(Integer a, Integer b, Integer c) {
        this.sideA = a;
        this.sideB = b;
        this.sideC = c;
    }

    @When("eu classifico o triangulo")
    public void eu_classifico_o_triangulo() {
        this.classificationResult = Triangulo.classificar(sideA, sideB, sideC);
    }

    @Then("o resultado deve ser {string}")
    public void o_resultado_deve_ser(String expectedOutput) {
        assertEquals(expectedOutput, this.classificationResult);
    }
}
