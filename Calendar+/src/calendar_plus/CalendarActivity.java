package calendar_plus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import calendar_plus_notification_handler.*;

import com.example.calendar_plus.R;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CalendarActivity extends ActionBarActivity {
	public GregorianCalendar Month, ItemMonth; 
	protected ArrayList<String> date;
	protected ArrayList<String> Items; //list of calendar items which needs to show
	protected ArrayList<String> Event;
	public CalendarAdapter Adapter;
	public Handler handler;
	protected TextView title;
	private static boolean Alarm_set=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);
		
		// get Calendar based on the current time in the default time zone with the default locale
		Month=(GregorianCalendar) GregorianCalendar.getInstance(); 
		
		ItemMonth=(GregorianCalendar) Month.clone();
			
		//set title
		title=(TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", Month));
		
		this.Adapter=new CalendarAdapter(this,Month); //initialize calendar adapter
		
		GridView gridview=(GridView) findViewById(R.id.grid);		
		//set grid view adapter	
		gridview.setAdapter(Adapter);
		
		//set previous month
		RelativeLayout Previous=(RelativeLayout) findViewById(R.id.previous);
		Previous.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setPreviousMonth();
				refreshCalendar();				
			}			
		});
		
		//set next month
		RelativeLayout Next=(RelativeLayout) findViewById(R.id.next);
		Next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				setNextMonth();
				refreshCalendar();				
			}
			
		});	
		
		//handle date selection on grid
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
					
					String selectedGridDate = CalendarAdapter.DayString.get(position);

					//create new intent and put selected grid date
					Intent intent=new Intent("com.example.calendar_plus.SCHEDULE");
					intent.putExtra("selectedGridDate", selectedGridDate);
					startActivity(intent);
			}			
		});
		
		
		//set the daily reminder
		if(Alarm_set==false){
			
			System.out.println("Alarm is not set");
			NotificationGenerator NG= new NotificationGenerator(CalendarActivity.this);
			
			 Calendar calendar = Calendar.getInstance();
			 
			 //set time to get the summary of schedule
	/*	     calendar.set(Calendar.HOUR_OF_DAY, 00);
		      calendar.set(Calendar.MINUTE, 02);
		      calendar.set(Calendar.SECOND, 0);
		      calendar.set(Calendar.AM_PM,Calendar.PM);  */
		      
		      //System.out.println("calendar+ " + calendar);
		      
		      Alarm_set=NG.createNotification(calendar);

		      //System.out.println(Alarm_set);	     
		}	
	}
	
	
	//set Gregorian calendar Month to next month
	private void setNextMonth(){
		//if current month is December
		if(Month.get(GregorianCalendar.MONTH)==Month.getActualMaximum(GregorianCalendar.MONTH)){
			Month.set((Month.get(GregorianCalendar.YEAR)+1),Month.getActualMinimum(GregorianCalendar.MONTH),1);
		}else{
			Month.set(GregorianCalendar.MONTH,Month.get(GregorianCalendar.MONTH)+1);
		}
	}
	
	//set Gregorian calendar Month to previous month 
	private void setPreviousMonth(){
		//if current month is January
		if(Month.get(GregorianCalendar.MONTH)==Month.getActualMinimum(GregorianCalendar.MONTH)){
			Month.set((Month.get(GregorianCalendar.YEAR)-1),Month.getActualMaximum(GregorianCalendar.MONTH),1);
		}else{
				Month.set(GregorianCalendar.MONTH,(Month.get(GregorianCalendar.MONTH))-1);
		}
	}
	private void refreshCalendar(){
		//title=(TextView) findViewById(R.id.title);
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", Month));
		Adapter.refreshDays();
		Adapter.notifyDataSetChanged();
		
	}
	

}
