package steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import io.cucumber.java.en.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import triangulo.Triangulo;

public class TrianguloSteps {
    private int a, b, c;
    private String resultado;

    @Given("os lados do triângulo são {int}, {int}, {int}")
    public void os_lados_do_triangulo_sao(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @When("eu classifico o triângulo")
    public void eu_classifico_o_triangulo() {
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saida));

        if (!Triangulo.tamanhoCorreto(a) || !Triangulo.tamanhoCorreto(b) || !Triangulo.tamanhoCorreto(c)) {
            System.out.println("Lados inválidos");
        } else if (Triangulo.ehInvalido(a, b, c)) {
            System.out.println("Não é um triângulo");
        } else {
            Triangulo.classificaTriangulo(a, b, c);
        }

        resultado = saida.toString().trim();
    }

    @Then("a saída deve ser {string}")
    public void a_saida_deve_ser(String esperado) {
        assertEquals(esperado, resultado);
    }
}
