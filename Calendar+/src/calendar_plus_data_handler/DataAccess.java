package calendar_plus_data_handler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DataAccess {

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns={MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_SUBJECT,
			MySQLiteHelper.COLUMN_DESCRIPTION, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_LAT,
			MySQLiteHelper.COLUMN_LNG, MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_DURATION};
			
	
	public DataAccess(Context context){
		dbHelper=new MySQLiteHelper(context);
	}
	public void close(){
		dbHelper.close();
	}
	
	//method to open db
	public void open() throws SQLException{
		database=dbHelper.getWritableDatabase();
	}
	
	//add new event to the database
	 public void addEvent(Event event){
		 //check whether there is an event saved for the given time slot
		 if(getEvent(event.getDate(),event.getTime())!=null){
			// this.deleteEvent(event);
			 this.deleteEvent(event.getDate(),event.getTime());
		 }
		ContentValues values=new ContentValues();
		//put event details into content values 
		values.put(MySQLiteHelper.COLUMN_SUBJECT, event.getSubject());
		values.put(MySQLiteHelper.COLUMN_DESCRIPTION, event.getDescription());
		values.put(MySQLiteHelper.COLUMN_LOCATION, event.getLocation());
		values.put(MySQLiteHelper.COLUMN_LAT, event.getLat());
		values.put(MySQLiteHelper.COLUMN_LNG, event.getLng());
		values.put(MySQLiteHelper.COLUMN_DATE, event.getDate());
		values.put(MySQLiteHelper.COLUMN_TIME, event.getTime());
		values.put(MySQLiteHelper.COLUMN_DURATION, event.getDuration());
		values.put(MySQLiteHelper.COLUMN_ARRIVAL_SUPPORT_ENABLED, 0);
		
		//open db
		open();
		//insert details
		database.insert(MySQLiteHelper.TABLE_EVENTS, null, values);
		//close db
		database.close();
	}
	 
	 //get all events stored in the database
	 public List<Event> getAllEvents(){
		 List<Event> events=new ArrayList<Event>();
		 //open db
		 open();
		 Cursor cursor=database.query(MySQLiteHelper.TABLE_EVENTS, allColumns, null, null, null, null, null);
		 cursor.moveToFirst();
		 //
		 while(!cursor.isAfterLast()){
			 Event event=CursorToEvent1(cursor);
			 events.add(event);
			 cursor.moveToNext();
		 }
		 cursor.close();
		 return events;
	 }
	 
	 private Event CursorToEvent1(Cursor cursor){
		 Event event = new Event();
		 //event.setId(cursor.getLong(0));
		 event.setSubject(cursor.getString(1));
		 event.setDescription(cursor.getString(2));
		 event.setLocation(cursor.getString(3));
		 event.setLat(cursor.getFloat(4));
		 event.setLng(cursor.getFloat(5));
		 event.setDate(cursor.getString(6));
		 event.setTime(cursor.getString(7));
		 event.setDuration(cursor.getLong(8));
		 
		 return event;
	 }
	 
	 //read a row according to given date time
	 public Event getEvent(String date, String time){
		 //open db
		 open();
		 //set columns which should read
		 String[] columns={MySQLiteHelper.COLUMN_SUBJECT, MySQLiteHelper.COLUMN_LOCATION,MySQLiteHelper.COLUMN_LAT,
				 MySQLiteHelper.COLUMN_LNG };
		 //implement db query
		 Cursor cursor=database.query(MySQLiteHelper.TABLE_EVENTS, columns,"date=? AND time=? ", 
				 new String[] {date, time}, null, null, null);
		 
		 //if cursor has rows (data)
		 if(cursor.getCount()!=0){
			 cursor.moveToFirst();

		 //set event attributes
		 Event event=new Event();
		 event.setSubject(cursor.getString(0));
		 event.setLocation(cursor.getString(1));
		 event.setLat(cursor.getDouble(2));
		 event.setLng(cursor.getDouble(3));
		 //close
		 cursor.close();
		 database.close();
		 
		return event;	
		 }else{//if no data read
			 return null;
		 }
	 }
	 
	 //get all data from the database
	 public List<Event> getAll(){
		 List<Event> events=new ArrayList<Event>();
		 String[] columns={MySQLiteHelper.COLUMN_SUBJECT,MySQLiteHelper.COLUMN_DESCRIPTION, MySQLiteHelper.COLUMN_LOCATION,MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TIME};
		 open();
		 Cursor cursor=database.query(MySQLiteHelper.TABLE_EVENTS, columns, null, null, null, null, null);
		 cursor.moveToFirst();
		 while(!cursor.isAfterLast()){
			 Event event=CursorToEvent2(cursor);
			 events.add(event);
			 cursor.moveToNext();
		 }
		 cursor.close();
		 close();
		 return events;
	 }
	 
	 //covertion for all daa reading
	 private Event CursorToEvent2(Cursor cursor){
		 Event event = new Event();
		 //event.setId(cursor.getLong(0));
		 event.setSubject(cursor.getString(0));
		 event.setDescription(cursor.getString(1));
		 event.setLocation(cursor.getString(2));
		// event.setDate_Time(cursor.getString(3));
		 event.setDate(cursor.getString(3));
		 event.setTime(cursor.getString(4));
		 
		 return event;
	 }
	 
	 //get events in db according to date
	 public List<Event> getEventByDate(String date){
		 List<Event> events=new ArrayList<Event>();
		 open();

		 String[] columns={MySQLiteHelper.COLUMN_SUBJECT, MySQLiteHelper.COLUMN_DESCRIPTION,MySQLiteHelper.COLUMN_LOCATION,
				 MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_ARRIVAL_SUPPORT_ENABLED};
		 
		 Cursor cursor=database.query(MySQLiteHelper.TABLE_EVENTS, columns,"date=? ", new String[] {date}, null, null, null);
		 cursor.moveToFirst();
		 while(!cursor.isAfterLast()){
			 Event event=CursorToEvent(cursor);
			 events.add(event);
			 cursor.moveToNext();
		 }
		 cursor.close();
		 close();
		 
		return events;	 
	 }
	 
	 private Event CursorToEvent(Cursor cursor){
		 Event event = new Event();
		 //event.setId(cursor.getLong(0));
		 event.setSubject(cursor.getString(0));
		 event.setDescription(cursor.getString(1));
		 event.setLocation(cursor.getString(2));
		 event.setDate(cursor.getString(3));
		 event.setTime(cursor.getString(4));
		 event.setASE(cursor.getString(5));
		 
		 return event;
	 }
	 
	 //delete appointment form db according to date time
	 public void deleteEvent(String date, String time) {
		 //open db
		 open();
		 //delete
		 database.delete(MySQLiteHelper.TABLE_EVENTS, "date=? AND time=? " ,
		            new String[] { date, time });
		 //close
		 close();
		}
	 
	 //method to get number of event of a particular day
	 public int getNoOfEventsByDate(String date){
		 open();
		
		 String[] columns={MySQLiteHelper.COLUMN_SUBJECT, MySQLiteHelper.COLUMN_DESCRIPTION,MySQLiteHelper.COLUMN_LOCATION,
				 MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_TIME};
		 Cursor cursor=database.query(MySQLiteHelper.TABLE_EVENTS, columns,"date=? ", new String[] {date}, null, null, null);
		 
		 int no_of_events=cursor.getCount();;
		 close();
		 //return no of the rows of the cursor
		 return no_of_events;
	 }
	 
	 //set arrival support enabled boolean when notification generated for the event
	 public void updateArrivalSupport(String date, String time){	
		 //open 
		 open();
		 //set value of the corresponding column which should update
		 ContentValues values = new ContentValues();
		 values.put(MySQLiteHelper.COLUMN_ARRIVAL_SUPPORT_ENABLED, 1);
		 //update row
		 database.update(MySQLiteHelper.TABLE_EVENTS, values, "date=? AND time=? " , 
				 new String[] {date, time});
		 //close
		 close();
	 }
	 
	 
}
