package br.edu.idp.es.stsw.bva;

import java.util.Scanner;

import br.edu.idp.es.stsw.bva.domain.DroneMissionPolicy;

public class DroneMissionApp {
    
    public static void main(String[] args){

        try(Scanner input = new Scanner(System.in)){
            System.out.println("Sistema de autorização de missão de drones.");

            while (true) { 
                System.out.print("Bateria: ");
                int bateria = input.nextInt();
                System.out.print("Vento: ");
                int vento = input.nextInt();
                System.out.print("Carga: ");
                int peso = input.nextInt();

                DroneMissionPolicy droneMission = new DroneMissionPolicy();
                System.out.println(droneMission.evaluate(bateria, vento, peso));
            }
        } catch(Exception ex){
            System.out.printf("Erro: %s", ex.getMessage());
        }
        
    }

}
