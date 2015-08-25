package calendar_plus_notification_handler;

import java.util.ArrayList;

import calendar_plus.ScheduleItem;

import com.example.calendar_plus.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class NotificationScheduleAdapter extends ArrayAdapter<ScheduleItem>{

	Context context;
	private int layoutResourceId;
	private ScheduleItem data[] =null;
	protected ArrayList<String> selectedTimeSlots= new ArrayList<String>();

	public NotificationScheduleAdapter(Context context, int layoutResourceId, ScheduleItem[] data, ArrayList<String> STSs) {
		super(context, layoutResourceId, data);
		
		this.layoutResourceId=layoutResourceId;
		this.context=context;
		this.data=data;	
		this.selectedTimeSlots=STSs;
	}
	
	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//System.out.println(position);
        if(convertView==null){
	            // inflate the layout
	            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	            convertView = inflater.inflate(layoutResourceId, parent, false);
	        }
        
        //get schedule item based on the position
        ScheduleItem item=data[position];
        
        //set the schedule data
        TextView textview_time=(TextView) convertView.findViewById(R.id.TVtime);
        TextView textview_desc=(TextView) convertView.findViewById(R.id.TVdesc);
        
        textview_time.setText(item.getTime_Slot());
        textview_desc.setText(item.getDesc());
        
        
        CheckBox checkbox=(CheckBox) convertView.findViewById(R.id.CBset);
        //view the check box if time slot has an event
       if(item.getShow_check_box()){
    	   //System.out.println("true " + position);
        	
        	checkbox.setVisibility(View.VISIBLE);
        }else{
        	checkbox.setVisibility(View.INVISIBLE);
        }
       
       final TextView tv_time=(TextView) convertView.findViewById(R.id.TVtime);
       
       //get time slots of marked rows
       final CheckBox CB= (CheckBox) convertView.findViewById(R.id.CBset);
       CB.setOnClickListener(new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
		    
			//selectedTimeSlots.add(tv_time.getText().toString());
			if(CB.isChecked()){
			//System.out.println(tv_time.getText().toString());
				selectedTimeSlots.add(tv_time.getText().toString());
			}
			else{
			//System.out.println("no");
				if(selectedTimeSlots.contains(tv_time.getText().toString())){
					selectedTimeSlots.remove(selectedTimeSlots.indexOf(tv_time.getText().toString()));
				}
			}
			/*		
			//System.out.println("selected time slots");
			for(int i=0;i<selectedTimeSlots.size();i++){
				System.out.println(selectedTimeSlots.get(i));
			}*/
						
			
		}
    	   
       });
		return convertView;
	}
	
}

