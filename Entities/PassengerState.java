package Entities;
/*
 * 
 */
public enum PassengerState {
	GOING_TO_AIRPORT("GTAP"),IN_QUEUE("INQE"),IN_FLIGHT("INFL"),AT_DESTINATION("ATDS");
	
	private String value;				//value of the passenger status
	
    private PassengerState(String value) {
        this.value = value;
    }
    /*
	 * @return the string of the value
	 */
    public String toString() {
        return value;
    }
}
