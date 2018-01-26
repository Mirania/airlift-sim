package SharedMemory;

import java.util.concurrent.Semaphore;

import Entities.*;

/*
 * 
 * Plane- shared memory
 */
public class Plane {
	
	/* 
	 * a semaphore used to simulate a FIFO, keeping the order in which the
	 * passengers boarded. This order is the same as that in which passengers
	 * arrived at the airport. When finding this semaphore with value 0, the
	 * passengers enter the queue of processes until they are informed that the flight
	 * finished, when they leave the plane one by one.
	*/
	private Semaphore semPlaneFIFO = new Semaphore(0);
	/*
	 * passenger that leaves execute "down", informs pilot that the act was successful,
	 * printing the state of the airlift. the pilot execute an "up",So that the next passenger in the queue can leave
	 */
	
	
	private Semaphore semPgDropped = new Semaphore(0);
	
	private Semaphore semMutex = new Semaphore(1);			//semaphore to access the critical region
	
	private Repository repository;							//repository of information
	
	private int inf;										//passengers in flight
	
	
	/*
	 * @param r repository of information
	 */
	public Plane(Repository r) {
		repository=r;
	}
	
	/*
	 * this method access to the semaphore of the critical region
	 * @param option string, if option="up" the semaphore is released
	 * 						 if option="down" the semaphore is bloqued
	 */
	
	public void mutex(String option) throws InterruptedException {
		if (option.equals("up")) {semMutex.release();}
		else if (option.equals("down")) {semMutex.acquire();}
	}
	
	
	public void pgDropped(String option) throws InterruptedException {
		if (option.equals("up")) {semPgDropped.release();}
		else if (option.equals("down")) {semPgDropped.acquire();}
	}
	
	public void planeFIFO(String option) throws InterruptedException {
		if (option.equals("up")) {semPlaneFIFO.release();}
		else if (option.equals("down")) {semPlaneFIFO.acquire();}
	}
	
	/*
	 * method used by plane to acquire value of inf from airport
	 * 
	 */
	public void setInF(int inf) {
		this.inf=inf;
	}

	/*
	 * method flight is called by the pilot when is flying to destination or flying back 
	 * @param pt pilot 
	 * @param toDestination is true if is flying to destination 
	 */
	
	public void flight(Pilot pt, boolean toDestination) throws InterruptedException
	{
		try {
			if (toDestination) {pt.setPtState(PilotState.FLYING);}
			else {pt.setPtState(PilotState.FLYING_BACK);}
			
			mutex("down");
			repository.setPilotState(pt.getPtState());
			repository.printDeparture(toDestination);
			repository.printStats();
			mutex("up");
			
		} catch (InterruptedException e) {System.out.println("Erro em flight.");}	
	}
	
	/*
	 * this method is called by the passenger
	 * @param passenger
	 */
	
	public void waitUntilDestination(Passenger p)	
	{
		try {

			planeFIFO("down"); //espera que o voo termine
			
			p.setPgState(PassengerState.AT_DESTINATION);
			
			mutex("down");
			repository.setPassengerState(p.getPgState(), p.getPgID());
			inf--;
			repository.setInF(inf);
			mutex("up");
			
			pgDropped("up"); //informa ao piloto que saiu

		} catch (InterruptedException e) {System.out.println("Erro em waitUntilDestination.");}	
	}	
	
	
	
	

}
