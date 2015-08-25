package calendar_plus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import calendar_plus_data_handler.DataAccess;
import calendar_plus_data_handler.Event;
import calendar_plus_notification_handler.NotificationSchedule;

import com.example.calendar_plus.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleManager extends Activity{
	private String SelectedDate;
	private String Time_str;
	private String subject_str;
	private String description_str;
	private String location_str;
	private String ASE_str;
	
	private EditText subject;
	private EditText description;
	private EditText date;
	private EditText time;
	private EditText duration;
	private EditText location;
	private DataAccess data_access;
	protected String Location;
	
	protected double lat;
	protected double lng;
	
	protected String name;
	
	protected boolean mapviewSelected=false;
	protected boolean saveSelected=false;
	
	private boolean location_verified;
	
	protected String parent_name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//create data access object
		data_access=new DataAccess(this);
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_row_data);
		
		//get selected date and time from the intent
		Intent intent=getIntent();
		SelectedDate=intent.getStringExtra("SelectedDate");
		Time_str=intent.getStringExtra("Time");
		subject_str=intent.getStringExtra("Subject");
		description_str=intent.getStringExtra("Description");
		location_str=intent.getStringExtra("Location");	
		ASE_str=intent.getStringExtra("ASE");
		
		//set parent name
		parent_name=intent.getStringExtra("parent_name");

		//set date to the selected date
		date=(EditText) findViewById(R.id.data_Date);
		date.setText(SelectedDate);
		
		//set time to the selected time
		time=(EditText) findViewById(R.id.data_Time);
		time.setText(Time_str);		
		
		//set duration
		duration=(EditText) findViewById(R.id.data_Duration);
		duration.setText("1 hour");
		
		subject=(EditText) findViewById(R.id.data_Subject);
		subject.setText(subject_str);
		
		description= (EditText) findViewById(R.id.data_Description);
		description.setText(description_str);
		
		location=(EditText) findViewById(R.id.data_Location);
		location.setText(location_str);
		
		//if event notified set view map enable
		Button view_map=(Button) findViewById(R.id.btn_view_map);
		if(ASE_str.equals("1")){
			view_map.setVisibility(View.VISIBLE);
			System.out.println("view map");
		}
		//selection of view map
		view_map.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent("com.example.calendar_plus_notification_handler.NOTIFICATIONMAP");
				
				Event event=data_access.getEvent(SelectedDate, Time_str);
				double Lat_dest=event.getLat();
				double Lng_dest=event.getLng();
				LatLng latLng_destination = new LatLng(Lat_dest, Lng_dest);
				
				//put extras to the intent
				intent.putExtra("location", location_str);
		    	intent.putExtra("time", Time_str);
		    	
	   	     	//add destination LatLng to the intent
	   	     	Bundle args = new Bundle();
	   	     	args.putParcelable("destination", latLng_destination);
	   	     	intent.putExtra("bundle", args); 				
				
				startActivity(intent);
				
			}
			
		});
		
		TextView verify_location=(TextView) findViewById(R.id.verify_location);	
		//selection of view map
		verify_location.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//set selections
				mapviewSelected=true;
				saveSelected=false;
				
				//get location
				Location= location.getText().toString();
				
				//if user did not entered the location show toast
				if(Location==null || Location.equals("")){
					Toast.makeText(getBaseContext(), "No location is entered", Toast.LENGTH_SHORT).show();
					
				//else view the location marker on map	
				}else{
					//set latitude and longitude of the location
					//after calculating lat and lng activity which shows the map will be started by non-ui thread
					setLatLngName();
				}  
			}		
		});
		
		//save selection
		ImageView save_button=(ImageView) findViewById(R.id.save);
		save_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				saveSelected=true;
				mapviewSelected=false;
				//System.out.println("save");
				
				//if user did not entered the event information
				if(subject.getText().toString().equals("") && location.getText().toString().equals("") ){
					Toast.makeText(getBaseContext(), "Please enter information.", Toast.LENGTH_SHORT).show();
				}else{
					//store event data if location is verified
					if(location_verified==true){
						storeEvent();
						
						//show toast to the user
						Toast.makeText(getBaseContext(), "Event data successfully saved", Toast.LENGTH_SHORT).show();

				
					}else{
						Toast.makeText(getBaseContext(), "Please verify the location.", Toast.LENGTH_SHORT).show();
					}

				
				}
				
			}			
		});	
		
		//delete button functionality
		ImageView delete_button=(ImageView) findViewById(R.id.delete);
		delete_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//delete event
				data_access.deleteEvent(date.getText().toString(), time.getText().toString());
				Toast.makeText(getBaseContext(), "Event data successfully deleted", Toast.LENGTH_SHORT).show();
				loadSchedule();
			}
			
		});

	//event button	
