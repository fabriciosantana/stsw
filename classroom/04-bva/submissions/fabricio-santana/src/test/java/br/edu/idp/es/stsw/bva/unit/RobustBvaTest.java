package br.edu.idp.es.stsw.bva.unit;

import static br.edu.idp.es.stsw.bva.unit.DroneMissionTestSpecification.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import br.edu.idp.es.stsw.bva.domain.DroneMissionPolicy;

class RobustBvaTest {

    private final DroneMissionPolicy policy = new DroneMissionPolicy();

    @ParameterizedTest(name = "[{index}] missão com bateria={0}, vento={1}, pesoCarga={2} deve ser {3}")
    @CsvSource({
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (MIN_BATTERY - 1) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + DENIED,
            (MIN_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (MIN_BATTERY + 1) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (MAX_BATTERY - 1) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (MAX_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (MAX_BATTERY + 1) + ", " + (NOMINAL_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + DENIED,
            (NOMINAL_BATTERY) + ", " + (MIN_WIND - 1) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + DENIED,
            (NOMINAL_BATTERY) + ", " + (MIN_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (MIN_WIND + 1) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (MAX_WIND - 1) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (MAX_WIND) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (MAX_WIND + 1) + ", " + (NOMINAL_PAYLOAD_WEIGHT) + ", " + DENIED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MIN_PAYLOAD_WEIGHT - 1) + ", " + DENIED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MIN_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MIN_PAYLOAD_WEIGHT + 1) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MAX_PAYLOAD_WEIGHT - 1) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MAX_PAYLOAD_WEIGHT) + ", " + AUTHORIZED,
            (NOMINAL_BATTERY) + ", " + (NOMINAL_WIND) + ", " + (MAX_PAYLOAD_WEIGHT + 1) + ", " + DENIED
    })
    @DisplayName("Decide a missão ao avaliar individualmente condições dentro e fora dos limites operacionais")
    void shouldDecideMissionWhenEachConditionIsInsideOrOutsideOperationalLimits(
            int battery,
            int wind,
            int payloadWeight,
            String expectedDecision) {
        assertEquals(expectedDecision, policy.evaluate(battery, wind, payloadWeight));
    }
}
