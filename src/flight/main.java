package flight;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static java.lang.Thread.currentThread;
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
        // got it to allow javascript to run first.

    }

    /**
     * This method will generate a Trip instance for the cheapest value flight and best valued flight
     */
    public static void generateResults(WebDriver driver, String departDate, String returnDate, String url){
        //First, need to create a flight instance for departure and one for return (Cheapest flight)
        Flight departureFlight = new Flight();
        Flight returnFlight = new Flight();
        List<WebElement> flightInfo = driver.findElements(By.className("mainInfo"));

        List<WebElement> prices = driver.findElements(By.className("option-text"));
        ArrayList<WebElement> removeList = new ArrayList<WebElement>();
        for(WebElement x : prices){

            if(!x.getText().contains("USD")){
                removeList.add(x);
            }
        }
        for(WebElement x : removeList){
            prices.remove(x);
        }
        // All prices should now match the index of their corresponding Trip Element

//        for(int i = 0; i < flightInfo.size(); i++){
        WebElement cheapestFlight = flightInfo.get(0);

//          element.getText() returns details about a trip
        String tripInfo = cheapestFlight.getText();

        String[] info = tripInfo.split("\n");
        String[] departInfo = Arrays.copyOfRange(info, 0, 7);
        String[] returnInfo = Arrays.copyOfRange(info, 7, 13);
        insertStats(departureFlight, departInfo);
        insertStats(returnFlight, returnInfo);
        String price = prices.get(0).getText();
        Trip cheapestTrip = new Trip(departureFlight, returnFlight, price, departDate, returnDate, url);



        // NEED TO UPDATE THIS PATH AND ALL CODE RELATING TO IT

        String bestPricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[4]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/div[3]/a[1]/div[1]/div[2]/span[1]";

        String bestValuePrice = driver.findElement(By.xpath(bestPricePath)).getAttribute("innerHTML").replace("&nbsp;", " ");
        bestValuePrice = bestValuePrice.substring(0, bestValuePrice.indexOf("D") + 1);

        Flight bestValueDepartureFlight = new Flight();
        Flight bestValueReturnFlight = new Flight();
        Trip bestValueTrip;
        if(bestValuePrice.equals(price)){
            bestValueTrip = cheapestTrip;
        }
        else{
            String index = getBestValuePriceIndex(driver, bestValuePrice);
//            print(index);
            if(index == null){
                bestValueTrip = cheapestTrip;
            }
            else {
                String bestValuePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[" + index +
                        "]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/ol[1]";
//                insertStats(driver, bestValueDepartureFlight, "1", bestValuePath);
//                insertStats(driver, bestValueReturnFlight, "2", bestValuePath);
                bestValueTrip = new Trip(bestValueDepartureFlight, bestValueReturnFlight, bestValuePrice, departDate, returnDate, url);
            }
        }

        bestValueTrip.printStats();



    }
    public static String getBestValuePriceIndex(WebDriver driver, String price){
        for(int i = 2; i < 19; i++){    //This loop only goes up to 18 because Momondo only displaces 18 results
            try {
                String optionPricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[" + Integer.toString(i) +
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
    public static void insertStats(Flight flight, String[] info) {
        String airline = info[0];
        String[] departInfo = info[1].split(" ", 2);
        String departAirportCode = departInfo[0];
        String departTime = departInfo[1];
        String departLocation = info[2];
        String departDuration = info[3];
        String layover = info[4];
        String[] arrivalInfo = info[5].split(" ", 2);
        String arrivalTime = arrivalInfo[0];
        String arrivalAirportCOde = arrivalInfo[1];
        String arrivalLocation = info[6];

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

        flight.arrivalAirportCode = arrivalAirportCOde;
        flight.arrivalAirport = arrivalLocation;
    }



}