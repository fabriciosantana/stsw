package br.edu.idp.es.stsw.pyramid.e2e;

import br.edu.idp.es.stsw.pyramid.app.StoreApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Topo da pirâmide — fluxo end-to-end")
class OrderFlowE2ETest {

    @Test
    void deveExecutarFluxoCompletoDeCompra() {
        StoreApplication application = new StoreApplication();

        application.registerProduct(
                100L,
                "Base Fluida",
                149.90,
                10
        );

        double total = application.checkout(100L, 3);

        assertEquals(449.70, total, 0.001);
        assertEquals(7, application.stockOf(100L));
    }
}
