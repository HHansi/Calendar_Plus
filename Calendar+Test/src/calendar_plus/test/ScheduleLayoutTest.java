package calendar_plus.test;

import calendar_plus.Schedule;

import com.example.calendar_plus.R;
import com.jayway.android.robotium.solo.Solo;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

public class ScheduleLayoutTest extends
		ActivityInstrumentationTestCase2<Schedule> {
	Solo solo;

	public ScheduleLayoutTest() {
		super(Schedule.class);
	}
	
	//overide getActivity method and put extras
	@Override
	public Schedule getActivity() {
	    Intent intent = new Intent();
	    
		intent.putExtra("selectedGridDate", "2014-09-10");
	    
	    setActivityIntent(intent);
	    return super.getActivity();
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	//test application launch 
	public void testStartActivity() {
		solo.assertCurrentActivity("calendar launch", Schedule.class);
	}	
	//test next button
	public void testNext(){
		ImageView iv=(ImageView) solo.getView(R.id.IV_grid);
		solo.clickOnView(iv);
	}

}




