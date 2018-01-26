package Entities;
/*
 * 
 */
public enum PilotState {
	FLYING_BACK("FLBK"),READY_FOR_BOARDING("RDFB"),WAIT_FOR_BOARDING("WTFB"),FLYING("FLFW"),DROPING_PASSENGERS("DPPS");
	
	private String value;

    private PilotState(String value) {
        this.value = value;
    }
    /*
   	 * @return the string of the value 
   	 */
    public String toString() {
        return value;
    }
}
