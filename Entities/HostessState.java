package Entities;

/*
 * 
 */

public enum HostessState {
	WAIT_FOR_FLIGHT("WTFL"),WAIT_FOR_PASSENGER("WTPS"),CHECK_PASSPORT("CKPS"),READY_TO_FLIGHT("RDTF");
	
	private String value;						//value of hostess status

	
    private HostessState(String value) {
        this.value = value;
    }
    
    /*
     *	@return the string of the value 
     */
    public String toString() {
        return value;
    }

}
