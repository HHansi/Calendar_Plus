package calendar_plus.test;

import android.test.ActivityInstrumentationTestCase2;


import android.widget.ImageView;
import calendar_plus.CalendarActivity;

import com.example.calendar_plus.R;
import com.jayway.android.robotium.solo.Solo;

public class CalendarActivityTest extends
		ActivityInstrumentationTestCase2<CalendarActivity> {
	private Solo solo;

	public CalendarActivityTest() {
		super(CalendarActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	//test application launch 
	public void testStartActivity() {
		solo.assertCurrentActivity("calendar launch", CalendarActivity.class);
	}
	//test previous button
	public void testPrevious(){
		ImageView iv=(ImageView) solo.getView(R.id.IV_previous);
		solo.clickOnView(iv);
	}
	//test next button
	public void testNext(){
		ImageView iv=(ImageView) solo.getView(R.id.IV_next);
		solo.clickOnView(iv);
	}
}
