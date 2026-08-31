package br.edu.idp.es.stsw.bva.unit;

final class DroneMissionTestSpecification {

    static final int MIN_BATTERY = 30;
    static final int NOMINAL_BATTERY = 70;
    static final int MAX_BATTERY = 100;

    static final int MIN_WIND = 0;
    static final int NOMINAL_WIND = 20;
    static final int MAX_WIND = 40;

    static final int MIN_PAYLOAD_WEIGHT = 1;
    static final int NOMINAL_PAYLOAD_WEIGHT = 4;
    static final int MAX_PAYLOAD_WEIGHT = 8;

    static final String AUTHORIZED = "AUTORIZADA";
    static final String DENIED = "NEGADA";

    private DroneMissionTestSpecification() {
    }
}
