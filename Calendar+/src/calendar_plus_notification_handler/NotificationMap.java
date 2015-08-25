package calendar_plus_notification_handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.calendar_plus.DirectionsJSONParser;
import com.example.calendar_plus.R;

public class NotificationMap extends FragmentActivity implements LocationListener{
	private GoogleMap map;
	private double Lat_current;
	private double Lng_current;
	
	//private double Lat_dest;
	//private double Lng_dest;
	
	protected LatLng latLng_destination;
	protected LatLng latLng_current;
	
	protected String location;
	protected String time;
	
	protected TextView tv_desc;
	protected TextView tv_distance;
	protected TextView tv_duration;
	
	
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

    LocationRequest mLocationRequest;

	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_mapview);
		
		//get extras from the intent
		Intent intent=getIntent();

		//set LatLng objects to destination
		Bundle bundle = getIntent().getParcelableExtra("bundle");
		latLng_destination = bundle.getParcelable("destination");		
		
		//set destination name
		location=intent.getStringExtra("location");
		//set appointment time
		time=intent.getStringExtra("time");
		
		//initialize text views
		tv_desc=(TextView) findViewById(R.id.tv_desc);
		tv_distance=(TextView) findViewById(R.id.tv_distance);
		tv_duration=(TextView) findViewById(R.id.tv_duration);
		
		//set text of notification description
		String text="You have an appointment at " + time + " at "+ location;
		tv_desc.setText(text);
		
		SupportMapFragment mf= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		
		//check whether the device installed google services or not
		if(isGoogleMapsInstalled()){
		map=mf.getMap(); //getting map
		}
		else{//if service not installed show toast
			Toast.makeText(getBaseContext(), "Google services not installed", Toast.LENGTH_SHORT).show();
		}
			
		
		// Clears all the existing markers	on map		
		map.clear();
		
		// Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        
        
        // Set the position for the marker
        markerOptions.position(latLng_destination);
        
    /*    // Set the title for the marker
        markerOptions.title(event.getLocation()); */
        
        //destination green
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        // Placing a marker on the position
        map.addMarker(markerOptions);    
        
        //zoom into the enterd location
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng_destination, 10);
        map.animateCamera(yourLocation);  
        
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
	
		//method to check whether the device has installed Google maps
		private boolean isGoogleMapsInstalled()
		{
		    try
		    {
		        @SuppressWarnings("unused")
				ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
		        return true;
		    } 
		    catch(PackageManager.NameNotFoundException e)
		    {
		        return false;
		    }
		}
		

		//method which listen for location changes
		@Override
		public void onLocationChanged(Location location) {
			//mark current location on map
			Lat_current= location.getLatitude();
			Lng_current=location.getLongitude();
			latLng_current=new LatLng(Lat_current, Lng_current);
			 MarkerOptions markerOptions = new MarkerOptions();
			 markerOptions.position(latLng_current);
			 markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
			 map.addMarker(markerOptions);
			 
			// Getting URL to the Google Directions API
			String url = getDirectionsUrl(latLng_current, latLng_destination);				
			
			DownloadTask downloadTask = new DownloadTask();
			
			// Start downloading json data from Google Directions API
			downloadTask.execute(url);
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("Latitude","disable");
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("Latitude","enable");
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
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
			
			// Executes in UI thread, after the execution of
			// doInBackground()
			@Override
			protected void onPostExecute(String result) {			
				super.onPostExecute(result);			
				
				ParserTask parserTask = new ParserTask();
				
				// Invokes the thread for parsing the JSON data
				parserTask.execute(result);
					
			}		
		}
		
		// A class to parse the Google Places in JSON format 
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
			@Override
			protected void onPostExecute(List<List<HashMap<String, String>>> result) {
				ArrayList<LatLng> points = null;
				PolylineOptions lineOptions = null;
				@SuppressWarnings("unused")
				MarkerOptions markerOptions = new MarkerOptions();
				String distance = "";
				String duration = "";
						
				//if unable to find any direction point
				if(result.size()<1){
					Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
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
				
				String distance_str="Distance: "+ distance;
				String duration_str="Duration: " + duration;
				
				//set duration and distance text views
				tv_distance.setText(distance_str);
				tv_duration.setText(duration_str);
				
				// Drawing polyline in the Google Map for the i-th route
				map.addPolyline(lineOptions);							
			}			
	    }   
		
		
}
	
	
