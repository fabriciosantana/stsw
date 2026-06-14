package matheus.steps;

import matheus.Triangulo;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

import static org.junit.Assert.assertEquals;

public class TrianguloSteps {

    private int lado1;
    private int lado2;
    private int lado3;
    private String resultado;

    /**
     * Step que armazena os três lados do triângulo
     * Padrão: "que eu tenho lados <a>, <b> e <c>"
     */
    @Dado("que eu tenho lados {int}, {int} e {int}")
    public void armazenoOsLados(int a, int b, int c) {
        this.lado1 = a;
        this.lado2 = b;
        this.lado3 = c;
    }

    /**
     * Step que chama a API pública Triangulo.classificar()
     * Usa apenas a interface pública da classe, sem acessar detalhes internos
     */
    @Quando("eu classifico o triângulo")
    public void classificoOTriangulo() {
        // Chamando APENAS a API pública da classe Triangulo
        this.resultado = Triangulo.classificar(lado1, lado2, lado3);
    }

    /**
     * Step que valida o resultado obtido
     * Padrão: "o resultado deve ser \"<resultado>\""
     */
    @Então("o resultado deve ser {string}")
    public void validoOResultado(String resultadoEsperado) {
        // Verificação com mensagem de erro detalhada
        assertEquals(
            String.format(
                "Erro: esperado '%s' mas obtive '%s' para triângulo (%d, %d, %d)",
                resultadoEsperado,
                resultado,
                lado1,
                lado2,
                lado3
            ),
            resultadoEsperado,
            resultado
        );
    }
}