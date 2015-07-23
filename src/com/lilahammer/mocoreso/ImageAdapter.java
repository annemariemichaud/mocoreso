package com.lilahammer.mocoreso;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	Bitmap[] images_bitmap;
	ArrayList<Observation> obs;
	int nombre_images;

	public View mProgressBar;

	public ImageAdapter(Context c, Bitmap[] images) {
		mContext = c;
		images_bitmap = images;

	}

	@Override
	public int getCount() {

		return images_bitmap.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;
		 
		ImageView imageView;
		if (convertView == null) {
			
			// get layout from mobile.xml
			gridView = inflater.inflate(R.layout.element_liste, null);

			// set image based on selected text
			imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
			imageView.setImageBitmap(images_bitmap[position]);

		} else {
			gridView = convertView;
		}

		return gridView;
	}

	

	
}

