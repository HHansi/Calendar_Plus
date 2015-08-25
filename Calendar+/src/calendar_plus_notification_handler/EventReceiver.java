package calendar_plus_notification_handler;

import com.example.calendar_plus.R;
import com.google.android.gms.maps.model.LatLng;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
	 
	public class EventReceiver extends BroadcastReceiver
	{	
		private static final int MY_NOTIFICATION_ID = 1;
		NotificationManager notificationManager;
		Notification myNotification;
	      
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {	 
	    	//get data from intent
	    	String location= intent.getStringExtra("location");
	    	String time=intent.getStringExtra("time");
	    	
	    	String text= "Appointment at " +  location +" at " +  time +"."  ;

	    	//get LatLng object of destination
			Bundle bundle = intent.getParcelableExtra("bundle");
			LatLng latLng_destination = bundle.getParcelable("destination");
	    	
	    	Intent myIntent=new Intent(context, NotificationMap.class);

	    	//put location , time and LatLng object of destination point to the intent
	    	myIntent.putExtra("location", location);
	    	myIntent.putExtra("time", time);
   	     	//add destination LatLng to the intent
   	     	Bundle args = new Bundle();
   	     	args.putParcelable("destination", latLng_destination);
   	     	myIntent.putExtra("bundle", args); 
	    	
	        PendingIntent pendingIntent = PendingIntent.getActivity(
	                context, 
	                0, 
	                myIntent, 
	                Intent.FLAG_ACTIVITY_NEW_TASK);

	        //create notification
	        myNotification = new NotificationCompat.Builder(context)
	        .setContentTitle("Calendar+")
	        .setContentText(text)
	        .setTicker("Getting late!")
	        .setWhen(System.currentTimeMillis())
	        .setContentIntent(pendingIntent)
	        .setDefaults(Notification.DEFAULT_SOUND)
	        .setAutoCancel(true)
	        .setSmallIcon(R.drawable.notification_icon)
	        .build();
	        
	        //System.out.println("notification created");
	        
	        //notify notification
			notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE); 
			notificationManager.notify(MY_NOTIFICATION_ID, myNotification); 


	    }  
	}