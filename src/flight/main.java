package flight;

import java.util.Collections;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Jin Zhen
 * This java program allows you to search Momondo.com for flights and looks for the optimal traveling period for cheapest travels.
 * At this point, need to open the Momondo on a Chrome before running
 */
public class main {
    
    public static WebDriver driver;
    public static void main(String[] args)throws InterruptedException{
        String departDate = "26-06-2018";
        String returnDate = "06-07-2018";
        String from = "SEL";
        String to = "NYC";
        // date is in the form of DD-MM-YYYY
        String includeNearbyAirports = "true";
        
        //These few lines bypasses the checks set by Momondo that disables automated testing
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches",Collections.singletonList("enable-automation"));    
        
        
        
        
  
//        try{
//            //need to find out link for round trip.
//            //https://www.expedia.com/Flights-Search?trip=oneway&leg1=from:nyc,to:mia,departure:06/21/2018TANYT&passengers=adults:1,children:0,seniors:0,infantinlap:Y&options=cabinclass%3Aeconomy&mode=search&origref=www.expedia.com
//            doc = Jsoup.connect("https://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + from + "&SD0=" + to
//                    + "&SDP0=" + departDate + "&SO1=" + to + "&SD1=" + from + "&SDP1=" + returnDate + "&AD=1&TK=ECO&DO=false&NA=" +includeNearbyAirports + "&currency=USD").get();
        System.setProperty("webdriver.chrome.driver", "C:/Users/jinth/Desktop/flights/chromedriver.exe");
        driver = new ChromeDriver(options);
        String url = ("https://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + from + "&SD0=" + to
                + "&SDP0=" + departDate + "&SO1=" + to + "&SD1=" + from + "&SDP1=" + returnDate + "&AD=1&TK=ECO&DO=false&NA=" +includeNearbyAirports + "&currency=USD");
        
        driver.navigate().to(url);
        waitForJavascript(driver);
        print("Finished loading");
        //Below this point means all the prices have been loaded. 
        /* Currently have several options to store prices
            can store into a DB and compare everyday to see if prices are steals or have lowered        *this could be a separate project*
            can have GUI and show lowest price in a month. THIS IS WHAT WILL BE WORKED ON
        */
        
        generateResults();
        
        
    }
    public static void print(String string){
        System.out.println(string);
    }
    /**
     * Compares the DOM periodically and compares the results to see if html on page is stabilized.
     */
    public static void waitForJavascript(WebDriver chrome) throws InterruptedException {
        print("Loading page...");
        WebDriverWait wait = new WebDriverWait(chrome, 50);
        wait.until(ExpectedConditions.textToBePresentInElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]"), "Search complete"));
        // got it to allow javascript to run first. 

    }
    
