package Entities;

import SharedMemory.*;

/*
 * 
 */
public class Passenger extends Thread 
{
	private int id;								//passenger identification number
	private PassengerState state;				//Identification of passenger status
	
	
	private Plane plane;						// Plane-shared memory
	private Airport airport;					// Airport-Shared memory
	
	
	/*
	 * this method receives:
	 * @param id integer value  that identifies the number of the passenger
	 * @param repository to access the repository information
	 * @param plane to access the shared memory plane
	 * @param airport to access the shared memory airport
	 */
	public Passenger(int id, Plane plane, Airport airport)
	{
		this.id=id;
		this.plane=plane;
		this.airport=airport;
	}
	
	/*
	 * @return the passenger identification number
	 */
	public int getPgID() {
		return id;
	}
	
	/*
	 * Sets the state of this passenger
	 * @param  state the state to set
	 */
	
	public void setPgState(PassengerState state) {
		this.state=state;
	}
	/*
	 * @return the passenger state
	 */
	
	public PassengerState getPgState() {
		return state;
	}
	
	/*
	 *@return  the string with the identification of passenger 
	 */
	
	public String toString() {
		return "Debug: P"+id;
	}
	
	
	/*
	 * Life cycle of passeger
	 * 
	 */
	@Override
	public void run() 
	{
		try 
		{
			airport.travelToAirport(this);				
			airport.waitInQueue(this);					
			plane.waitUntilDestination(this);			
				
		} catch (InterruptedException e) {System.out.println("Erro no ciclo de vida do passenger.");}
}
	
}
