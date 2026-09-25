package br.edu.idp.es.stsw.bva;

import br.edu.idp.es.stsw.bva.DroneMissionPolicy;

public class Main {
    public static void main(String[] args) {
        DroneMissionPolicy droneMission = new DroneMissionPolicy();
        String valor = droneMission.evaluate(10, 30, 5);
        System.out.println(valor);
    }
}