package calendar_plus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeocodeJSONParser {
	
	// Receives a JSONObject and returns a list 
	public List<HashMap<String,String>> parse(JSONObject jObject){		
		
		JSONArray jPlaces = null;
		try {			
			// Retrieves all the elements in the 'places' array 
			jPlaces = jObject.getJSONArray("results");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		/* Invoking getPlaces with the array of json object
		 * where each json object represent a place
		 */
		return getPlaces(jPlaces);
	}
	
	
	private List<HashMap<String, String>> getPlaces(JSONArray jPlaces){
		int placesCount = jPlaces.length();
		List<HashMap<String, String>> placesList = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> place = null;	

		// Taking each place, parses and adds to list object 
		for(int i=0; i<placesCount;i++){
			try {
				// Call getPlace with place JSON object to parse the place 
				place = getPlace((JSONObject)jPlaces.get(i));
				placesList.add(place);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return placesList;
	}
	
	// Parse the Place JSON object to a HashMap
	private HashMap<String, String> getPlace(JSONObject jPlace){
		
		HashMap<String, String> place = new HashMap<String, String>();
		String formatted_address = "-NA-";		
		String lat="";
		String lng="";
						
		try {
			// Extracting formatted address, if available
			if(!jPlace.isNull("formatted_address")){
				formatted_address = jPlace.getString("formatted_address");
			}
			
			//get latitude and longitude from jPlace
			lat = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
			lng = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");						
			
			//add data to HashMap
			place.put("formatted_address", formatted_address);			
			place.put("lat", lat);
			place.put("lng", lng);		
			
		} catch (JSONException e) {			
			e.printStackTrace();
		}		
		return place;
	}
}

