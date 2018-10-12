package flight;

/**
 *
 * @author jinth
 */
public class Flight {
    String airline;
    String departureAirportCode, arrivalAirportCode;
    String departureTime, arrivalTime;
    boolean layover = false;
    String totalTravelTime;

    public void printStats(){
        System.out.println("Airline: " + airline + " \nDeparture Airport Code: " + departureAirportCode + " \nDeparture Time: " + departureTime +
                "\nAirport Arrival Code: " + arrivalAirportCode + " \nAirport Arrival TIme: " + arrivalTime + "\nTotal Travel TIme: " + totalTravelTime + "\nLayover: " + layover);
    }
}