/*	
		Button event_btn=(Button) findViewById(R.id.event);
		event_btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent("com.example.calendar_plus.NOTIFICATIONMAPVIEW");
				//String date=SelectedDate +" " + Time_str;
				//intent.putExtra("date_time", date);
				
				intent.putExtra("date", SelectedDate);
				intent.putExtra("time", Time_str);
				startActivity(intent);
				
			}
			
		});  */
	}

	//method to download data from the url
	private String downloadUrl(String Url_Str) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
        		//create URL by passing the url as a String
                URL url = new URL(Url_Str);

                // Creating a http connection
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

                //close buffer reader
                br.close();

        }catch(Exception e){
                Log.d("Exception while downloading url", e.toString());
        }finally{
        		//close input stream
                iStream.close();
                //disconnect http connection
                urlConnection.disconnect();
        }
        return data;        
	}	
	
	//class which downloads Places from the web service in non ui thread
	private class DownloadTask extends AsyncTask<String, Integer, String>{
	        String data = null;

	        @Override // method which invoke with execute method
	        protected String doInBackground(String... url) {
	                try{    
	                	//download data
	                         data = downloadUrl(url[0]);
	                }catch(Exception e){
	                         Log.d("Background Task",e.toString());
	                }
	                return data;
	        }

	        // Execute after the complete execution of doInBackground method
	        @Override
	        protected void onPostExecute(String result){        		
	        		//start ParserTask execution which parses the json data  in a non-ui thread
	        		ParserTask parserTask = new ParserTask();
	                parserTask.execute(result);
	        }
	}
	
    //class which parse the Geocoded Places in non-ui thread 
	class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
		JSONObject jObject;
				
		@Override  // method which invoke with execute method
		protected List<HashMap<String,String>> doInBackground(String... jsonData) {	
			List<HashMap<String, String>> places = null;			
			GeocodeJSONParser parser = new GeocodeJSONParser();
        
	        try{
	        	jObject = new JSONObject(jsonData[0]);	        	
	           //parse json object and get list of HashMaps which contains place details
	            places = parser.parse(jObject);
	            
	        }catch(Exception e){
	                Log.d("Exception",e.toString());
	        }
	        return places;
		}
		
		// Executed after the complete execution of doInBackground method
		@Override
		protected void onPostExecute(List<HashMap<String,String>> list){	
			//System.out.println("size " + list.size());
			//for(int i=0;i<list.size();i++){
	            
	            // Getting a place from the places list
	            HashMap<String, String> hmPlace = list.get(0);
	
	            // Getting latitude of the place
	            lat = Double.parseDouble(hmPlace.get("lat"));	            
	            
	            // Getting longitude of the place
	            lng = Double.parseDouble(hmPlace.get("lng"));
	            
	            // Getting name
	            name = hmPlace.get("formatted_address");
	            
	           // boolean LatLngcalculated = true;
	            
	            if(mapviewSelected){
	            
	            	//set location verified
	            	location_verified=true;
	            //create intent and put latitude, longitude and name of the location point
				Intent intent=new Intent("com.example.calendar_plus.MAPVIEW");
				intent.putExtra("lat", lat);
				intent.putExtra("lng", lng);
				intent.putExtra("location", name);				

				startActivity(intent);
	            }
	            else if(saveSelected){
	            	storeEvent();
	            }            
		}
	}
	
	
	//store event to the database
	private void storeEvent(){
		//create event and set attributes
		Event event=new Event();
		event.setSubject(subject.getText().toString());
		event.setDescription(description.getText().toString());
		event.setLocation(location.getText().toString());
		event.setDate(date.getText().toString());
		event.setTime(time.getText().toString());
		
		event.setLat(this.lat);
		event.setLng(this.lng);
		
		long duration_in_mins;
		String[] d = duration.getText().toString().split(" ");
		if(d[1].equalsIgnoreCase("hour")){
			duration_in_mins=(Integer.parseInt(d[0]))*60;
		} else{
			duration_in_mins=Integer.parseInt(d[0]);
		}
			
		event.setDuration(duration_in_mins);
		
		//store event to the database
		data_access.addEvent(event);
		
		//view schedule after saving
		this.loadSchedule();
		
		
	} 
	
	//if map is not viewed
	//but with verifying location by map this method doesn't need
	protected void setLatLngName(){	
		String url = "https://maps.googleapis.com/maps/api/geocode/json?";

		try {
			// encoding special characters like space in the user input location
			Location = URLEncoder.encode(location.getText().toString(), "utf-8");
			//System.out.println("location 317 " + Location);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}					
		String Address= "address=" + Location;
		
		//whether your application used a sensor to determine the user's location
		//String Sensor = "sensor=true"; 
		String Sensor = "sensor=flase"; 
		
		//set url
		url = url + Address + "&" + Sensor;
		System.out.println(url);
							
		// Instantiating DownloadTask to get places from Google Geocoding service in a non-ui thread
		DownloadTask downloadTask = new DownloadTask();
		
		// Start downloading the geocoding places
		downloadTask.execute(url);	   
	} 
	
	//method to view schedule after saving or deleting
	private void loadSchedule(){

		if(parent_name.equals("Schedule")){
			Intent i = new Intent(this, Schedule.class);
			i.putExtra("selectedGridDate", SelectedDate);
			startActivity(i);
			//finish current activity
			this.finish();
		}else if(parent_name.equals("NotificationSchedule")){
			Intent i = new Intent(this, NotificationSchedule.class);
			i.putExtra("selectedGridDate", SelectedDate);
			startActivity(i);
			//finish current activity
			this.finish();
		}
	    
	    
	}

}

