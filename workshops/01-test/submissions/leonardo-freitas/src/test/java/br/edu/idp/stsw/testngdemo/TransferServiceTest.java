package br.edu.idp.stsw.testngdemo;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TransferServiceTest {

    private TransferService service;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        service = new TransferService();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        service = null;
    }

    @Test(groups = {"smoke", "regression"})
    public void shouldAcceptAValidTransfer() {
        boolean result = service.isValid(5_000.00, 1_000.00);

        Assert.assertTrue(result);
    }

    @Test(groups = {"regression"})
    public void shouldRejectTransferGreaterThanBalance() {
        boolean result = service.isValid(500.00, 1_000.00);

        Assert.assertFalse(result);
    }

    @Test(groups = {"regression"})
    public void shouldUpdateBalanceAfterTransfer() {
        double remainingBalance = service.transfer(5_000.00, 1_000.00);

        Assert.assertEquals(remainingBalance, 4_000.00);
    }

    @DataProvider(name = "boundaryValues")
    public Object[][] boundaryValues() {
        return new Object[][]{
                {9.00, false},
                {10.00, true},
                {11.00, true},
                {9_999.00, true},
                {10_000.00, true},
                {10_001.00, false}
        };
    }

    @Test(
            dataProvider = "boundaryValues",
            groups = {"regression", "bva"}
    )
    public void shouldValidateBoundaryValues(
            double amount,
            boolean expected
    ) {
        boolean result = service.isValid(20_000.00, amount);

        Assert.assertEquals(
                result,
                expected,
                "Resultado inesperado para o valor: " + amount
        );
    }

    @DataProvider(name = "equivalencePartitions")
    public Object[][] equivalencePartitions() {
        return new Object[][]{
                {-50.00, false},
                {5.00, false},
                {500.00, true},
                {5_000.00, true},
                {15_000.00, false}
        };
    }

    @Test(
            dataProvider = "equivalencePartitions",
            groups = {"regression", "ecp"}
    )
    public void shouldValidateEquivalencePartitions(
            double amount,
            boolean expected
    ) {
        Assert.assertEquals(
                service.isValid(20_000.00, amount),
                expected
        );
    }
}
