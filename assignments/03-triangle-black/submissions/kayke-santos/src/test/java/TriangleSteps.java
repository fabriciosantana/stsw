import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private int a;
    private int b;
    private int c;
    private String resultado;

    @Given("que os lados do triângulo são {int}, {int} e {int}")
    public void queOsLadosDoTrianguloSao(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @When("o triângulo é classificado")
    public void oTrianguloEClassificado() {
        this.resultado = Triangle.classify(a, b, c);
    }

    @Then("o resultado deve ser {string}")
    public void oResultadoDeveSer(String resultadoEsperado) {
        assertEquals(resultadoEsperado, this.resultado);
    }
}
