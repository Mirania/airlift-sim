package Entities;

import SharedMemory.*;

/*
 * 
 */
public class Pilot extends Thread
{
	private PilotState state;						//pilot state
	
	
	private Plane plane;							//plane-shared memory							
	private Airport airport;						//aiport-shared memory
	private Destination destination;				//destination-shared memory
	
	
	/*
	 *Sets the pilot state
	 *@param state is the pilot state  
	 */
	public void setPtState(PilotState state) {
		this.state=state;
	}
	
	/*
	 *@return   the pilot state
	 */
	
	public PilotState getPtState() {
		return state;
	}
	
	/*
	 * @param r repository of information
	 * @param p plane 
	 * @param destination
	 */
	public Pilot(Plane p, Airport a, Destination d) {
		plane=p;
		airport=a;
		destination=d;
	}
	
	//life cycle of this pilot
	@Override
	public void run() {
		try 
		{
			while(!destination.isFinished()) {
		        plane.flight(this, false); // from target to origin
		        airport.signalReadyForBoarding(this);
		        airport.waitUntilReadyToFlight(this);
		        plane.flight(this, true); // from origin to target
		        destination.dropPassengersAtTarget(this);
			}	
		} catch (InterruptedException e) {System.out.println("Erro no ciclo de vida do pilot.");}
		

		
		
	}
	

}
