package triangle;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TriangleSteps {

    private int sideA;
    private int sideB;
    private int sideC;
    private String result;

    private final Triangle triangle = new Triangle();

    @Dado("que os lados são {int}, {int} e {int}")
    public void queOsLadosSao(int a, int b, int c) {
        this.sideA = a;
        this.sideB = b;
        this.sideC = c;
    }

    @Quando("eu classifico o triângulo")
    public void euClassificoOTriangulo() {
        this.result = triangle.classify(sideA, sideB, sideC);
    }

    @Então("o resultado deve ser {string}")
    public void oResultadoDeveSer(String expected) {
        assertEquals(expected, result);
    }
}
