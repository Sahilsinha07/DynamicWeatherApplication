package MyPackage;

import jakarta.servlet.ServletException;

//import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
//import org.apache.jasper.tagplugins.jstl.core.Url;

/**
 * Servlet implementation class MyServelet
 */
public class MyServelet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServelet() {
        super();
        
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String city = request.getParameter("city"); // this is used to get the input from the html page
		
		// api setup
		String apiKey = "dbb04f80ccdb8735a4f0847770ce6c7b";
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+ city+  "&appid=" + apiKey;
		
		try {

		URL url = new URL(apiUrl);
		HttpURLConnection connect = (HttpURLConnection)url.openConnection(); // here we have opened a new url connection
		connect.setRequestMethod("GET"); // setting the request method to get as we want data from the api of the connection we just opened

	    InputStream ip = connect.getInputStream(); // this will get the data from the url connection as data comes in stream
	    // now make a stream reader object to read the data from the input stream
	    InputStreamReader ir = new InputStreamReader(ip); // this will read the data from the input stream
	    Scanner sc = new Scanner(ir); // this will just read the data a// scann the data basically
	    StringBuilder recievedContent = new StringBuilder(); // made a new string builder to store the conent got from the api
	    while(sc.hasNextLine()) {
	    	recievedContent.append(sc.nextLine());
	    }
	    sc.close();
	    // closed the scanner object so data cannot be manipulated
	    // we have recieved the api content now we have to deliver it to the front end
	    // converting json into gson
//	    System.out.println(recievedContent);
	    // till now we have sent the request to the api and recieved the data fromt he api also in json formt
	    // now we need to convert this json data to string using gson library which is a library by google and has to be added externally
	    Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(recievedContent.toString(), JsonObject.class);
        //Date & Time
        long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000; // Convert to milliseconds
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // Format for the date
        String date = dateFormat.format(new Date(dateTimestamp));

        // Rest of your code...

        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
        // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", recievedContent.toString());
        
        connect.disconnect();
        
        
	}
		catch(IOException e) {
            e.printStackTrace(); // this is exceptional handling
            
        }
		// Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
		
	}

}
