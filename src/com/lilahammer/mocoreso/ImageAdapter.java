package com.lilahammer.mocoreso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
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

	public int getCount() {

		return images_bitmap.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
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
			gridView = (View) convertView;
		}

		return gridView;
	}

	

	
}

