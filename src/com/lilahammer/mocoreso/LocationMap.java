package com.lilahammer.mocoreso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationMap extends Activity implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerDragListener,
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private GoogleMap myMap;
	private boolean markerClicked;
	private double lat;
	private double lng;
	private LocationClient mLocationClient;

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 60;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	LocationRequest mLocationRequest;
	private Location location;
	private boolean first;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_map_layout);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
		mLocationClient = new LocationClient(this, this, this);
		// Create the LocationRequest object
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		first = true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.location_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	public void onMapClick(LatLng point) {
			
			if (first == true) {
				first = false;
		 myMap.addMarker(new MarkerOptions() .position(point)
		 .draggable(true));
		
		 lat=point.latitude;
		 lng=point.longitude;
		
		
			}
		
		//myMap.addMarker(new MarkerOptions()
		//.position(
		//		new LatLng(location.getLatitude(), location
		//				.getLongitude())).draggable(true));
		
		}
	

	@Override
	public void onMarkerDrag(Marker marker) {

	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		lat = marker.getPosition().latitude;
		lng = marker.getPosition().longitude;
		

	}

	@Override
	public void onMarkerDragStart(Marker marker) {

	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub

	}

	public void envoyerLocalisation() {
		Toast.makeText(this, "localisation" + lat + lng,
				Toast.LENGTH_LONG).show();
		Intent returnIntent = new Intent();
		 
		String stringlatitude = String.valueOf(lat);
		String stringlongitude = String.valueOf(lng);
		returnIntent.putExtra("latitude", stringlatitude);
		returnIntent.putExtra("longitude", stringlongitude);
		setResult(RESULT_OK, returnIntent);
		finish();
	}

	public void retour() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
	
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		
	}

	public void onLocationChanged(Location location) {
		// Report to the UI that the location was updated
		
			FragmentManager myFragmentManager = getFragmentManager();
			MapFragment myMapFragment = (MapFragment) myFragmentManager
					.findFragmentById(R.id.map1);
			myMap = myMapFragment.getMap();
			location = mLocationClient.getLastLocation();
			myMap.setMyLocationEnabled(true);
			myMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			myMap.setOnMapClickListener(this);
			myMap.setOnMapLongClickListener(this);
			myMap.setOnMarkerDragListener(this);
			if (location != null) {
				myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
						location.getLatitude(), location.getLongitude()), 13));

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(location.getLatitude(), location
								.getLongitude())) // Sets the center of the map to
													// location user
						.zoom(17) // Sets the zoom
						.bearing(0) // Sets the orientation of the camera to east
						.build(); // Creates a CameraPosition from the builder
				myMap.animateCamera(CameraUpdateFactory
						.newCameraPosition(cameraPosition));
				
				
			
		}
			
		

	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}
	@Override
	protected void onResume() {
	  super.onResume();
	  if (checkPlayServices()) {
	    // Then we're good to go!
	  }
	}
	private boolean checkPlayServices() {
		  int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		  if (status != ConnectionResult.SUCCESS) {
		    if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
		      showErrorDialog(status);
		    } else {
		      Toast.makeText(this, "Appareil incompatible", 
		          Toast.LENGTH_LONG).show();
		      finish();
		    }
		    return false;
		  }
		  return true;
		} 

		void showErrorDialog(int code) {
		  GooglePlayServicesUtil.getErrorDialog(code, this, 
		      REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
		}
		static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  switch (requestCode) {
		    case REQUEST_CODE_RECOVER_PLAY_SERVICES:
		      if (resultCode == RESULT_CANCELED) {
		        Toast.makeText(this, "Google Play Services doit être installé.",
		            Toast.LENGTH_SHORT).show();
		        finish();
		      }
		      return;
		  }
		  super.onActivityResult(requestCode, resultCode, data);
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item){
			switch(item.getItemId()){
			case (android.R.id.home) :
				finish();
			case (R.id.action_cancel):
				retour();
			case(R.id.action_accept) :
			envoyerLocalisation();
			default :
				return super.onOptionsItemSelected(item);
			}
			
		}
}
