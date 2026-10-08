package br.edu.idp.stsw.testngdemo;

/**
 * Serviço simples usado na demonstração do seminário de TestNG.
 *
 * Regras:
 * - valor mínimo de transferência: R$ 10,00;
 * - valor máximo de transferência: R$ 10.000,00;
 * - o valor não pode ultrapassar o saldo disponível.
 */
public class TransferService {

    public static final double MIN_AMOUNT = 10.00;
    public static final double MAX_AMOUNT = 10_000.00;

    public boolean isValid(double balance, double amount) {
        if (balance < 0) {
            throw new IllegalArgumentException("Saldo não pode ser negativo");
        }

        return amount >= MIN_AMOUNT
                && amount <= MAX_AMOUNT
                && amount <= balance;
    }

    public double transfer(double balance, double amount) {
        if (!isValid(balance, amount)) {
            throw new IllegalArgumentException("Transferência inválida");
        }

        return balance - amount;
    }
}
