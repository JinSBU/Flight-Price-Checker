package flight;

//Each instance of this class represents a flight option given by Momondo
public class Trip {
    int price;
    Flight departureFlight, returnFlight;
    public Trip(Flight departureFlight, Flight returnFlight, int price){
        this.departureFlight = departureFlight;
        this.returnFlight = returnFlight;
        this.price = price;
    }
}
