package com.lilahammer.mocoreso;

import java.util.ArrayList;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentObservations extends ListFragment {

	
	private DataAdapter dbmoco;
	private ObservationAdapter adapter;
	int index;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			//index = savedInstanceState.getInt("index");
			//ArrayList<String> list = new ArrayList<String>();
		    //dbmoco = new DataAdapter (getActivity());
		    //list = dbmoco.getAllDate();
			//AfficherListe(list.get(0));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_observations, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		ArrayList<String> list = new ArrayList<String>();
	    dbmoco = new DataAdapter (getActivity());
	    list = dbmoco.getAllDate();
	    if(list.size() != 0){
	    String s = list.get(0);
	    String annees = s.substring(6, 10);
		String mois = s.substring(3, 5);
		String jour = s.substring(0, 2);
		s = annees + mois + jour;
		AfficherListe(s);}
	}

	public void AfficherListe(String date) {
		dbmoco = new DataAdapter(getActivity());
		//String date = dbmoco.getDate(index + 1);
		ArrayList<Observation> observations = new ArrayList<Observation>();
		observations = dbmoco.getAllLocalisation(date);
		adapter = new ObservationAdapter(getActivity(), R.layout.single_line,
				observations);
		setListAdapter(adapter);

	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Observation observation = (Observation) getListAdapter().getItem(
				position);
		Intent intent = new Intent(getActivity(), UpdateObservation.class);
		intent.putExtra("name", observation.getName());
		startActivity(intent);

	}
}
