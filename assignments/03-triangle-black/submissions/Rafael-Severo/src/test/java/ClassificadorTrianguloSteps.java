import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;
// Import corrigido para o JUnit 4:
import static org.junit.Assert.assertEquals;

public class ClassificadorTrianguloSteps {

    private int ladoA;
    private int ladoB;
    private int ladoC;
    private String resultadoObtido;

    @Dado("que eu informo os lados do triângulo como {int}, {int} e {int}")
    public void que_eu_informo_os_lados_do_triângulo_como_e(Integer a, Integer b, Integer c) {
        this.ladoA = a;
        this.ladoB = b;
        this.ladoC = c;
    }

    @Quando("eu peço para classificar")
    public void eu_peço_para_classificar() {
        // Chama o método estático da classe que você descompilou
        this.resultadoObtido = ClassificadorTriangulo.classificar(ladoA, ladoB, ladoC);
    }

    @Então("o resultado retornado deve ser {string}")
    public void o_resultado_retornado_deve_ser(String resultadoEsperado) {
        // No JUnit 4, a mensagem de erro vem PRIMEIRO, depois o esperado, depois o obtido
        assertEquals(
            "A classificação falhou para os lados: " + ladoA + ", " + ladoB + ", " + ladoC, 
            resultadoEsperado, 
            resultadoObtido
        );
    }
}