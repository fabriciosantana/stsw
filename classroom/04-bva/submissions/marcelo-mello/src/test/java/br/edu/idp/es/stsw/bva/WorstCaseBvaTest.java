package br.edu.idp.es.stsw.bva;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Worst-Case BVA (5³ = 125 casos)")
class WorstCaseBvaTest {

    static final int[] BATERIA    = {30, 31, 70, 99, 100};
    static final int[] VENTO      = { 0,  1, 20, 39,  40};
    static final int[] PESO_CARGA = { 1,  2,  4,  7,   8};

    static Stream<int[]> combinacoes() {
        Stream.Builder<int[]> builder = Stream.builder();
        for (int bat : BATERIA)
            for (int ven : VENTO)
                for (int carga : PESO_CARGA)
                    builder.add(new int[]{bat, ven, carga});
        return builder.build();
    }

    @ParameterizedTest(name = "bat={0}, vento={1}, carga={2} → AUTORIZADA")
    @MethodSource("combinacoes")
    @DisplayName("Todas as combinações válidas devem ser AUTORIZADA")
    void todasDevemSerAutorizadas(int[] params) {
        assertEquals("AUTORIZADA",
                DroneMissionPolicy.evaluate(params[0], params[1], params[2]),
                String.format("Esperava AUTORIZADA para bat=%d, vento=%d, carga=%d",
                        params[0], params[1], params[2]));
    }
}
