package flight;

//Each instance of this class represents a flight option given by Momondo
public class Trip {
    String price;
    Flight departureFlight, returnFlight;
    public Trip(Flight departureFlight, Flight returnFlight, String price){
        this.departureFlight = departureFlight;
        this.returnFlight = returnFlight;
        this.price = price;
    }
}
