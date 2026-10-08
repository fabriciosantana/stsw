package br.edu.idp.es.stsw.bva.steps;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.idp.es.stsw.bva.DroneMissionPolicy;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DroneMissionSteps {

    private final DroneMissionPolicy policy = new DroneMissionPolicy();
    private String decision;

    @When("eu avalio uma missão com bateria {int}, vento {int} e peso da carga {int}")
    public void eu_avalio_uma_missão_com_bateria_vento_e_peso_da_carga(Integer battery, Integer wind, Integer payloadWeight) {
        decision = policy.evaluate(battery, wind, payloadWeight);
    }
    /*
    public void avaliarMissao(int battery, int wind, int payloadWeight) {
        decision = policy.evaluate(battery, wind, payloadWeight);
    }
        //*/

    @Then("a missão deve ser {string}")
    public void validarResultado(String expectedDecision) {
        // assertEquals(expectedDecision, decision);
    }
}
