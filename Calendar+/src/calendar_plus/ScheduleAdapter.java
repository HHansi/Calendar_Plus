package calendar_plus;

import com.example.calendar_plus.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends ArrayAdapter<ScheduleItem>{

	protected Context context;
	protected int layoutResourceId;
	protected ScheduleItem data[] =null;

	public ScheduleAdapter(Context context, int layoutResourceId, ScheduleItem[] data) {
		super(context, layoutResourceId, data);
		
		this.layoutResourceId=layoutResourceId;
		this.context=context;
		this.data=data;		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
        
		return convertView;
	}

}
