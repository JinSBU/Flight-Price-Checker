package flight;

import java.util.Collections;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
public class Flight {
    
    public WebDriver driver = new ChromeDriver();
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
//            System.out.println("HTML File retrieved");
        System.setProperty("webdriver.chrome.driver", "C:/Users/jinth/Desktop/flights/chromedriver.exe");
        WebDriver driver = new ChromeDriver(options);
        String url = ("https://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + from + "&SD0=" + to
                + "&SDP0=" + departDate + "&SO1=" + to + "&SD1=" + from + "&SDP1=" + returnDate + "&AD=1&TK=ECO&DO=false&NA=" +includeNearbyAirports + "&currency=USD");
        
        driver.navigate().to(url);
        print(url);
        waitForJavascript(driver);
        print("Finished loading");
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
        // got it to allow javascript to run first. will get the cheapest trip first

    }

    
    
    
}
