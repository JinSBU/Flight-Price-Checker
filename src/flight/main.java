package flight;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
;
import java.util.stream.IntStream;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.Thread.sleep;

/**
 *
 * @author Jin Zhen
 * This java program allows you to search Momondo.com for flights and looks for the optimal traveling period for cheapest travels.
 * At this point, need to open the Momondo on a Chrome before running
 */
public class main {


    static ChromeOptions options;
    static String from;
    static String to;
    static String includeNearbyAirports;
    public static void main(String[] args)throws InterruptedException{
//        String departDate = "26-06-2018";
//        String returnDate = "06-07-2018";
        String month = "11";
        String year = "2018";
        int durationOfTravel = 7;
        from = "NYC";
        to = "SFO";
        //Input email and password for gmail. Used to bypass captcha/bot check?

        // date is in the form of DD-MM-YYYY
        String includeNearbyAirports = "true";

        //These few lines bypasses the checks set by Momondo that disables automated testing
        options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches",Collections.singletonList("enable-automation"));

//        options.addArguments("headless");
        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36");


        System.setProperty("webdriver.chrome.driver", "C:/Users/jinth/IdeaProjects/Flight-Price-Checker/chromedriver.exe");


        //Below this point means all the prices have been loaded.
        /* Currently have several options to store prices
            can store into a DB and compare everyday to see if prices are steals or have lowered        *this could be a separate project*
            can have GUI and show lowest price in a month. THIS IS WHAT WILL BE WORKED ON
        */
        generateResultsForMonth(month, year, durationOfTravel);



    }
    public static void generateResultsForMonth(String month, String year, int durationOfTravel) throws InterruptedException {

        YearMonth yearMonthObj = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        String email = "133@gmail.com";
        String password = "";
        int numDaysInMonth = yearMonthObj.lengthOfMonth();

        // change 2 to numDaysInMonth - durationOfTravel
        IntStream.range(1,2).parallel().forEach(i->{
            WebDriver driver;
            driver = new ChromeDriver(options);

            try {
                loginGoogle(driver, email, password);

                //use intstream to try to get multhreading
                String startTripDay = String.format("%02d", i);
                String endTripDay = String.format("%02d", i + durationOfTravel);

                String departDate = startTripDay + "-" + month + "-" + year;         // We always start at the beginning of the month. Format is DD-MM-YYYY
                String returnDate = endTripDay + "-" + month + "-" + year;
                String url = ("https://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + from + "&SD0=" + to
                        + "&SDP0=" + departDate + "&SO1=" + to + "&SD1=" + from + "&SDP1=" + returnDate + "&AD=1&TK=ECO&DO=false&NA=" + includeNearbyAirports + "&currency=USD");
                Thread.sleep(1000);

                driver.navigate().to(url);

                waitForLoad(driver);
                Thread.sleep(4000);
                checkCaptcha(driver);
                sleep(2000);
                waitForJavascript(driver);
                generateResults(driver, departDate, returnDate, url);
            }
            catch(Exception e){
                e.printStackTrace();
            }


        });

    }
    public static void checkCaptcha(WebDriver driver) throws InterruptedException {

        if(driver.getPageSource().contains("Please confirm that you are a real momondo user")) {
            Thread.sleep(4000);
            driver.findElement(By.xpath( "//a[@id='loginlink']")).sendKeys(Keys.TAB, Keys.SPACE);
            sleep(3000);
        }
    }
    public static void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }
    public static void loginGoogle(WebDriver driver, String email, String password) throws InterruptedException {
        driver.navigate().to("https://accounts.google.com/ServiceLogin?hl=en&sacu=1");
        waitForLoad(driver);
        Thread.sleep(2000);
        WebElement emailBox = driver.findElement(By.id("identifierId"));
        emailBox.sendKeys(email, Keys.ENTER);
        Thread.sleep(2000);
        WebElement passwordBox = driver.findElement(By.name("password"));
        passwordBox.sendKeys(password, Keys.ENTER);
//        Thread.sleep(2000);

    }
    public static void print(String string){
        System.out.println(string);
    }
    /**
     * Compares the DOM periodically and compares the results to see if html on page is stabilized.
     */
    public static void waitForJavascript(WebDriver chrome) throws InterruptedException {
        print("Loading page...");
        WebDriverWait wait = new WebDriverWait(chrome, 70);
//        print(chrome.findElement(By.xpath("//div[contains(text(),'Search complete')]")).getAttribute("innerHTML"));
//        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("title"), "Search complete"));
        try {
            while (!chrome.findElement(By.xpath("//div[contains(text(),'Search complete')]")).getAttribute("innerHTML").equals("Search complete")) {
                sleep(1000);
            }
        }
        catch(Exception e){
            print("Could not complete search");
        }
    }

    /**
     * This method will generate a Trip instance for the cheapest value flight and best valued flight
     */
    public static void generateResults(WebDriver driver, String departDate, String returnDate, String url){
        //First, need to create a flight instance for departure and one for return (Cheapest flight)
        Flight departureFlight = new Flight();
        Flight returnFlight = new Flight();
        WebElement cheapestFlight = driver.findElement(By.className("mainInfo"));

//        WebElement cheapestFlight = flightInfo.get(0);

//          element.getText() returns details about a trip
        String tripInfo = cheapestFlight.getText();
        boolean newWebpage = false;
        String[] info = tripInfo.split("\n");
        if(info.length == 10){
            newWebpage = true;
        }
//        for(String x : info){
//            print(x);
//        }
        WebElement price = driver.findElement(By.className("option-text"));

        // All prices should now match the index of their corresponding Trip Element

        String airline = info[0];
        String[] departInfo = Arrays.copyOfRange(info, 1, 7);
        String[] returnInfo = Arrays.copyOfRange(info, 7, 13);
        insertStats(departureFlight, departInfo, airline);
        insertStats(returnFlight, returnInfo, airline);
        String priceText = price.getText();
        Trip cheapestTrip = new Trip(departureFlight, returnFlight, priceText, departDate, returnDate, url);
    }

    public static void insertStats(Flight flight, String[] info, String airline) {
        for(String x: info){
            print(x);
        }
        String departAirportCode = info[0].substring(0,3);
        String departTime = info[0].substring(4);
        String departLocation = info[1];
        String departDuration = info[2];
        String layover = info[3];

        String arrivalTime = info[4].substring(4);
        String arrivalAirportCode = info[4].substring(0, 3);
        String arrivalLocation = info[5];

        flight.airline = airline;
        flight.departureAirportCode = departAirportCode;
        flight.departureTime = departTime;

        flight.departureAirport = departLocation;


        if(layover.equals("Nonstop")){
            flight.layover = false;
        }
        else flight.layover = true;

        flight.totalTravelTime = departDuration;
        flight.arrivalTime = arrivalTime;

        flight.arrivalAirportCode = arrivalAirportCode;
        flight.arrivalAirport = arrivalLocation;
    }
}