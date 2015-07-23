package com.lilahammer.mocoreso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ProgressBar;

public class ListeObservationsActivity extends Activity {

	// Définition des variables

	private DataAdapter dbmoco;
	ArrayList<Observation> observations;
	boolean saisons[] = new boolean[4];
	ArrayList<Observation> observationsUpdate;
	private GridView gridview;
	private ProgressBar mProgressBar;
	private Context c;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listeobservation_layout);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		c = getApplicationContext();
		for (int i = 0; i < 4; i++) {
			saisons[i] = true;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		dbmoco = new DataAdapter(this);
		observations = new ArrayList<Observation>();

		observations = dbmoco.getObservations();
		gridview = (GridView) findViewById(R.id.gridview);
		new GetImageData().execute(observations);
		mProgressBar.setVisibility(View.VISIBLE);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent i = new Intent(v.getContext(), UpdateObservation.class);
				String name = observations.get(position).getName();
				i.putExtra("name", name);
				startActivity(i);

			}
		});
	}

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();

		// Check which checkbox was clicked
		switch (view.getId()) {
		case R.id.checkbox_hiver:
			if (checked)
				saisons[3] = true;

			else

				saisons[3] = false;
			break;
		case R.id.checkbox_ete:
			if (checked)
				saisons[1] = true;
			else
				saisons[1] = false;
			break;
		case R.id.checkbox_printemps:
			if (checked)
				saisons[0] = true;
			else
				saisons[0] = false;
			break;
		case R.id.checkbox_automne:
			if (checked)
				saisons[2] = true;
			else
				saisons[2] = false;
			break;

		}
		updateGrid();
	}

	public void updateGrid() {
		observationsUpdate = new ArrayList<Observation>();
		for (int i = 0; i < observations.size(); i++) {
			if (saisons[observations.get(i).getSaisons()])
				observationsUpdate.add(observations.get(i));
		}
		new GetImageData().execute(observationsUpdate);
		mProgressBar.setVisibility(View.VISIBLE);
		// gridview.invalidateViews();
		// gridview.setAdapter(new ImageAdapter(this,observationsUpdate));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (android.R.id.home):
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	// Downloading bitmap asynchronously
	class GetImageData extends
			AsyncTask<ArrayList<Observation>, Void, Bitmap[]> {

		@Override
		protected Bitmap[] doInBackground(ArrayList<Observation>... params) {

			ArrayList<Observation> obs = new ArrayList<Observation>();
			obs = params[0];
			String[] tab_images = new String[obs.size()];
			Bitmap[] images_bitmap = new Bitmap[obs.size()];
			for (int i = 0; i < obs.size(); i++) {
				tab_images[i] = obs.get(i).getPath();
			}
			for (int i = 0; i < obs.size(); i++) {
				// chargement des images
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inJustDecodeBounds = true;
				File file = new File(tab_images[i]);
				Uri bitmapuri = Uri.fromFile(file);

				try {
					int valueInPixels = getResources().getDimensionPixelSize(R.dimen.size_photo);
					images_bitmap[i] = decodeSampledBitmapFromResource(
							bitmapuri, valueInPixels, valueInPixels/2);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

				}
			}

			return images_bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap[] result) {
			if (result != null) {
				gridview.setAdapter(new ImageAdapter(c, result));

			}
			mProgressBar.setVisibility(View.GONE);
		}

		public Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth,
				int reqHeight) throws IOException {

			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			InputStream input = c.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(input, null, options);
			input.close();
			if ((options.outWidth == -1) || (options.outHeight == -1))
				return null;

			// Calculate inSampleSize
			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			input = c.getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
			input.close();
			return bitmap;
		}

		public int calculateInSampleSize(BitmapFactory.Options options,
				int reqWidth, int reqHeight) {
			// Raw height and width of image
			final int height = options.outHeight;
			final int width = options.outWidth;
			int inSampleSize = 1;

			if (height > reqHeight || width > reqWidth) {

				final int halfHeight = height / 2;
				final int halfWidth = width / 2;

				// Calculate the largest inSampleSize value that is a power of 2
				// and
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
}
