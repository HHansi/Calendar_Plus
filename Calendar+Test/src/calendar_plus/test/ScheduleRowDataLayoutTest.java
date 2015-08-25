package calendar_plus.test;

import calendar_plus.ScheduleManager;

import com.jayway.android.robotium.solo.Solo;
import com.example.calendar_plus.R;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

public class ScheduleRowDataLayoutTest extends
		ActivityInstrumentationTestCase2<ScheduleManager> {
	
	private Solo solo;
	
	public ScheduleRowDataLayoutTest() {
		super(ScheduleManager.class);
		// TODO Auto-generated constructor stub
	}
	
	//overide getActivity method and put extras
	@Override
	public ScheduleManager getActivity() {
	    Intent intent = new Intent();
	    
		intent.putExtra("Subject", "test");
		intent.putExtra("Description", "");
		intent.putExtra("Location", "Piliyandala");
		intent.putExtra("ASE", "1" );	
		intent.putExtra("SelectedDate", "2014-09-10");
		intent.putExtra("Time", "3:00");
		intent.putExtra("parent_name", "Schedule");
	    
	    setActivityIntent(intent);
	    return super.getActivity();
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo=new Solo(getInstrumentation(), getActivity());
	}
	
	//test start of the activity
	public void testStartActivity(){
		solo.assertCurrentActivity("check ScheduleManager launch" , ScheduleManager.class);
	}
	
	//test save image view
	public void testSave(){
		ImageView iv= (ImageView) solo.getView(R.id.save);
		solo.clickOnView(iv);
	//	solo.assertCurrentActivity("directed to schedule", Schedule.class);
	}	
	//test delete image view
	public void testDelete(){
		ImageView iv=(ImageView) solo.getView(R.id.delete);
		solo.clickOnView(iv);
	}

} 
