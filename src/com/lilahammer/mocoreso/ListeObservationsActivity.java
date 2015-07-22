package com.lilahammer.mocoreso;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.Toast;

import com.lilahammer.mocoreso.FragmentDate.OnSelectedListener;

public class ListeObservationsActivity extends Activity  {

	// Définition des variables

	private DataAdapter dbmoco;
	ArrayList<Observation> observations;
	boolean saisons[] = new boolean [4];
	ArrayList<Observation> observationsUpdate;
	private GridView gridview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_activity);
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        for (int i=0;i<4;i++){
    		saisons[i]=true;
    	}
        dbmoco = new DataAdapter (this);
        observations = new ArrayList<Observation>();
        
    	observations = dbmoco.getObservations();
        gridview = (GridView) findViewById(R.id.gridview);
        
        gridview.setAdapter(new ImageAdapter(this,observations));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
            	Intent i = new Intent(v.getContext(),UpdateObservation.class);
            	String name = observations.get(position).getName();
            	i.putExtra("name",name);
            	startActivity(i);
                //Toast.makeText(ListeObservationsActivity.this, "" + position,
                  //      Toast.LENGTH_SHORT).show();
            }
        });

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
		for (int i=0;i<observations.size();i++){
			if (saisons[observations.get(i).getSaisons()])
				observationsUpdate.add(observations.get(i));
		}
		gridview.invalidateViews();
		gridview.setAdapter(new ImageAdapter(this,observationsUpdate));
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
