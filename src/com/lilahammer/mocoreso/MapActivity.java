package com.lilahammer.mocoreso;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lilahammer.mocoreso.FragmentDate.OnSelectedListener;

public class MapActivity extends Activity implements OnSelectedListener {
	
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_activity);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
		
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment myMapFragment = (MapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		map = myMapFragment.getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		// récupèrer les éléments de la première liste

		DataAdapter dbmoco = new DataAdapter(this);

		String date = dbmoco.getDate(1);

		ArrayList<Observation> Observations = new ArrayList<Observation>();

		Observations = dbmoco.getAllLocalisation(date);
		if (Observations.size()!=0){
		// initialiser par défaut l'affichage de la première liste
		Iterator it = Observations.iterator();

		while (it.hasNext()) {

			Observation tmpobs = (Observation) it.next();

			double latitude = tmpobs.getLatitude();

			double longitude = tmpobs.getLongitude();

			String name = tmpobs.getName();
			Marker marker = map.addMarker(new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(name));

			marker.showInfoWindow();

		}
	

		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
				Observations.get(0).getLatitude(), Observations.get(0)
						.getLongitude()), 13));

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(Observations.get(0).getLatitude(),
						Observations.get(0).getLongitude())) // Sets the center
				.zoom(17) // Sets the zoom
				.bearing(0) // Sets the orientation of the camera to east
				.build(); // Creates a CameraPosition from the builder
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
		

	}

	@Override
	public void onArticleSelected(String date) {
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		map.clear();
		map.setMyLocationEnabled(true);
		
		//récupère les éléments de la liste
		DataAdapter dbmoco = new DataAdapter(this);
		//String date = dbmoco.getDate(index + 1);
		ArrayList<Observation> Observations = new ArrayList<Observation>();
		Observations = dbmoco.getAllLocalisation(date);

		// initialiser par défaut l'affichage de la première liste
		Iterator it = Observations.iterator();
		
		while (it.hasNext()) {
			Observation tmpobs = (Observation) it.next();
			double latitude = tmpobs.getLatitude();

			double longitude = tmpobs.getLongitude();
			String name = tmpobs.getName();
			map.addMarker(new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(name));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case (android.R.id.home) :
			finish();
		default :
			return super.onOptionsItemSelected(item);
		}
		
	}
}
