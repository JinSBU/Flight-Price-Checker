# Flight-Price-Checker
Checks for cheapest flights via Momondo.com.

Goal: To create a software program that will allow user to find the cheapest flight options. Users will be able to select how many days 
      they wish to travel and a time period for the travel. The program will compare all available options with those parameters and             return the cheapest flight option and best valued option. 
      
Roadmap for this project:

  Completed: (As of 5/2/2018)
  
      - Decide on a website to get best prices for flights (Momondo since Google Flights API is shutting down).
      
      - Create general outline for how this project will look.
      
      - Return cheapest flight given period of travel and airports.
      
      - Return "best" value flight given period of travel and airports.
      
  
On the way:

      - Create a more user-friendly interface for this roadmap using Javascript & HTML.
      
      - Create a user-friendly interface for selecting parameters and displaying results. Preferably a calender which will
      
        display cheapest price and show detailed description if hovered over.
        
      - Place flight data into a local/online database to see price differential between ordering today and yesterday.
      
      - Implement an alert system where users can set up alerts for when prices for tickets to a certain destination go on sale.
      
      - Host this on a website for ease of use.
      
      - Analyze data after a certain time frame to look for trends for pricing of tickets.
      
      - Be able to send large amounts of request to Momondo without being flagged.
      
Working on:

Finding alternative to headless selenium. As of right now, Momondo does not allow headless browsers to access their site. If I want to open 30 pages on their site, doing so without the option of headless browser would be a mess for users.      
