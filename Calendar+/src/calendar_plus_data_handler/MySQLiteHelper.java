package calendar_plus_data_handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	
	public static final String TABLE_EVENTS="events";
	
	//define and initialize column names
	public static final String COLUMN_ID="id";
	public static final String COLUMN_SUBJECT="subject";
	public static final String COLUMN_DESCRIPTION="description";
	public static final String COLUMN_LOCATION="location";
	public static final String COLUMN_LAT="lat";
	public static final String COLUMN_LNG="lng";
	public static final String COLUMN_DATE="date";
	public static final String COLUMN_TIME="time";
	public static final String COLUMN_DURATION="duration";
	public static final String COLUMN_ARRIVAL_SUPPORT_ENABLED="arrival_support_enabled";
	
	private static final String DATABASE_NAME="events.db";
	private static final int DATABASE_VERSION=1;
	
	//database creation sql statement
	private static final String DATABASE_CREATE="create table "+ TABLE_EVENTS+
			"("+ COLUMN_ID+ " integer primary key autoincrement, " + COLUMN_SUBJECT+
			" text not null, "+ COLUMN_DESCRIPTION + " text, "+ COLUMN_LOCATION+
			" text, " + COLUMN_LAT + " double, " + COLUMN_LNG + " double, " 
			+ COLUMN_DATE+ " text not null, " + COLUMN_TIME+ " text not null, "
			+ COLUMN_DURATION +" integer, " + COLUMN_ARRIVAL_SUPPORT_ENABLED + " boolean );";

			
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		System.out.println(DATABASE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXIST "+ TABLE_EVENTS);
		onCreate(db);
		
	}

}
