package calendar_plus_notification_handler;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
	 
	public class NotificationGenerator
	{
		Context context;
		public NotificationGenerator(Context context){
			this.context=context; //context=MainActivity.this
		}
	      public boolean createNotification(Calendar calendar){
	    	     Intent myIntent = new Intent(context, Receiver.class);
	    	     PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,0);
	    	     
	    			AlarmManager alarmManager = (AlarmManager) context
	    					.getSystemService(Context.ALARM_SERVICE);
	    		
	    			//alarm should repeat and notified once a day
	    			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pendingIntent);
	    		
	    			//for demonstration purpose
	    		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5*60*1000, pendingIntent);
	    			
	    	  return true;
	      }
	      
	}
