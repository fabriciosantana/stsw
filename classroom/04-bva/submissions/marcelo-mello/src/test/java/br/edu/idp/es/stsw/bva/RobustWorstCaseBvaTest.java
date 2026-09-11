package br.edu.idp.es.stsw.bva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Robust Worst-Case BVA (7³ = 343 casos)")
class RobustWorstCaseBvaTest {

    static final int[] BATERIA    = {29, 30, 31, 70, 99, 100, 101};
    static final int[] VENTO      = {-1,  0,  1, 20, 39,  40,  41};
    static final int[] PESO_CARGA = { 0,  1,  2,  4,  7,   8,   9};

    static boolean bateriaValida(int v)   { return v >= 30 && v <= 100; }
    static boolean ventoValido(int v)     { return v >= 0  && v <= 40;  }
    static boolean cargaValida(int v)     { return v >= 1  && v <= 8;   }

    static Stream<Object[]> combinacoes() {
        Stream.Builder<Object[]> builder = Stream.builder();
        for (int bat : BATERIA)
            for (int ven : VENTO)
                for (int carga : PESO_CARGA) {
                    boolean autorizada = bateriaValida(bat)
                            && ventoValido(ven)
                            && cargaValida(carga);
                    builder.add(new Object[]{bat, ven, carga,
                            autorizada ? "AUTORIZADA" : "NEGADA"});
                }
        return builder.build();
    }

    @ParameterizedTest(name = "bat={0}, vento={1}, carga={2} → {3}")
    @MethodSource("combinacoes")
    @DisplayName("Todas as 343 combinações robustas")
    void avaliaTodasCombinacoes(int bat, int vento, int carga, String esperado) {
        assertEquals(esperado,
                DroneMissionPolicy.evaluate(bat, vento, carga),
                String.format("Falhou para bat=%d, vento=%d, carga=%d", bat, vento, carga));
    }
}
