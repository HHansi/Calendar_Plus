package calendar_plus;

public class ScheduleItem {
	private String time_slot;
	private String desc;
	private int index_of_selectedEventList;
	private boolean show_check_box;
	
	public ScheduleItem(String timeSlot, String desc, int index){
		this.time_slot=timeSlot;
		this.desc=desc;
		this.index_of_selectedEventList=index;
		this.show_check_box=false;
	}
	
	public void setTime_slot(String time){
		this.time_slot=time;
	}
	
	public void setDesc(String desc){
		this.desc=desc;
	}
	
	public void setindex_of_selectedEventList(int index){
		index_of_selectedEventList=index;
	}
	
	public void setShow_check_box(boolean set){
		this.show_check_box=set;
	}
	
	public String getTime_Slot(){
		return time_slot;
	}
	
	public String getDesc(){
		return desc;
	}
	
	public int getindex_of_selectedEventList(){
		return index_of_selectedEventList;
	}
	
	public boolean getShow_check_box(){
		return show_check_box;
	}
}
