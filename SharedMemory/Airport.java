package SharedMemory;

import java.util.Random;
import java.util.concurrent.Semaphore;

import Entities.*;

/*
 * 
 * Airport
 * 
 */
public class Airport {
	
	private Repository repository;		//repository of information
	private Plane plane;				//plane region
	private int inq;					//passengers in queue
	private int inf;					//passengers in flight
	private int tob;					//total passengers boarded
	
	
	
	
	public Airport(Plane p, Repository r) {
		repository=r;
		plane=p;
	}
	
/*
 * Informs the hostess that there are passengers in line. Each arriving passenger adds 1
 *  to the Semaphore value, and the hostess subtracts 1 each time a passenger is served.
 *  Finding the Semaphore value 0, the hostess returns to the waiting for passengers state.
 */
	private Semaphore semPgInQueue = new Semaphore(0);
/*
 * Semaphore that simulates a FIFO, acting as a passenger queue.
 *  These, when finding the Semaphore with value 0, are inserted 
 *  in the queue of processes on hold, implemented in class Semaphore.
 *  Every time the hostess is available, he / she answers to the passenger
 *  who has been waiting for the longest time.
 */
	private Semaphore semQueueFIFO = new Semaphore(0);
/*
 * passenger shows id to hostess, So that it can announce its shipment.
 */
	private Semaphore semIdShown = new Semaphore(0);
/*
 * Signals the possibility of starting the shipment. After giving a signal to the pilot that
 * Can raise a flight, hostess waits for the pilot to return, which is indicated
 * By up in this Semaphore.
 */
	private Semaphore semReadyForBoarding = new Semaphore(0);
	/*
	 * Pilot waits signal that he can lift flight. Each time a passenger boards,
	 * The hostess evaluates whether the plane can leave. If yes, inform the pilot 
	 * through the up in this semaphore.
	 */
	
	private Semaphore semReadyToFlight = new Semaphore(0);
	
	private Semaphore semMutex = new Semaphore(1); //semaphore to access to the critical region
	
	
	/*
	 * method to change the semaphore mutex that represent the critical region
	 * @param option "up" to change the mutex to release
	 * 				 "down" to change the mutex to acquire
	 */
	
	public void mutex(String option) throws InterruptedException {
		if (option.equals("up")) {semMutex.release();}
		else if (option.equals("down")) {semMutex.acquire();}
	}
	
	/*
	 * this method change the semaphore of passenger in queue
	 * @param option "up" to change the mutex to release
	 * 				 "down" to change the mutex to acquire
	 */
	
	public void pgInQueue(String option) throws InterruptedException {
		if (option.equals("up")) {semPgInQueue.release();}
		else if (option.equals("down")) {semPgInQueue.acquire();}
	}
	
	
	public void queueFIFO(String option) throws InterruptedException {
		if (option.equals("up")) {semQueueFIFO.release();}
		else if (option.equals("down")) {semQueueFIFO.acquire();}
	}
	
	public void idShown(String option) throws InterruptedException {
		if (option.equals("up")) {semIdShown.release();}
		else if (option.equals("down")) {semIdShown.acquire();}
	}
	
	public void readyForBoarding(String option) throws InterruptedException {
		if (option.equals("up")) {semReadyForBoarding.release();}
		else if (option.equals("down")) {semReadyForBoarding.acquire();}
	}
	
	public void readyToFlight(String option) throws InterruptedException {
		if (option.equals("up")) {semReadyToFlight.release();}
		else if (option.equals("down")) {semReadyToFlight.acquire();}
	}
	
	/*
	 * method used by repository to acquire value of inq from airport
	 * 
	 */
	public int getInQ() {
		return inq;
	}
	
	/*
	 * method used by repository to acquire value of inf from airport
	 * 
	 */
	public int getInF() {
		return inf;
	}
	
	/*
	 * method used by repository to acquire value of tob from airport
	 * 
	 */
	public int getToB() {
		return tob;
	}
	

	/*
	 * method called by the hostess
	 * @param ht hostess
	 */
	public void waitForNextFlight(Hostess ht) throws InterruptedException
	{
		try {
		
	
			
			ht.setHtState(HostessState.WAIT_FOR_FLIGHT);
			mutex("down");
			repository.setHostessState(ht.getHtState());
			mutex("up");
			
			readyForBoarding("down"); //espera que o piloto regresse
			
			inf=0;
			
			
			
		} catch (InterruptedException e) {System.out.println("Erro em waitInQueue.");}
	}
	
