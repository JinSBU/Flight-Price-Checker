package flight;

//Each instance of this class represents a flight option given by Momondo
public class Trip {
    static String price;
    static Flight departureFlight, returnFlight;
    public Trip(Flight departureFlight, Flight returnFlight, String price){
        this.departureFlight = departureFlight;
        this.returnFlight = returnFlight;
        this.price = price;
    }
    public static void printStats(){
        departureFlight.printStats();
        returnFlight.printStats();
        System.out.println(price + "\n");


    }
}
