package br.edu.idp.es.stsw.bva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BVA Normal (4n+1 = 13 casos)")
class NormalBvaTest {

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
            "30, 20, 4, AUTORIZADA",
            "31, 20, 4, AUTORIZADA",
            "99, 20, 4, AUTORIZADA",
            "100, 20, 4, AUTORIZADA"
    })
    @DisplayName("BVA Normal – bateria")
    void variaBateria(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }

    @ParameterizedTest(name = "vento={1} → {3}")
    @CsvSource({
            "70,  0, 4, AUTORIZADA",
            "70,  1, 4, AUTORIZADA",
            "70, 39, 4, AUTORIZADA",
            "70, 40, 4, AUTORIZADA"
    })
    @DisplayName("BVA Normal – vento")
    void variaVento(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }

    @ParameterizedTest(name = "carga={2} → {3}")
    @CsvSource({
            "70, 20, 1, AUTORIZADA",
            "70, 20, 2, AUTORIZADA",
            "70, 20, 7, AUTORIZADA",
            "70, 20, 8, AUTORIZADA"
    })
    @DisplayName("BVA Normal – pesoCarga")
    void variaCarga(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado, DroneMissionPolicy.evaluate(bat, vento, carga));
    }
}
