package com.lilahammer.mocoreso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdateObservation extends Activity {

	private String oldname;
	private DataAdapter dbmoco;
	private String date_obs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_layout);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		String name = intent.getStringExtra("name");
		oldname = name;
		TextView edittext = (TextView) findViewById(R.id.text);
		dbmoco = new DataAdapter(this);
		Observation observation = dbmoco.getObservationsByName(name);
		edittext.setText(name);
		setDate_obs(observation.getDate());
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		File file = new File(observation.getPath());
		Uri bitmapuri = Uri.fromFile(file);
		try {
			setPic(bitmapuri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.displaymenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// met à jour l'imageButton avec la photo choisie par l'utilisateur
	private void setPic(Uri photoUri) throws FileNotFoundException, IOException {
		if (photoUri == null) {
			
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageView mImageButton = (ImageView) findViewById(R.id.photo);
			int valueInPixels = getResources().getDimensionPixelSize(R.dimen.size_display);
			mImageButton.setImageBitmap(decodeSampledBitmapFromResource(photoUri,valueInPixels,valueInPixels/2));
		}
	}
	public Bitmap decodeSampledBitmapFromResource(Uri uri, int reqWidth,
			int reqHeight) throws IOException {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		InputStream input = this.getContentResolver().openInputStream(uri);
		BitmapFactory.decodeStream(input, null, options);
		input.close();
		if ((options.outWidth == -1) || (options.outHeight == -1))
			return null;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		input = this.getContentResolver().openInputStream(uri);
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
	public Bitmap getImage(Uri uri) throws FileNotFoundException, IOException {
		InputStream input = this.getContentResolver().openInputStream(uri);
		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1)
				|| (onlyBoundsOptions.outHeight == -1))
			return null;
		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;
		double ratio = (originalSize > 1000) ? (originalSize / 500) : 1.0;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = this.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	public void deleteObservation() {
		dbmoco = new DataAdapter(this);
		dbmoco.deleteData(oldname);
		finish();
		
	}

	public void confirmDelete() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					deleteObservation();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				}
			}
		};
		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setMessage("Effacer cette observation ?")
				.setPositiveButton("Oui", dialogClickListener)
				.setNegativeButton("Non", dialogClickListener).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case (android.R.id.home):
			finish();
			return true;
		case (R.id.delete):
			confirmDelete();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public String getDate_obs() {
		return date_obs;
	}

	public void setDate_obs(String date_obs) {
		this.date_obs = date_obs;
	}
}