    /**
     * This method will generate a Trip instance for the cheapest value flight and best valued flight
     */
    public static void generateResults(){
        //First, need to create a flight instance for departure and one for return (Cheapest flight)
        Flight departureFlight = new Flight();
        Flight returnFlight = new Flight();
        String cheapestPath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/ol[1]";

        insertStats(departureFlight, "1", cheapestPath);
        insertStats(returnFlight, "2", cheapestPath);
        String pricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/a[1]/span[1]";
        String price = driver.findElement(By.xpath(pricePath)).getAttribute("innerHTML").replace("&nbsp;", " ");
        price = price.substring(0, price.indexOf("D") + 1);
        Trip cheapestTrip = new Trip(departureFlight, returnFlight, price);

        String bestPricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/a[1]/div[1]/div[2]/span[1]";

        String bestValuePrice = driver.findElement(By.xpath(bestPricePath)).getAttribute("innerHTML").replace("&nbsp;", " ");
        bestValuePrice = bestValuePrice.substring(0, bestValuePrice.indexOf("D") + 1);
//        print("Best price: " + bestValuePrice);

        Flight bestValueDepartureFlight = new Flight();
        Flight bestValueReturnFlight = new Flight();
        Trip bestValueTrip;
        if(bestValuePrice.equals(price)){
            bestValueTrip = cheapestTrip;
        }
        else{
            String index = getBestValuePriceIndex(bestValuePrice);
//            print(index);
            if(index == null){
                bestValueTrip = cheapestTrip;
            }
            else {
                String bestValuePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[" + index +
                        "]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/ol[1]";
                insertStats(bestValueDepartureFlight, "1", bestValuePath);
                insertStats(bestValueReturnFlight, "2", bestValuePath);
                bestValueTrip = new Trip(bestValueDepartureFlight, bestValueReturnFlight, bestValuePrice);
            }
        }

        //Up to here. Just finished putting values into bestValueTrip








    }
    public static String getBestValuePriceIndex(String price){
        for(int i = 2; i < 19; i++){    //This loop only goes up to 18 because Momondo only displaces 18 results
            try {
                String optionPricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[3]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[" + Integer.toString(i) +
                        "]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/a[1]/span[1]";
                String optionPrice = driver.findElement(By.xpath(optionPricePath)).getAttribute("innerHTML").replace("&nbsp;", " ");

                optionPrice = optionPrice.substring(0, optionPrice.indexOf("D") + 1).replace("\n", "");
                if (optionPrice.equals(price)) {
                    return Integer.toString(i);
                }
            }
            catch(Exception e){
                //Only have this here because Momondo skips some indices for some reason
            }
        }
        return null;


    }
    public static void insertStats(Flight departureFlight, String listIndex, String path) {
        String spanIndex = "1";
        String departAirportInfoPath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[2]/span[" + spanIndex +"]";
        departureFlight.airline = driver.findElement(By.xpath(path.substring(0, path.length() - 6) + "/div[1]")).getAttribute("innerHTML").replace("<span>", " ").replace("</span>", " ");
        departureFlight.departureAirportCode = (driver.findElement(By.xpath(departAirportInfoPath))).getAttribute("innerHTML");

        spanIndex = "2";
        departAirportInfoPath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[2]/span[" + spanIndex +"]";
        String departingTime = driver.findElement(By.xpath(departAirportInfoPath + "/span[1]")).getAttribute("innerHTML") + " " + driver.findElement(By.xpath(departAirportInfoPath + "/span[2]")).getAttribute("innerHTML");
        // The line above combines the time ex: 5:30 with "am/pm"
        departureFlight.departureTime = departingTime;

        spanIndex = "3";
        departAirportInfoPath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[2]/span[" + spanIndex +"]";            //setting the new path so we can get departureAirport
        departureFlight.departureAirport = driver.findElement(By.xpath(departAirportInfoPath)).getAttribute("innerHTML");

        String layoverPAth = path + "/li[" + listIndex + "]/div[1]/div[1]/div[3]/div[3]";
        WebElement layoverElement = driver.findElement(By.xpath(layoverPAth));
        if(!layoverElement.getAttribute("innerHTML").toLowerCase().contains("nonstop")){
            departureFlight.layover = true;
        }

        String travelTimePath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[3]/div[1]";
        departureFlight.totalTravelTime = driver.findElement(By.xpath(travelTimePath)).getAttribute("innerHTML");

        //Below will fill in info about arrival Airport
        String arrivalTimePath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[4]/span[1]/span[1]";
        String arrivalPartOfDayPath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[4]/span[1]/span[2]";
        String arrivalTime = driver.findElement(By.xpath(arrivalTimePath)).getAttribute("innerHTML") + " " + driver.findElement(By.xpath(arrivalPartOfDayPath)).getAttribute("innerHTML");
        departureFlight.arrivalTime = arrivalTime;


        String arrivalAirportCodePath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[4]/span[2]";
        departureFlight.arrivalAirportCode = driver.findElement(By.xpath(arrivalAirportCodePath)).getAttribute("innerHTML");

        String arrivalAirportPath = path + "/li[" + listIndex + "]/div[1]/div[1]/div[4]/span[3]";
        departureFlight.arrivalAirport = driver.findElement(By.xpath(arrivalAirportPath)).getAttribute("innerHTML");


    }

    
    
}
