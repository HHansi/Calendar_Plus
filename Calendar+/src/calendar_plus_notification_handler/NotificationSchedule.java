package calendar_plus_notification_handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import calendar_plus.ScheduleItem;
import calendar_plus_data_handler.DataAccess;
import calendar_plus_data_handler.Event;

import com.example.calendar_plus.R;





import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NotificationSchedule extends Activity{
	//private GridView gridview;
	private String selectedGridDate;
	private ArrayList<String> selectedTimeSlots= new ArrayList<String>();
	
	private List<Event> selectedList =new ArrayList<Event>();
	private ScheduleItem[] ScheduleItemList=new ScheduleItem[24];


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule);
		
		//set selected date text
		TextView selectedDate=(TextView) findViewById(R.id.selected_date);
		//get data from the intent
		Intent intent=getIntent();
		selectedGridDate= intent.getStringExtra("selectedGridDate");
		selectedDate.setText(selectedGridDate);
		
		//get all events details
		DataAccess data_access=new DataAccess(this);
		
		selectedList=data_access.getEventByDate(selectedGridDate);
		
			//initialize schedule item list
			for(int i=0;i<24;i++){
				ScheduleItemList[i]=new ScheduleItem("", "", 100 );
			}

		//set time slots of the schedule	
		for(int i=0;i<24;i++){
			if(i<12){
				ScheduleItemList[i].setTime_slot(i+1 + ":00 AM");
			}else{
				ScheduleItemList[i].setTime_slot(((i-12)+1) + ":00 PM");
			}
			
		}

		//if time slots have events add them to the schedule item list
		for(int i=0;i<selectedList.size();i++){
			Event event=selectedList.get(i);
			System.out.println(event);
			
			//set selected time slots list
			selectedTimeSlots.add(event.getTime());
			
			String[] time=event.getTime().split(" ");
			String[] time_digit=time[0].split(":");
			
			int time_int;
			if(time[1].equalsIgnoreCase("PM")){
				time_int=Integer.parseInt(time_digit[0])+12;
			}else{
				time_int=Integer.parseInt(time_digit[0]);
			} 
			
			//insert data of the event to the schedule item list
			ScheduleItemList[time_int-1].setDesc(" " + event.getSubject() + " at " + event.getLocation());
			ScheduleItemList[time_int-1].setindex_of_selectedEventList(i); //to get event data later from selected event list
			ScheduleItemList[time_int-1].setShow_check_box(true);	
		}
		
		
		//set data of the schedule list view using the schedule adapter
		ListView listview =(ListView) findViewById(R.id.list_schedule_time);
       // ScheduleAdapter adapter = new ScheduleAdapter(this, R.layout.notification_schedule_row, ScheduleItemList );
		final NotificationScheduleAdapter adapter = new NotificationScheduleAdapter(this, R.layout.notification_schedule_row, 
				ScheduleItemList,selectedTimeSlots );
        listview.setAdapter(adapter);
        
		//handle selection on list item
		listview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				//time of selected slot
				String time=((TextView) v.findViewById(R.id.TVtime)).getText().toString();
				Event event=getSelectedEvent(time);
				
				Intent intent=new Intent("com.example.calendar_plus.SCHEDULEMANAGER");
				
				//put extra data into intent
				if(event!=null){
					intent.putExtra("Subject", event.getSubject());
					intent.putExtra("Description", event.getDescription());
					intent.putExtra("Location", event.getLocation());
					intent.putExtra("ASE", event.getASE() );
				}else{
					intent.putExtra("Subject", "");
					intent.putExtra("Description", "");
					intent.putExtra("Location", "");
					intent.putExtra("ASE", "0");
				}
				
				
				//create intent schedule manager and put date and time
				
				intent.putExtra("SelectedDate", selectedGridDate);
				intent.putExtra("Time", time);
				intent.putExtra("parent_name", "NotificationSchedule");

				startActivity(intent);
			}
			
		});  
        
        //set grid invisible
        ImageView grid_icon=(ImageView) findViewById(R.id.IV_grid);
        grid_icon.setVisibility(View.GONE);
        
		Button Enable= (Button) findViewById(R.id.btn_enable);
		Enable.setVisibility(View.VISIBLE);
		Enable.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				//enable on time arrival supports only if GPS enabled
				if(isGpsEnabled()){
				System.out.println("enable selected");
				
				
				//selected checked time slots
				selectedTimeSlots= adapter.selectedTimeSlots;
				
				
				//create time list in sorted order
				ArrayList<String> time_list=new ArrayList<String>();
				for(int i=0;i<selectedList.size();i++){
					Event event=selectedList.get(i);
					
					@SuppressWarnings("unused")
					String[] time=event.getTime().split(" ");
					//String[] time_digit=time[0].split(":");
				

					time_list.add(event.getTime());
				}

				ArrayList<String> sortedTimeSlots=sort(selectedTimeSlots);
				
					//print sorted list
					System.out.println("ArrayList is sorted");
					for(String temp:sortedTimeSlots){
						System.out.println(temp);
					} 
					
				//create new intent
				Intent intent= new Intent(NotificationSchedule.this, ArrivalService.class);
				//set date of the intent
				intent.putExtra("date", selectedGridDate);
				
				//put sorted time list to the intent
	    	     Bundle args = new Bundle();
	    	     //args.putStringArrayList("time_list", selectedTimeSlots);
	    	     args.putStringArrayList("time_list", sortedTimeSlots);
	    	     intent.putExtra("bundle", args);
	    	     

	    	     //if schedule has enables appointments
				if(time_list.size()==0){
					Toast.makeText(getBaseContext(), "No appointments", Toast.LENGTH_SHORT).show();

				}else{
					//start on time arrival supporter service
		    	     startService(intent);
		    	     
		    	     //show toast
		    	     Toast.makeText(getBaseContext(), "Arrival support enabled.", Toast.LENGTH_SHORT).show();
		    	     
		    	     //finish activity
		    	     finishActivity();
				}
	    	     
	    	     
			}else{//show toast to enable GPS
				Toast.makeText(getBaseContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();		
			}
			}
						
			
		});
		
		

	}
	private void finishActivity(){
		this.finish();
	}
	
	//method to get event data based on time
	private Event getSelectedEvent(String time){
		//Event event=new Event();
		String[] time_str=time.split(" ");
		String[] time_digits=time_str[0].split(":");
		int time_index;
		if(time_str[1].equalsIgnoreCase("PM")){
			time_index=Integer.parseInt(time_digits[0]) +12;
		}else{
			time_index=Integer.parseInt(time_digits[0]);
		}
		if(ScheduleItemList[time_index-1].getindex_of_selectedEventList()==100){
			return null;
		}else{
			return selectedList.get(ScheduleItemList[time_index-1].getindex_of_selectedEventList());
		}
		
	}
	
	//check availability of location services
	private boolean isGpsEnabled()
	{
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		return service.isProviderEnabled(LocationManager.GPS_PROVIDER)&&service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	//method o sort an array list
	private ArrayList<String> sort(ArrayList<String> unsortedTime){
		ArrayList<Integer> AM=new ArrayList<Integer>();
		ArrayList<Integer> PM=new ArrayList<Integer>();
		
		//seperate time slots to Am and Pm
		for(int i=0;i<unsortedTime.size(); i++){
			String item=unsortedTime.get(i);
			if(item.split(" ")[1].equals("AM")){
				AM.add(Integer.parseInt((item.split(" ")[0]).split(":")[0]));
			}else{
				PM.add(Integer.parseInt((item.split(" ")[0]).split(":")[0]));
			}
		}
		
		//sort Am and Pm seperatly
		Collections.sort(AM);
		Collections.sort(PM);
		
		//create sorted time list
		ArrayList<String> sortedTime= new ArrayList<String>();
		for(int i=0;i<AM.size();i++){
			sortedTime.add(AM.get(i)+ ":00 AM");
		}
		for(int i=0;i<PM.size();i++){
			sortedTime.add(PM.get(i)+ ":00 PM");
		}
		return sortedTime;
		
	}






}

