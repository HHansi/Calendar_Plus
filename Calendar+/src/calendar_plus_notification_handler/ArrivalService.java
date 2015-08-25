package calendar_plus_notification_handler;

//import com.example.calendar_plus.NotificationMapView.DownloadTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
//import com.example.calendar_plus.DirectionsJSONParser;
//import com.example.calendar_plus.NotificationMapView.ParserTask;



import calendar_plus_data_handler.DataAccess;
import calendar_plus_data_handler.Event;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class ArrivalService extends Service implements LocationListener{

	private double Lat_current;
	private double Lng_current;

	protected LatLng latLng_destination;
	protected LatLng latLng_current;
	
	protected double Lat_dest;
	protected double Lng_dest;
	
	protected String date;
	
	protected String time;
	
	protected ArrayList<String> time_list= new ArrayList<String>();
	
	protected String time_24;
	protected String location;
	
	private int index;
	
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 100;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 50;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private LocationRequest mLocationRequest;
    
    Calendar current_calendar;

	private LocationManager locationManager;
	
	protected DataAccess data_access=new DataAccess(this);
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		//get data from the intent
		date=intent.getStringExtra("date");
		time=intent.getStringExtra("time");
		time_24=intent.getStringExtra("time_24");
		//System.out.println("service started " + date + " " + time);
		
		Bundle bundle = intent.getParcelableExtra("bundle");
		ArrayList<String> time_list = bundle.getStringArrayList("time_list");
		timeCalculator(date,time_list);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//System.out.println("service destroyed");
		super.onDestroy();
		
	}
	
	//method to strat time calculation
	public void timeCalculator(String date, ArrayList<String> time_list){
		//set date and time of the event
		this.date=date;
		this.time_list=time_list;
		
		index=0; //set index to 0 at beginning
		startTimeCalculator();
	}
	
	private void startTimeCalculator(){
		//if end of the list stop service
		if(index==(time_list.size())){
			System.out.println("end of the list");
			this.stopSelf();
		}else{
		
		//System.out.println(time_list.get(index));
		
		Event event=data_access.getEvent(date, time_list.get(index));
		
		//set ldestination location
		location=event.getLocation();
		
		System.out.println("Subject " + event.getSubject());
		System.out.println("location " + event.getLocation());
		System.out.println("lat " + event.getLat());
		System.out.println("lng " + event.getLng());
		
		Lat_dest=event.getLat();
		Lng_dest=event.getLng();
        latLng_destination = new LatLng(Lat_dest, Lng_dest);
        
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		}
	}



	@Override
	public void onLocationChanged(Location location) {
		//if time_list reached its end location listener stops
		if(index==(time_list.size())){
			locationManager.removeUpdates(this);
			locationManager=null;
			System.out.println("location listener stopped");
		}else{
		
		//mark current location on map
		Lat_current= location.getLatitude();
		Lng_current=location.getLongitude();
		latLng_current=new LatLng(Lat_current, Lng_current);
		
		System.out.println("current Lat : " + Lat_current);
		System.out.println("current Lng : " + Lng_current);
		
		// Getting URL to the Google Directions API
		String url = getDirectionsUrl(latLng_current, latLng_destination);				
		
		DownloadTask downloadTask = new DownloadTask();
		
		// Start downloading json data from Google Directions API
		downloadTask.execute(url);
		}
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		Log.d("Latitude","disable");
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		Log.d("Latitude","enable");
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.d("Latitude","status");
		
	}

	private String getDirectionsUrl(LatLng origin,LatLng dest){
		
		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		
		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;		
		
					
		// Sensor enabled
		String sensor = "sensor=false";			
					
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;
					
		// Output format
		String output = "json";
		
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		
		
		return url;
	}
	
	// A method to download json data from url 
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url 
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url 
                urlConnection.connect();

                // Reading data from url 
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                        sb.append(line);
                }
                
                data = sb.toString();

                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
                iStream.close();
                urlConnection.disconnect();
        }
        return data;
     }
    
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{			
				
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {
				
			// For storing data from web service
			String data = "";
					
			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;		
		}
		
		/* Executes in UI thread, after the execution of
		 doInBackground() */
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);			
			
			ParserTask parserTask = new ParserTask();
			
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
				
		}		
	}
	
	//A class to parse the Google Places in JSON format 
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
    	
    	// Parsing the data in non-ui thread    	
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
			
			JSONObject jObject;	
			List<List<HashMap<String, String>>> routes = null;			           
            
            try{
            	jObject = new JSONObject(jsonData[0]);
            	DirectionsJSONParser parser = new DirectionsJSONParser();
            	
            	// Starts parsing data
            	routes = parser.parse(jObject);    
            }catch(Exception e){
            	e.printStackTrace();
            }
            return routes;
		}
		
		// Executes in UI thread, after the parsing process
		@SuppressWarnings("static-access")
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			if(index!=(time_list.size())){
			
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			
			@SuppressWarnings("unused")
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";
			
			
			//if no direction points found
			if(result.size()<1){
				Toast.makeText(getBaseContext(), "Searching for Points", Toast.LENGTH_SHORT).show();
				return;
			}
				
			
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				
				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);	
					
					if(j==0){	// Get distance from the list
						distance = (String)point.get("distance");						
						continue;
					}else if(j==1){ // Get duration from the list
						duration = (String)point.get("duration");
						
						
						System.out.println("distance : " + distance);
						System.out.println("duration : " + duration);
						
						//compare 
						current_calendar=current_calendar.getInstance();
						System.out.println("current time : " + current_calendar.getTime());
						
						int h=getHours(duration);
						int m= getMinutes(duration);
						
						//add duration to current time
						current_calendar.add(Calendar.HOUR_OF_DAY, h);
						current_calendar.add(Calendar.MINUTE, m);
						System.out.println("after adding : " + current_calendar.getTime());

						
						SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
						String time_str=   dateFormat.format(current_calendar.getTime());
						System.out.println("time : "+ time_str);
						
						//if current time + arrival time= appointment time
						if(time_compare(time_str, convert_to24(time_list.get(index)))){
							System.out.println("************ time up ************");
							
							//set arrival support enabled in the database
							data_access.updateArrivalSupport(date,time_list.get(index));
							
							//generate event notification
							EventNotificationGenerator ENG=new EventNotificationGenerator(ArrivalService.this);

							ENG.createNotification(location, time_list.get(index), latLng_destination);
							
							//set index to next appointment
							index=index+1;
							System.out.println("index+: " +index);
							
							//start time calculation
							startTimeCalculator();						
						}
						
						continue;
					}
					
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);	
					
					points.add(position);						
				}
				
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);	
				
			}						
		}		
		}
    }
    
    //return true if the time a >= time b
    private boolean time_compare(String a, String b){
    	String[] a_elements= a.split(":");
    	String[] b_elements=b.split(":");
    	
    	if(Integer.parseInt(a_elements[0])==Integer.parseInt(b_elements[0]) && 
			Integer.parseInt(a_elements[1])==Integer.parseInt(b_elements[1])){
    		return true;
    	}else if(Integer.parseInt(a_elements[0])> Integer.parseInt(b_elements[0])){
    		return true;
    	}else if(Integer.parseInt(a_elements[0])==Integer.parseInt(b_elements[0]) && 
    			Integer.parseInt(a_elements[1])>Integer.parseInt(b_elements[1])){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    
    //separate hours from a given time
    private int getHours(String duration){
    	String[] time_elements=duration.split(" ");
    	if(time_elements.length==4){ //if duration has hours return it
    		int hours=Integer.parseInt(time_elements[0]);
    		System.out.println("hours : " + hours);
    		return hours;
    	}else{
    		return 0;
    	}
    }
    
    //separate mins from a given time
    private int getMinutes(String duration){
    	String[] time_elements=duration.split(" ");
    	if(time_elements.length==4){ //if duration has hours return it
    		int mins=Integer.parseInt(time_elements[2]);
    		System.out.println("mins : " + mins );
    		return mins;
    	}else if(time_elements.length==2){
    		int mins=Integer.parseInt(time_elements[0]);
    		System.out.println("mins : " + mins);
    		return mins;
    	}else{
    		return 0;
    	}
    }
    
    //convert time to 24 hour time
    private String convert_to24(String time){
    	String[] timeElements=time.split(" ");
    	String[] time_digits=timeElements[0].split(":");
    	
		String time24;
		if(timeElements[1].equalsIgnoreCase("PM")){
			time24=(Integer.parseInt(time_digits[0])+12) + ":00"; 
		}else{
			time24=Integer.parseInt(time_digits[0]) + ":00";
		}
		return time24;
    }

}
