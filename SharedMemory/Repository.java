package SharedMemory;

import java.util.concurrent.Semaphore;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import Entities.*;

/*
 * 
 * Repository of information
 * 
 */
public class Repository {
	
	//formato: PT HT P0 P1 ... P19 P20 InQ InF ToB
	private PilotState pt;
	private HostessState ht;
	private PassengerState[] pg = new PassengerState[21];
	private int inq, inf, tob;
	
	private int lastID; //para o embarque
	private int flightN=0; //numero de voos
	private int flightList[] = new int[5]; //passageiros por voo

	// semaforo para acesso a regiao critica
	private Semaphore semMutex = new Semaphore(1);
	
	private static File file; //ficheiro destino
	private static PrintWriter printWriter; //escreve no log.txt
	
	//metodos utilitarios
	
	public Repository() throws IOException {
		try {
			//inicializar estados
			pt = PilotState.FLYING_BACK;
			ht = HostessState.WAIT_FOR_FLIGHT;
			for (int i=0;i<21;i++) {
				pg[i] = PassengerState.GOING_TO_AIRPORT;
			}
			
			file = new File("log.txt");
			file.createNewFile(); //cria se nao existir
			printWriter = new PrintWriter(file);
		} catch (IOException e) {System.out.println("Erro ao gerar log.txt.");System.exit(1);}
	}
	
	public void mutex(String option) throws InterruptedException {
		if (option.equals("up")) {semMutex.release();}
		else if (option.equals("down")) {semMutex.acquire();}
	}
	
	public void updateFlightList() throws InterruptedException {
		try {
			mutex("down");
			flightList[flightN-1] = inf;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no updateFlightList");}
	}
	
	//metodos para print
	
	public void printHeader() throws InterruptedException {
		try {
			mutex("down");
			String title = "Air Lift - Description of the internal state";
			System.out.printf("%86s",title);
			printWriter.printf("%86s",title);
			System.out.print("\n\n PT   HT  ");
			printWriter.print("\n\n PT   HT  ");
			for (int i=0;i<21;i++) {
				String pAux;
				if (i<10) {pAux="P0"+i;} else {pAux="P"+i;};
				System.out.printf("%4s ",pAux);
				printWriter.printf("%4s ",pAux);
			}
			System.out.print("InQ InF PTAL\n");
			printWriter.print("InQ InF PTAL\n");
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printHeader");}
		
	}
	
	public void printStats() throws InterruptedException {
		try {
			mutex("down");
			System.out.printf("%s %s ",pt,ht);
			printWriter.printf("%s %s ",pt,ht);
			for (int i=0;i<21;i++) {
				System.out.printf("%s ",pg[i]);
				printWriter.printf("%s ",pg[i]);
			}
			System.out.printf(" %2s  %2s  %2s\n",inq,inf,tob);
			printWriter.printf(" %2s  %2s  %2s\n",inq,inf,tob);
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printStats");}
	}
	
	public void printBoarded() {
		try {
			mutex("down");
			if (lastID<10) {
				System.out.println("\nFlight "+flightN+" : Passenger 0"+lastID+" boarded");
				printWriter.println("\nFlight "+flightN+" : Passenger 0"+lastID+" boarded");
				}
			else {
				System.out.println("\nFlight "+flightN+" : Passenger "+lastID+" boarded");
				printWriter.println("\nFlight "+flightN+" : Passenger "+lastID+" boarded");
				}
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printBoarded");}	
		
	}
	
	public void printDeparture(boolean toDestination) throws InterruptedException {
		try {
			mutex("down");
			if (!toDestination) {
				if (flightN!=0) { //nao imprime mensagens antes de se iniciar o primeiro boarding
					System.out.println("\nFlight "+flightN+" : Returning");
					printWriter.println("\nFlight "+flightN+" : Returning");
					}
			} else {
				System.out.print("\nFlight "+flightN+" : Departed with "+inf+" passenger");
				printWriter.print("\nFlight "+flightN+" : Departed with "+inf+" passenger");
				if (inf>1) {System.out.print("s");printWriter.print("s");}
				System.out.println();
				printWriter.println();
				}
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printDeparture");}
	}
	
	public void printStarted() {
		try {
			mutex("down");
			System.out.println("\nFlight "+flightN+" : Boarding Started");
			printWriter.println("\nFlight "+flightN+" : Boarding Started");
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printStarted");}
	}
	
	public void printArrived() {
		try {
			mutex("down");
			System.out.println("\nFlight "+flightN+" : Arrived");
			printWriter.println("\nFlight "+flightN+" : Arrived");
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printArrived");}
	}
	
	public void printResults() {
		try {
			mutex("down");
			System.out.println("\nAirlift result");
			printWriter.println("\nAirlift result");
			System.out.println("Airlift used "+(flightN)+" flights");
			printWriter.println("Airlift used "+(flightN)+" flights");
			
			for (int i=0;i<flightN;i++) {
				System.out.println("Flight "+(i+1)+" took "+flightList[i]+" passengers");
				printWriter.println("Flight "+(i+1)+" took "+flightList[i]+" passengers");
			}
			
			System.out.println("\nLegend:");
			System.out.println("PT - Pilot state");
			System.out.println("HT - Hostess state");
			System.out.println("P## - Passenger ## state");
			System.out.println("InQ - Number of passengers in queue");
			System.out.println("InF - Number of passengers in flight");
			System.out.println("PTAL - Total number of passengers that already performed boarding");
			
			printWriter.println("\nLegend:");
			printWriter.println("PT - Pilot state");
			printWriter.println("HT - Hostess state");
			printWriter.println("P## - Passenger ## state");
			printWriter.println("InQ - Number of passengers in queue");
			printWriter.println("InF - Number of passengers in flight");
			printWriter.println("PTAL - Total number of passengers that already performed boarding");
			
			printWriter.close();
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no printResults");}
	}
	
	//seguem-se os setters
	
	public void setInQ(int inq) {
		try {
			mutex("down");
			this.inq=inq;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setInQ");}
	}
	
	public void setInF(int inf) {
		try {
			mutex("down");
			this.inf=inf;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setInF");}
	}
	
	public void setToB(int tob) {
		try {
			mutex("down");
			this.tob=tob;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setToB");}
	}
	
	public void incrementFlightN() throws InterruptedException {
		try {
			mutex("down");
			flightN++;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no incrementFlightN");}
	}
	
	public void setPilotState(PilotState state) throws InterruptedException {
		try {
			mutex("down");
			pt=state;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setPiloState");}
	}
	
	public void setHostessState(HostessState state) throws InterruptedException {
		try {
			mutex("down");
			ht=state;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setHostessState");}
	}
	
	public void setPassengerState(PassengerState state, int id) throws InterruptedException {
		try {
			mutex("down");
			pg[id]=state;
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setPassengerState");}
		
	}
	
	public void setLastID(Passenger p) throws InterruptedException {
		try {
			mutex("down");
			lastID = p.getPgID();
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro no setLastID");}
		
	}

}
