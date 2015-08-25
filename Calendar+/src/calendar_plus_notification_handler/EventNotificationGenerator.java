package calendar_plus_notification_handler;

import java.util.Calendar;

import com.google.android.gms.maps.model.LatLng;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class EventNotificationGenerator{
	Context context;
	public EventNotificationGenerator(Context context){
		this.context=context; //context=MainActivity.this
	}
 
	//method to create notification
		public boolean createNotification(String location, String time, LatLng latLng_destination){
    	  	 Calendar calendar=Calendar.getInstance();
    	  	 
    	  	 //create intent and put extra data
    	     Intent myIntent = new Intent(context, EventReceiver.class);
    	     myIntent.putExtra("location", location);
    	     myIntent.putExtra("time", time);
    	     
    	     //add destination LatLng to the intent
    	     Bundle args = new Bundle();
    	     args.putParcelable("destination", latLng_destination);;
    	     myIntent.putExtra("bundle", args);     	     
    	     
    	     //create pending intent
    	     PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,0);
    	     
    	     //create an alarm manager to handle alarms
    			AlarmManager alarmManager = (AlarmManager) context
    					.getSystemService(Context.ALARM_SERVICE);
    		
    		//set alarm
    		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    			
    			
    		//System.out.println("notified");
    			
    			
    	  return true;
      }
      
}
