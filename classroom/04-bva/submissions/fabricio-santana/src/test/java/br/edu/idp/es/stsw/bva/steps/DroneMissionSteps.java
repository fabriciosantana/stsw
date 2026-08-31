package br.edu.idp.es.stsw.bva.steps;

//import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.idp.es.stsw.bva.domain.DroneMissionPolicy;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DroneMissionSteps {

    private final DroneMissionPolicy policy = new DroneMissionPolicy();
    private String decision;

    @When("eu avalio uma missão com bateria {int}, vento {int} e peso da carga {int}")
    public void avaliarMissao(int battery, int wind, int payloadWeight) {
        decision = policy.evaluate(battery, wind, payloadWeight);
    }

    @Then("a missão deve ser {string}")
    public void validarResultado(String expectedDecision) {
        //assertEquals(expectedDecision, decision);
        
    }
}
