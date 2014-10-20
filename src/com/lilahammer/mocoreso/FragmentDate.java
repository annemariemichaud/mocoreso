package com.lilahammer.mocoreso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FragmentDate extends ListFragment implements AdapterView.OnItemClickListener{
	
	private DataAdapter dbmoco;
	private StableArrayAdapter adapter;
	private OnSelectedListener mListener;
	private View view;
	
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			
			view = inflater.inflate(R.layout.fragment_date,container,false);
			return view;
		}
		
		
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			//initalisation de la listview
		    ArrayList<String> list = new ArrayList<String>();
		    dbmoco = new DataAdapter (getActivity());
		    list = dbmoco.getAllDate();
		    adapter = new StableArrayAdapter(getActivity(),
			        android.R.layout.simple_list_item_1, list);
			 setListAdapter(adapter);
			 getListView().setOnItemClickListener(this);
		}
		
		//vérification de l'implémentation de OnSelectedListener dans ListActivity
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        try {
	        mListener = (OnSelectedListener) activity;
	        } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement OnSelectedListener");
	        }
	    }
		
	//définition de l'adapter pour FragmentDate
		  public class StableArrayAdapter extends ArrayAdapter<String> {			
			    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();			    
			    public StableArrayAdapter(Context context, int textViewResourceId,
			        List<String> objects) {
			      super(context, textViewResourceId, objects);
			      for (int i = 0; i < objects.size(); ++i) {
			        mIdMap.put(objects.get(i), i);			        
			      }
			    }
			    @Override
			    public long getItemId(int position) {
			      String item = getItem(position);
			      return mIdMap.get(item);
			    }			 
			    @Override
			    public void add(String object) {
			    // TODO Auto-generated method stub
			    super.add(object);			    
			    int i = mIdMap.size();
			    mIdMap.put(object, i);		    
			    }
			    @Override
			    public boolean hasStableIds() {
			      return true;
			  }		
		}

		//passe l'index à l'activité  
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			// TODO Auto-generated method stub
			//mListener.onArticleSelected(arg2);
			String s = ((TextView)arg1.findViewById(android.R.id.text1)).getText().toString();
			String annees = s.substring(6, 10);
			String mois = s.substring(3, 5);
			String jour = s.substring(0, 2);
			s = annees + mois + jour;
			mListener.onArticleSelected(s);
		}
		
		/* Container Activity must implement this interface
	    public interface OnSelectedListener {
	        public void onArticleSelected(int index);
	    }*/
	 // Container Activity must implement this interface
	    public interface OnSelectedListener {
	        public void onArticleSelected(String date);
	    }

}