	/*
	 * method called by the pilot when is ready to flight
	 * @param pt pilot
	 */
	public void signalReadyForBoarding(Pilot pt) throws InterruptedException 
	{
		try {
			
	
			pt.setPtState(PilotState.READY_FOR_BOARDING);
			
			readyForBoarding("up"); //informa hostess que pode iniciar boarding
			
			mutex("down");
			repository.setPilotState(pt.getPtState());
			repository.incrementFlightN();
			repository.printStarted();
			repository.printStats();
			mutex("up");

		} catch (InterruptedException e) {System.out.println("Erro em signalReadyForBoarding.");}
	}
	
	/*
	 * method called by the hostess
	 * @param ht hostess
	 * 
	 */
	public void signalReadyToFlight(Hostess ht) throws InterruptedException 
	{

		try {

			ht.setHtState(HostessState.READY_TO_FLIGHT);
			
			mutex("down");
			repository.setHostessState(ht.getHtState());
			repository.printStats();
			mutex("up");
			
			readyToFlight("up"); //informa piloto que voo pode partir

		} catch (InterruptedException e) {System.out.println("Erro em signalReadyToFlight.");}	
	}
	
	
	/*
	 * method called by the pilot 
	 * @param pt pilot
	 */
	public void waitUntilReadyToFlight(Pilot pt) throws InterruptedException
	{
		try {

			pt.setPtState(PilotState.WAIT_FOR_BOARDING);
			
			mutex("down");
			repository.setPilotState(pt.getPtState());
			mutex("up");
			
			readyToFlight("down"); //espera que hostess indique que pode levantar voo
	
			
			
		} catch (InterruptedException e) {System.out.println("Erro em waitUntilReadyToFlight.");}
	}
	
	/*
	 * method called by the hostess
	 * @param ht hostess
	 */
	public void waitForPassenger(Hostess ht) throws InterruptedException 
	{

		try {

			ht.setHtState(HostessState.WAIT_FOR_PASSENGER);
			
			mutex("down");
			repository.setHostessState(ht.getHtState());
			repository.printStats();
			mutex("up");
		
			pgInQueue("down"); //espera que cheguem passageiros

			
		} catch (InterruptedException e) {System.out.println("Erro em waitForPassenger.");}		
	}
	
	/*
	 * method called by the hostess
	 * @param ht hostess
	 */
	public void checkPassport(Hostess ht) throws InterruptedException 
	{
		
		try {

			queueFIFO("up"); //atende um passageiro
			
			idShown("down"); //espera que mostrem id
			
			ht.setHtState(HostessState.CHECK_PASSPORT);
			inq--;
			inf++;
			tob++;
			mutex("down");
			repository.setHostessState(ht.getHtState());
			repository.setInQ(inq);
			repository.setInF(inf);
			repository.setToB(tob);
			plane.setInF(inf);
			repository.printBoarded();
			repository.printStats();
			if (inf==10 || (inq==0 && inf>4) || tob==21) { //se for ultimo passageiro no voo, fazer update
				repository.updateFlightList();
			}
			mutex("up");

			
		} catch (InterruptedException e) {System.out.println("Erro em checkPassport.");}	
	}
	
	/*
	 * method called by the hostess to determine if it's the last passenger in the flight
	 * 
	 */
	public boolean lastPgInFlight() {
		boolean res = false;
		
		try {
			mutex("down");
			if (inf==10 || (inq==0 && inf>4) || tob==21) {res=true;}
			else {res=false;}
			mutex("up");
		} catch (InterruptedException e) {System.out.println("Erro em lastPgInFlight.");}
		
		return res;
	}
	
	
	/*
	 * method called by the passenger
	 * @param p passenger
	 */
	public void travelToAirport(Passenger p) throws InterruptedException 
	{
		try {

			//espera entre 1 e 3 segundos
			Random r = new Random();
			Thread.sleep(r.nextInt(200));

		} catch (InterruptedException e) {System.out.println("Erro em travelToAirport.");}
	}
	
	/*
	 *method called by the passenger
	 *@param p passenger 
	 */
	public void waitInQueue(Passenger p) throws InterruptedException 
	{
		try {
	
			pgInQueue("up"); //inform the hostess  there are passengers in the queue
			
			p.setPgState(PassengerState.IN_QUEUE);
			
			mutex("down");
			repository.setPassengerState(p.getPgState(), p.getPgID());
			inq++;
			repository.setInQ(inq);
			repository.printStats();
			mutex("up");
			
			queueFIFO("down"); //process stay in the queue 
			
			p.setPgState(PassengerState.IN_FLIGHT);
			
			mutex("down");
			repository.setPassengerState(p.getPgState(), p.getPgID());
			repository.setLastID(p);
			mutex("up");
			
			idShown("up"); // showed the id
			

		} catch (InterruptedException e) {System.out.println("Erro em waitInQueue.");}
	}
	
}
