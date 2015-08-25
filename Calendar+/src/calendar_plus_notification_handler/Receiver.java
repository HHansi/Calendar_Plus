package calendar_plus_notification_handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

//import com.example.calendar_plus.NotificationSchedule;
import com.example.calendar_plus.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
	 
	@SuppressLint("SimpleDateFormat")
	public class Receiver extends BroadcastReceiver
	{	
		private static final int MY_NOTIFICATION_ID = 1;
		NotificationManager notificationManager;
		Notification myNotification;
	      
	    @Override
	    public void onReceive(Context context, Intent intent)
	    {	    	
	    	//get current date
	    	Calendar calendar=Calendar.getInstance();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String selectedDate=   dateFormat.format(calendar.getTime());
	    	
	    	Intent myIntent=new Intent(context, NotificationSchedule.class);
	    	myIntent.putExtra("selectedGridDate", selectedDate);
	    	
	    	//create pending intent of notification
	        PendingIntent pendingIntent = PendingIntent.getActivity(
	                context, 
	                0, 
	                myIntent, 
	                Intent.FLAG_ACTIVITY_NEW_TASK);

	        //create notification
	        myNotification = new NotificationCompat.Builder(context)
	        .setContentTitle("Calendar+")
	        .setContentText("Summary of today")
	        .setTicker("Notification!")
	        .setWhen(System.currentTimeMillis())
	        .setContentIntent(pendingIntent)
	        .setDefaults(Notification.DEFAULT_SOUND)
	        .setAutoCancel(true)
	        .setSmallIcon(R.drawable.notification_icon)
	        .build();
	        
	       // System.out.println("notification created");
	        //notify
			notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE); 
			notificationManager.notify(MY_NOTIFICATION_ID, myNotification); 


	    }  
	}


