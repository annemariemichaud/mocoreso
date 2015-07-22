package com.lilahammer.mocoreso;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

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
	private boolean saisons[];
	private ArrayList<Observation> Observations;
	private ArrayList<Observation> observationsUpdate;
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

		// récupèrer tous les éléments
		saisons = new boolean[4];
		DataAdapter dbmoco = new DataAdapter(this);

		 Observations = new ArrayList<Observation>();

		Observations = dbmoco.getObservations();
		if (Observations.size()!=0){
		// initialiser par défaut l'affichage de la première liste
		Iterator<Observation> it = Observations.iterator();
		Marker tab [] = new Marker [Observations.size()];
		int i=0;

		while (it.hasNext()) {

			Observation tmpobs = (Observation) it.next();

			double latitude = tmpobs.getLatitude();

			double longitude = tmpobs.getLongitude();

			String name = tmpobs.getName();
			tab[i]=  map.addMarker(new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(name));

			tab[i].showInfoWindow();

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
		Observations = dbmoco.getObservations();
		Marker tab[] = new Marker[Observations.size()];
		int i=0;
		// initialiser par défaut l'affichage de la première liste
		Iterator it = Observations.iterator();
		
		while (it.hasNext()) {
			Observation tmpobs = (Observation) it.next();
			double latitude = tmpobs.getLatitude();

			double longitude = tmpobs.getLongitude();
			String name = tmpobs.getName();
			tab[i] = map.addMarker(new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(name));
			tab[i].showInfoWindow();
			i=i+1;
		}
	}
	
	public void onCheckboxClicked(View view) {
	    // Is the view now checked?
	    boolean checked = ((CheckBox) view).isChecked();
	    
	    // Check which checkbox was clicked
	    switch(view.getId()) {
	        case R.id.checkbox_hiver:
	            if (checked)
	               saisons[3]=true;
	            	
	            else
	            
	            	saisons[3]=false;
	            break;
	        case R.id.checkbox_ete:
	            if (checked)
	              saisons[1]=true;
	            else
	            	saisons[1]=false;
	            	break;
	        case R.id.checkbox_printemps:
	            if (checked)
	               saisons[0]=true;
	            else
	            	saisons[0]=false;
	            	break;
	        case R.id.checkbox_automne:
	            if (checked)
	               saisons[2]=true;
	            else
	            	saisons[2]=false;
	            break;
	       
	    }
	    updateGrid();
	}
	public void updateGrid() {
		observationsUpdate = new ArrayList<Observation>();
		for (int i=0;i<Observations.size();i++){
			if (saisons[Observations.get(i).getSaisons()])
				observationsUpdate.add(Observations.get(i));
		}
		GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.map)).getMap();
		map.clear();
		map.setMyLocationEnabled(true);
		
		Marker tab[] = new Marker[observationsUpdate.size()];
		int i=0;
		// initialiser par défaut l'affichage de la première liste
		Iterator it = observationsUpdate.iterator();
		
		while (it.hasNext()) {
			Observation tmpobs = (Observation) it.next();
			double latitude = tmpobs.getLatitude();

			double longitude = tmpobs.getLongitude();
			String name = tmpobs.getName();
			tab[i] = map.addMarker(new MarkerOptions().position(
					new LatLng(latitude, longitude)).title(name));
			tab[i].showInfoWindow();
			i=i+1;
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
