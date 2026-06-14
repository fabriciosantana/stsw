package br.com.filipemarraa.seminario;

public class PaymentCalculator {

    public double calculateDiscount(double price, String userType) {
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }

        if (userType == null) {
            return price;
        }

        if (userType.equals("VIP")) {
            return price * 0.8;
        } else if (userType.equals("PREMIUM")) {
            return price * 0.9;
        }

        return price;
    }
}