package demo.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import static org.assertj.core.api.Assertions.assertThat;

public class PostsApiSteps {

    private String baseUrl;
    private Response resposta;

    // -----------------------------------------------------------------------
    // DADO
    // -----------------------------------------------------------------------

    @Dado("que a API está disponível em {string}")
    public void queAApiEstaDisponivelEm(String url) {
        this.baseUrl = url;
        SerenityRest.setDefaultBasePath("");
    }

    // -----------------------------------------------------------------------
    // QUANDO
    // -----------------------------------------------------------------------

    @Quando("eu faço um GET em {string}")
    public void euFacoUmGetEm(String endpoint) {
        resposta = SerenityRest
                .given()
                    .baseUri(baseUrl)
                    .header("Content-Type", "application/json")
                .when()
                    .get(endpoint);
    }

    @Quando("eu faço um POST em {string} com o corpo:")
    public void euFacoUmPostEmComOCorpo(String endpoint, String corpo) {
        resposta = SerenityRest
                .given()
                    .baseUri(baseUrl)
                    .header("Content-Type", "application/json")
                    .body(corpo)
                .when()
                    .post(endpoint);
    }

    // -----------------------------------------------------------------------
    // ENTÃO
    // -----------------------------------------------------------------------

    @Então("o status da resposta deve ser {int}")
    public void oStatusDaRespostaDeveSer(int statusEsperado) {
        assertThat(resposta.getStatusCode())
                .as("Status HTTP inesperado")
                .isEqualTo(statusEsperado);
    }

    @Então("a resposta deve conter uma lista de posts")
    public void aRespostaDeveConterUmaListaDePosts() {
        int tamanho = resposta.jsonPath().getList("$").size();
        assertThat(tamanho)
                .as("A lista de posts deveria ter mais de 0 itens")
                .isGreaterThan(0);
    }

    @Então("o campo {string} da resposta deve ser {int}")
    public void oCampoDaRespostaDeveSerInt(String campo, int valorEsperado) {
        int valorReal = resposta.jsonPath().getInt(campo);
        assertThat(valorReal)
                .as("Campo '%s' com valor inesperado", campo)
                .isEqualTo(valorEsperado);
    }

    @Então("o campo {string} da resposta deve ser {string}")
    public void oCampoDaRespostaDeveSerString(String campo, String valorEsperado) {
        String valorReal = resposta.jsonPath().getString(campo);
        assertThat(valorReal)
                .as("Campo '%s' com valor inesperado", campo)
                .isEqualTo(valorEsperado);
    }
}
