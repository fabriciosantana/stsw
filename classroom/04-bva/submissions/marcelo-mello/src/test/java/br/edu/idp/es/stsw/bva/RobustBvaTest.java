package br.edu.idp.es.stsw.bva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BVA Robusto (6n+1 = 19 casos)")
class RobustBvaTest {

    @ParameterizedTest(name = "nominal: bat={0}, vento={1}, carga={2} → {3}")
    @CsvSource({
        "70, 20, 4, AUTORIZADA"
    })
    @DisplayName("Caso nominal")
    void nominal(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }

    @ParameterizedTest(name = "bateria={0} → {3}")
    @CsvSource({
        "29, 20, 4, NEGADA",
        "30, 20, 4, AUTORIZADA",
        "31, 20, 4, AUTORIZADA",
        "99, 20, 4, AUTORIZADA",
        "100, 20, 4, AUTORIZADA",
        "101, 20, 4, NEGADA"
    })
    @DisplayName("BVA Robusto – bateria")
    void variaBateria(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }

    @ParameterizedTest(name = "vento={1} → {3}")
    @CsvSource({
        "70, -1, 4, NEGADA",
        "70,  0, 4, AUTORIZADA",
        "70,  1, 4, AUTORIZADA",
        "70, 39, 4, AUTORIZADA",
        "70, 40, 4, AUTORIZADA",
        "70, 41, 4, NEGADA"
    })
    @DisplayName("BVA Robusto – vento")
    void variaVento(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }

    @ParameterizedTest(name = "carga={2} → {3}")
    @CsvSource({
        "70, 20, 0, NEGADA",
        "70, 20, 1, AUTORIZADA",
        "70, 20, 2, AUTORIZADA",
        "70, 20, 7, AUTORIZADA",
        "70, 20, 8, AUTORIZADA",
        "70, 20, 9, NEGADA"
    })
    @DisplayName("BVA Robusto – pesoCarga")
    void variaCarga(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }
}
