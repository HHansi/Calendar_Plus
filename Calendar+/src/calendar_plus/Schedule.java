package calendar_plus;

import java.util.ArrayList;
import java.util.List;

import calendar_plus_data_handler.DataAccess;
import calendar_plus_data_handler.Event;

import com.example.calendar_plus.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class Schedule extends Activity {
	protected GridView gridview;
	private String selectedGridDate;
	
	protected List<Event> selectedList =new ArrayList<Event>();
	protected ScheduleItem[] ScheduleItemList=new ScheduleItem[24];


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

		//read appointent details of the selected date from database
		selectedList=data_access.getEventByDate(selectedGridDate);

	//testing
		System.out.println("****selected list****");
		for(int i=0;i<selectedList.size();i++){
			System.out.println(selectedList.get(i).getDate()+ " " + selectedList.get(i).getSubject() + " " + selectedList.get(i).getLocation());
		} 
		
		//create array for schedule items of the selected date
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

			String[] time=event.getTime().split(" ");
			String[] time_digit=time[0].split(":");
			
			int time_int;
			if(time[1].equalsIgnoreCase("PM")){
				time_int=Integer.parseInt(time_digit[0])+12;
			}else{
				time_int=Integer.parseInt(time_digit[0]);
			} 
			

			ScheduleItemList[time_int-1].setDesc(" " + event.getSubject() + " at " + event.getLocation());
			ScheduleItemList[time_int-1].setindex_of_selectedEventList(i);		
		}
		
		
		//set data of the schedule list view using the schedule adapter
		ListView listview =(ListView) findViewById(R.id.list_schedule_time);
        ScheduleAdapter adapter = new ScheduleAdapter(this, R.layout.schedule_row, ScheduleItemList );
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
				
				//add event date to the intent if time slots has an event
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
				intent.putExtra("parent_name", "Schedule");

				startActivity(intent);
			}
			
		});
		

		//grid image view to direct current view to calendar grid
		ImageView IV_grid =(ImageView) findViewById(R.id.IV_grid);
		IV_grid.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setCalendarGrid();
			}
			
		});
		
	}
	
	private Event getSelectedEvent(String time){
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
		
		//return selectedList[ScheduleItemList[time_index-1].getindex_of_selectedEventList()];		
	}
	
	
	private void setCalendarGrid(){
		Intent i_calendar = new Intent(this, CalendarActivity.class);
		startActivity(i_calendar);
		this.finish();
	}

	

}
