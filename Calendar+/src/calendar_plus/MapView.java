package calendar_plus;


import com.example.calendar_plus.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MapView extends FragmentActivity {
	private GoogleMap map;
	//private Location CurrentLocation;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapview);
		
		SupportMapFragment mf= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		
		//check whether the device installed google services or not
		if(isGoogleMapsInstalled()){
			map=mf.getMap(); //getting map
		}
		else{//if service not installed show toast
			Toast.makeText(getBaseContext(), "Google services not installed", Toast.LENGTH_SHORT).show();
		}
		
		//enable my location button
		map.setMyLocationEnabled(true);
		
		//this.CurrentLocation=map.getMyLocation();
		
		//get lat,lng and name of the entered location from intent
		Intent intent=getIntent();
		double lat=intent.getDoubleExtra("lat", 0);
		double lng=intent.getDoubleExtra("lng", 0);
		String location=intent.getStringExtra("location");
		
		// Clears all the existing markers	on map		
		map.clear();
		
		// Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();
        
        LatLng latLng = new LatLng(lat, lng);
        
        // Set the position for the marker
        markerOptions.position(latLng);
        
        // Set the title for the marker
        markerOptions.title(location);

        // Placing a marker on the position
        map.addMarker(markerOptions);    
        
        //zoom into the enterd location
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(yourLocation);

	}
	
		//method to check whether the device has installed Google maps
		private boolean isGoogleMapsInstalled()
		{
		    try
		    {
		        @SuppressWarnings("unused")
				ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
		        return true;
		    } 
		    catch(PackageManager.NameNotFoundException e)
		    {
		        return false;
		    }
		}
}
	
	
