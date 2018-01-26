package MainAirlift;

import java.io.IOException;

import Entities.*;
import SharedMemory.*;

/*
 * Simulation of aerial bridge
 * this program is constituted by 1 pilot, 1 hostess and 21 passengers 
 */
public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		
		//instanciar regioes partilhadas
		
		Repository repository = new Repository();
		Plane plane = new Plane(repository);	
		Airport airport = new Airport(plane, repository);
		Destination destination = new Destination(airport, repository, plane);
		
		repository.printHeader();
		
		Thread[] pgArray = new Thread[21]; //array que guarda os processos passenger
			
		Thread hostess = new Hostess(airport); //criar a hostess
		Thread pilot = new Pilot(plane, airport, destination); //criar o pilot
		
		for (int i=0;i<21;i++) {
			pgArray[i] = new Passenger(i, plane, airport); //criar os passengers e atribuir id
			}
	
		//iniciar todos os processos
		for (int i=0;i<21;i++) {
			pgArray[i].start();
			}
		pilot.start();
		hostess.start();
		
		
		//airlift terminou, apresentar resultados
		
		pilot.join();
		hostess.join();
		for (int i=0;i<21;i++) {
			pgArray[i].join();
			}
		
		repository.printResults();
		
	}
}
