package com.codigo;

public class Calculadora {

    public int soma(int a, int b) {
        return a + b;
    }

    public int subtrai(int a, int b) {
        return a - b;
    }

    public int multiplica(int a, int b) {
        return a * b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Não pode dividir por zero!");
        } 

        return a / b;
    }

    public int potencia(int a, int b) {
        int potencia = 1;
        int i = 1;
        for(i = 0; i < b; i++) {
            potencia = potencia * a;
        }
        return potencia;
    }
}
