package Entities;

import SharedMemory.*;

/*
 * 
 */



public class Hostess extends Thread
{
	private HostessState state; 				// Hostess State 
	private int nPassengers=0;				// Number of passanger
	
	private Airport airport;				// Airport-Shared memory
	
	
	/*
	 * @param  repository this is the first parameter to acess the Repository
	 * @param  airport this is the second parameter to access to the shared memory Airport   
	 */
	public Hostess(Airport airport) {
		this.airport=airport;
	}
	
	/*
	 *Sets the hostess state
	 *@param state is the hostess state  
	 */
	public void setHtState(HostessState state) {
		this.state=state;
	}

	/*
	 *@return   the hostess state
	 */
	public HostessState getHtState() {
		return state;
	}
	
	//ciclo de vida
	@Override
	public void run() {
		
		try {
			while(nPassengers < 21 ) {
		        airport.waitForNextFlight(this);
		        do { 
		            airport.waitForPassenger(this);
		            airport.checkPassport(this);
		            nPassengers++;
		        } while (!airport.lastPgInFlight());
		        airport.signalReadyToFlight(this);
		    }
		} catch (InterruptedException e) {System.out.println("Erro no ciclo de vida da hostess.");}
		
	}

}
