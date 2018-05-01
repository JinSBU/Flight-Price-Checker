package flight;

/**
 *
 * @author jinth
 */
public class Flight {
    String airline;
    String departureAirportCode, arrivalAirportCode;
    String departureAirport, arrivalAirport;
    String departureTime, arrivalTime;
    boolean layover = false;
    String totalTravelTime;

    public void printStats(){
        System.out.println(airline + " " + departureAirportCode + " " + departureAirport + " " + departureTime +
                "\n" + arrivalAirportCode + " " + arrivalAirportCode + " " + arrivalAirport + " " + arrivalTime + "\n" + totalTravelTime);
    }
}
