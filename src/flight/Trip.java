package flight;

//Each instance of this class represents a flight option given by Momondo
public class Trip {
    static String price;
    static Flight departureFlight, returnFlight;
    static String departDate, returnDate, url;
    public Trip(Flight departureFlight, Flight returnFlight, String price, String departDate, String returnDate, String url){
        this.departureFlight = departureFlight;
        this.returnFlight = returnFlight;
        this.price = price;
        this.departDate = departDate;
        this.returnDate = returnDate;
        this.url = url;
    }
    public static void printStats(){
        departureFlight.printStats();
        returnFlight.printStats();
        System.out.println(price+ "\nDepart Date: " + departDate  + "\nReturn Date: " + returnDate + "\n" + url + "\n");
    }
    public static int getPrice(){
        return Integer.parseInt(price);
    }
}
