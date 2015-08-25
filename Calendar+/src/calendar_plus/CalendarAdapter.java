package calendar_plus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import calendar_plus_data_handler.DataAccess;

import com.example.calendar_plus.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter{
	private java.util.Calendar Month;
	
	public GregorianCalendar Pmonth;
	
	private GregorianCalendar SelectedDate;
	protected SimpleDateFormat DF;
	protected String CurrentDateString;
	
	public static List<String> DayString;
	private ArrayList<String> Items;
	
	private Context context;
	
	protected String ItemValue;
	private GregorianCalendar PmonthMaxSet;
	protected int FirstDay, MaxWeekNo, MonthLength, MaxP, CalMaxP;
	
	private View PreviousView;
	

	public CalendarAdapter(Context context, GregorianCalendar month){
		DayString=new ArrayList<String>();
		this.Items=new ArrayList<String>();
		//set Month to current Month based calendar
		this.Month=month;
		
		this.SelectedDate=(GregorianCalendar) month.clone();
		this.context=context;
		
		//set first day of the month
		month.set(GregorianCalendar.DAY_OF_MONTH, 1);
		//create data format
		this.DF=new SimpleDateFormat("yyyy-MM-dd",Locale.US);

		this.CurrentDateString=DF.format(SelectedDate.getTime());
		this.refreshDays();
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return DayString.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return DayString.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int Position, View ConvertView, ViewGroup Parent) {
		// TODO Auto-generated method stub
		View V=ConvertView;
		
		TextView DayView;
		if(ConvertView==null){
			LayoutInflater LI=(LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			V=LI.inflate(R.layout.calendar_item, null);
		}
		DayView=(TextView)V.findViewById(R.id.date); //text view for date in calendar grid
		
		//set visibility of dots to gone 
		ImageView IV_red=(ImageView) V.findViewById(R.id.date_icon_busy);
		ImageView IV_black=(ImageView) V.findViewById(R.id.date_icon);
		IV_red.setVisibility(View.GONE);
		IV_black.setVisibility(View.GONE);
		
		
		//separate day string
		String[] SeparatedTime=DayString.get(Position).split("-");
		//take last part of the date as the grid value
		String GridValue=SeparatedTime[2].replace("^0*", "");
		
		//checking whether the day is in current month or previous month
			//if previous month dates
		if((Integer.parseInt(GridValue)>1)&&(Position<FirstDay)){
			//set colour to white
			DayView.setTextColor(Color.WHITE);
			//set not clickable and not focusable
			DayView.setClickable(false);
			DayView.setFocusable(false);
			
			//if next month dates
		}else if((Integer.parseInt(GridValue)<7)&&(Position>28)){
			DayView.setTextColor(Color.WHITE);
			DayView.setClickable(false);
			DayView.setFocusable(false);
			
			//set current month dates
		}else{
			//set color to black
			DayView.setTextColor(Color.BLACK);
		}
		
		//set current date selected
		if(DayString.get(Position).equals(CurrentDateString)){
			setSelected(V);
			PreviousView=V;
		}else{
			V.setBackgroundResource(R.drawable.list_item_background);
		}
		//set date on grid
		DayView.setText(GridValue);
		
		//set date
		//ie. 2 -> 02
		String Date=DayString.get(Position);
		if(Date.length()==1){
			Date="0" + Date;
		}
		//set month
		String MonthStr="" + (Month.get(GregorianCalendar.MONTH)+1);
		if(MonthStr.length()==1){
			MonthStr="0" + MonthStr;
		}
		
		//set event strength of a day
		DataAccess data_access=new DataAccess(context);
		if(data_access.getNoOfEventsByDate(DayString.get(Position))>5){
			//ImageView IV=(ImageView) V.findViewById(R.id.date_icon_busy);
			IV_red.setVisibility(View.VISIBLE);
		}
		else if(data_access.getNoOfEventsByDate(DayString.get(Position))>0){
			IV_black.setVisibility(View.VISIBLE);
			
		}
		
		return V;
	} 
	
	// method to set previous view
	private View setSelected(View view){
		if(PreviousView!=null){
			PreviousView.setBackgroundResource(R.drawable.list_item_background);
		}
		PreviousView=view;
		view.setBackgroundResource(R.drawable.calendar_cel_selectl);
		return view;
	}

	public void refreshDays(){
		Items.clear();
		DayString.clear();
		Pmonth=(GregorianCalendar) Month.clone();
		//get first day of the month (as sunday, monday ...)
		FirstDay= Month.get(GregorianCalendar.DAY_OF_WEEK);
		//get number of weeks of the current month
		MaxWeekNo=Month.getActualMaximum(GregorianCalendar.WEEK_OF_MONTH);
		//allocate grid cells
		MonthLength=MaxWeekNo*7;
		
		//get last days of previous month
		MaxP=getMaxP();
		CalMaxP=MaxP-(FirstDay-1);	
		PmonthMaxSet=(GregorianCalendar) Pmonth.clone();
		PmonthMaxSet.set(GregorianCalendar.DAY_OF_MONTH,CalMaxP+1);
		
		//add date items to Day String list
		for(int i=0;i<MonthLength;i++){
			ItemValue=DF.format(PmonthMaxSet.getTime());
			PmonthMaxSet.add(GregorianCalendar.DATE,1);
			DayString.add(ItemValue);
		}
	}
	
	//method to get maximum date value of the previous month
	private int getMaxP(){
		int MaxP;
		if(Month.get(GregorianCalendar.MONTH)==Month.getActualMinimum(GregorianCalendar.MONTH)){
			System.out.println("getmaxP month"+Month.get(GregorianCalendar.MONTH));
			Pmonth.set((Month.get(GregorianCalendar.YEAR)-1), Month.getActualMaximum(GregorianCalendar.MONTH),1);	
			System.out.println(Pmonth.get(GregorianCalendar.MONTH));
		}else{
			Pmonth.set(GregorianCalendar.MONTH, Month.get(GregorianCalendar.MONTH)-1);
		}
		MaxP=Pmonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		return MaxP;
	}
	
}
