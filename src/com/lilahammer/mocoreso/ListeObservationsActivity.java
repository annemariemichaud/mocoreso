package com.lilahammer.mocoreso;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.lilahammer.mocoreso.FragmentDate.OnSelectedListener;

public class ListeObservationsActivity extends Activity implements
		OnSelectedListener {

	// Définition des variables
	private FragmentObservations f2;
	private FragmentManager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
	}

	

	@Override
	public void onArticleSelected(String date) {
		manager = getFragmentManager();
		f2 = (FragmentObservations) manager
				.findFragmentById(R.id.fragment_observation);
		f2.AfficherListe(date);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case (android.R.id.home) :
			Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		default :
			return super.onOptionsItemSelected(item);
		}
		
	}
}
