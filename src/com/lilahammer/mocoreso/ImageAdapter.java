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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	Bitmap[] images_bitmap;
	ArrayList<Observation> obs;
	int nombre_images;

	public ImageAdapter(Context c, ArrayList<Observation> observations) {
		mContext = c;
		obs = observations;

	}

	public int getCount() {

		nombre_images = obs.size();
		String nombres = "" + nombre_images;
		Log.i("nombre images", nombres);
		return nombre_images;
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

		String[] tab_images = new String[nombre_images];
		String[] tab_Texte = new String[nombre_images];
		images_bitmap = new Bitmap[nombre_images];
		for (int i = 0; i < nombre_images; i++) {

			tab_images[i] = obs.get(i).getPath();
			tab_Texte[i] = obs.get(i).getName();
		}
		Log.i("uri image 1", tab_images[0]);
		for (int i = 0; i < nombre_images; i++) {
			// chargement des images
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			File file = new File(tab_images[i]);
			Uri bitmapuri = Uri.fromFile(file);

			try {
				// images_bitmap[i] = getThumbnail(bitmapuri);
				images_bitmap[i] = decodeSampledBitmapFromResource(bitmapuri,
						150, 150);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

			}
		}

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

	public Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth,
			int reqHeight) throws IOException {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream input = mContext.getContentResolver().openInputStream(uri);
		BitmapFactory.decodeStream(input, null, options);
		input.close();
		if ((options.outWidth == -1) || (options.outHeight == -1))
			return null;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		input = mContext.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
		input.close();
		return bitmap;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

}