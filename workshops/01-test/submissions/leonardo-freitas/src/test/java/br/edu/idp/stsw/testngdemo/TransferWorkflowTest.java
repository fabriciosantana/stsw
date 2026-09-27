package br.edu.idp.stsw.testngdemo;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Demonstra dependências entre métodos de teste.
 *
 * O objetivo aqui é didático: em suítes reais, dependências devem ser
 * usadas com cuidado para não criar testes excessivamente acoplados.
 */
public class TransferWorkflowTest {

    private final AtomicBoolean authenticated = new AtomicBoolean(false);
    private final AtomicBoolean transferCompleted = new AtomicBoolean(false);

    @Test(groups = {"workflow"})
    public void authenticate() {
        authenticated.set(true);

        Assert.assertTrue(authenticated.get());
    }

    @Test(
            groups = {"workflow"},
            dependsOnMethods = "authenticate"
    )
    public void executeTransfer() {
        Assert.assertTrue(
                authenticated.get(),
                "O usuário deve estar autenticado antes da transferência"
        );

        TransferService service = new TransferService();
        double balance = service.transfer(1_000.00, 100.00);

        Assert.assertEquals(balance, 900.00, 0.001);
        transferCompleted.set(true);
    }

    @Test(
            groups = {"workflow"},
            dependsOnMethods = "executeTransfer"
    )
    public void logout() {
        Assert.assertTrue(
                transferCompleted.get(),
                "A transferência deve terminar antes do logout"
        );

        authenticated.set(false);

        Assert.assertFalse(authenticated.get());
    }
}
