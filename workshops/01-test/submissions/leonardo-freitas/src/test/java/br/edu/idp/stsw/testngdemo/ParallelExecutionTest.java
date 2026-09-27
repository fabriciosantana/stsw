package br.edu.idp.stsw.testngdemo;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ParallelExecutionTest {

    @Test(groups = {"parallel-demo"})
    public void transferScenarioA() throws InterruptedException {
        runScenario("A", 2_000.00, 100.00, 1_900.00);
    }

    @Test(groups = {"parallel-demo"})
    public void transferScenarioB() throws InterruptedException {
        runScenario("B", 3_000.00, 500.00, 2_500.00);
    }

    @Test(groups = {"parallel-demo"})
    public void transferScenarioC() throws InterruptedException {
        runScenario("C", 5_000.00, 1_000.00, 4_000.00);
    }

    private void runScenario(
            String scenario,
            double balance,
            double amount,
            double expected
    ) throws InterruptedException {
        String thread = Thread.currentThread().getName();

        System.out.printf(
                "Cenário %s executando na thread: %s%n",
                scenario,
                thread
        );

        // Pequena pausa apenas para tornar o paralelismo visível na demo.
        Thread.sleep(300);

        TransferService service = new TransferService();
        double result = service.transfer(balance, amount);

        Assert.assertEquals(result, expected);
    }
}
