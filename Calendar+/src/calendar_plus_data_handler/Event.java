package calendar_plus_data_handler;


public class Event {
	private String subject;
	private String location;
	private String description;
	private double lat;
	private double lng;
	
	private String date;
	private String time;
	private long duration;
	private String arrival_support_enabled;
	
	public void setSubject(String Subject){
		subject=Subject;
	}
	public void setLocation(String Location){
		location=Location;
	}
	public void setDescription(String Description){
		description=Description;
	}
	public void setLat(double Lat){
		lat=Lat;
	}
	public void setLng(double Lng){
		lng=Lng;
	}
	public void setDate(String date){
		this.date=date;
	}
	public void setTime(String time){
		this.time=time;
	}
		
       

	public void setDuration(long Duration){
		duration=Duration;
	}
	public void setASE(/*boolean ase*/ String ase){
		arrival_support_enabled=ase;
		//arrival_support_enabled=Boolean.getBoolean(ase);
	}
	

	public String getSubject(){
		return subject;
	}
	public String getLocation(){
		return location;
	}
	public String getDescription(){
		return description;
	}
	public double getLat(){
		return lat;
	}
	public double getLng(){
		return lng;
	}

	
	public String getDate(){
		return date;
	}
	public String getTime(){
		return time;
	}
	
	
	public long getDuration(){
		return duration;
	}
	public String getASE(){
		return arrival_support_enabled;
	}
}
