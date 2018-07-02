package flight;

import java.time.YearMonth;
import java.util.Collections;
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

    static WebDriver driver;
    static String from;
    static String to;
    static String includeNearbyAirports;
    public static void main(String[] args)throws InterruptedException{
//        String departDate = "26-06-2018";
//        String returnDate = "06-07-2018";
        String month = "08";
        String year = "2018";
        int durationOfTravel = 7;
        from = "NYC";
        to = "SFO";
        //Input email and password for gmail. Used to bypass captcha/bot check?
        String email = "textacc133@gmail.com";
        String password = "zxasqw123";
        // date is in the form of DD-MM-YYYY
        String includeNearbyAirports = "true";

        //These few lines bypasses the checks set by Momondo that disables automated testing
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches",Collections.singletonList("enable-automation"));

//        options.addArguments("headless");
        options.addArguments("--user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.50 Safari/537.36");


        System.setProperty("webdriver.chrome.driver", "C:/Users/jinth/Desktop/flights/chromedriver.exe");
        driver = new ChromeDriver(options);

        loginGoogle(email,password);
        print("Logged in");

        //Below this point means all the prices have been loaded.
        /* Currently have several options to store prices
            can store into a DB and compare everyday to see if prices are steals or have lowered        *this could be a separate project*
            can have GUI and show lowest price in a month. THIS IS WHAT WILL BE WORKED ON
        */
        generateResultsForMonth(month, year, durationOfTravel);

        driver.close();


    }
    public static void generateResultsForMonth(String month, String year, int durationOfTravel) throws InterruptedException {

        YearMonth yearMonthObj = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int numDaysInMonth = yearMonthObj.lengthOfMonth();
        for(int i = 1; i <= numDaysInMonth - durationOfTravel; i++){
            IntStream.range(0,20).parallel().forEach(i->{
                //use intstream to try to get multhreading
            });
            String startTripDay = String.format("%02d", i);
            String endTripDay = String.format("%02d", i + durationOfTravel);

            String departDate = startTripDay + "-" + month + "-" + year;         // We always start at the beginning of the month. Format is DD-MM-YYYY
            String returnDate = endTripDay + "-" + month + "-" + year;
            String url = ("https://www.momondo.com/flightsearch/?Search=true&TripType=2&SegNo=2&SO0=" + from + "&SD0=" + to
                    + "&SDP0=" + departDate + "&SO1=" + to + "&SD1=" + from + "&SDP1=" + returnDate + "&AD=1&TK=ECO&DO=false&NA=" +includeNearbyAirports + "&currency=USD");
            Thread.sleep(1000);
            driver.navigate().to(url);
            checkCaptcha();
            waitForJavascript(driver);
            print("Finished loading");
            generateResults(departDate, returnDate, url);
        }

    }
    public static void checkCaptcha(){
        if(driver.getPageSource().contains("Please confirm that you are a real momondo user")) {
            driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[1]/div[1]/div[1]/nav[1]/ul[1]/li[7]/div[1]/div[1]/div[1]/a[1]")).sendKeys(Keys.TAB, Keys.SPACE);
        }
        else
            print("No captcha");

    }
    public static void waitForLoad(WebDriver driver) {
        new WebDriverWait(driver, 30).until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }
    public static void loginGoogle(String email, String password) throws InterruptedException {
        driver.navigate().to("https://accounts.google.com/ServiceLogin?hl=en&sacu=1");
        waitForLoad(driver);
        Thread.sleep(2);
        WebElement emailBox = driver.findElement(By.id("identifierId"));
        emailBox.sendKeys(email, Keys.ENTER);
        Thread.sleep(2000);
        WebElement passwordBox = driver.findElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/form[1]/content[1]/div[1]/div[1]/div[1]/div[1]/div[1]/input[1]"));
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
        wait.until(ExpectedConditions.textToBePresentInElement(By.xpath("/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[1]/div[1]/div[1]/div[2]/div[1]"), "Search complete"));
        // got it to allow javascript to run first.

    }

    /**
     * This method will generate a Trip instance for the cheapest value flight and best valued flight
     */
    public static void generateResults(String departDate, String returnDate, String url){
        //First, need to create a flight instance for departure and one for return (Cheapest flight)
        Flight departureFlight = new Flight();
        Flight returnFlight = new Flight();
        String cheapestPath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/ol[1]";
        insertStats(departureFlight, "1", cheapestPath);
        insertStats(returnFlight, "2", cheapestPath);
        String pricePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[2]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/a[1]/span[1]";
        String price = driver.findElement(By.xpath(pricePath)).getAttribute("innerHTML").replace("&nbsp;", " ");
        price = price.substring(0, price.indexOf("D") + 1);
        Trip cheapestTrip = new Trip(departureFlight, returnFlight, price, departDate, returnDate, url);

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
            String index = getBestValuePriceIndex(bestValuePrice);
//            print(index);
            if(index == null){
                bestValueTrip = cheapestTrip;
            }
            else {
                String bestValuePath = "/html[1]/body[1]/div[1]/div[1]/div[4]/div[1]/div[1]/div[4]/div[1]/div[2]/div[1]/div[2]/div[6]/div[2]/div[1]/div[1]/div[" + index +
                        "]/div[1]/div[1]/div[1]/div[1]/div[1]/div[1]/ol[1]";
                insertStats(bestValueDepartureFlight, "1", bestValuePath);
                insertStats(bestValueReturnFlight, "2", bestValuePath);
                bestValueTrip = new Trip(bestValueDepartureFlight, bestValueReturnFlight, bestValuePrice, departDate, returnDate, url);
            }
        }

        bestValueTrip.printStats();



    }
    public static String getBestValuePriceIndex(String price){
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