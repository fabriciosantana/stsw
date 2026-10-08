package br.edu.idp.es.stsw.bva.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.idp.es.stsw.bva.DroneMissionPolicy;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Então;

public class DroneMissionSteps {

    private String decision;

    @Quando("eu avalio uma missão com bateria {int}, vento {int} e peso da carga {int}")
    public void avaliarMissao(int battery, int wind, int payloadWeight) {
        decision = DroneMissionPolicy.evaluate(battery, wind, payloadWeight);
    }

    @Então("a missão deve ser {string}")
    public void validarResultado(String expectedDecision) {
        assertEquals(expectedDecision, decision);
    }
}
