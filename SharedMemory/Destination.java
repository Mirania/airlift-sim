package SharedMemory;

import java.util.concurrent.Semaphore;

import Entities.*;

/*
 * 
 */

public class Destination 
{
	
	private Repository repository;					//repository of information
	private Plane plane;							//plane- shared memory
	private Airport airport;						//airport region
	
	
	private Semaphore semMutex = new Semaphore(1);	//semaphore of the critical region
	

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
	 * @param r repository information 
	 * @param p plane critical region
	 */
	public Destination(Airport a, Repository r, Plane p) {
		airport=a;
		repository=r;
		plane=p;
	}
	
	/*
	 * this method is called by the pilot when you drop a passenger
	 * @param pt pilot 
	 */
	public void dropPassengersAtTarget(Pilot pt)	throws InterruptedException
	{
		try {
			pt.setPtState(PilotState.DROPING_PASSENGERS);
			
			int inf;
			
			mutex("down");
			repository.setPilotState(pt.getPtState());
			repository.printArrived();
			inf=airport.getInF();
			mutex("up");
			
			for (int i=0;i<inf;i++) {
				plane.planeFIFO("up"); //Informs passengers that the flight has ended
				
				plane.pgDropped("down"); //Expects passengers to leave
				
				mutex("down");
				repository.printStats();
				mutex("up");
			}
			
		} catch (InterruptedException e) {System.out.println("Erro no dropPassengersAtTarget");}
	}
	
	
	/*
	 * this method is called by the pilot 
	 * 
	 */
	public boolean isFinished() throws InterruptedException
	{
		int tob=-1;
		
		try {
			mutex("down");		
			tob=airport.getToB();		
			mutex("up");
			
		} catch (InterruptedException e) {System.out.println("Erro no isFinished");}
		
		if (tob==-1) {System.out.println("Erro no isFinished");System.exit(1);}
		
		if (tob==21) {return true;} else return false;
	}
}
