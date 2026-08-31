package br.edu.idp.es.stsw.bva.domain;

public class DroneMissionPolicy {

    public String evaluate(int battery, int wind, int payloadWeight) {
        boolean batteryIsSafe = isInside(battery, 30, 100);
        boolean windIsSafe = isInside(wind, 0, 40);
        boolean payloadWeightIsSafe = isInside(payloadWeight, 1, 8);

        if (batteryIsSafe && windIsSafe && payloadWeightIsSafe) {
            return "AUTORIZADA";
        }

        return "NEGADA";
    }

    private boolean isInside(int value, int min, int max) {
        return value >= min && value <= max;
    }
}
