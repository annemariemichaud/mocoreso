package com.lilahammer.mocoreso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateObservation extends Activity {

	private Uri fileUri = null;
	private File photoFile ;
	private double lat = 0;
	private double lon = 0;
	private String oldname;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private String mCurrentPhotoPath;
	private DataAdapter dbmoco;
	private String date;
	
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
		File file = new File(observation.getPath());
		photoFile = new File(observation.getPath());
		Uri bitmapuri = Uri.parse(file.toString());
		setPic(bitmapuri);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.displaymenu, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	// met à jour l'imageButton avec la photo choisie par l'utilisateur
	private void setPic(Uri photoUri) {
		if (photoUri == null) {
			Log.d("d", "photouri est null");
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageView mImageButton = (ImageView) findViewById(R.id.photo);
			// Get the dimensions of the View
			/*
			 * int targetW = mImageButton.getWidth(); int targetH =
			 * mImageButton.getHeight();
			 */
			int targetW = 50;
			int targetH = 50;
			// Get the dimensions of the bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;

			// Determine how much to scale down the image
			int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;
			Bitmap bitmap = BitmapFactory.decodeFile(
					imageFile.getAbsolutePath(), bmOptions);

			mImageButton.setImageBitmap(bitmap);
		}
	}

	
	public void deleteObservation() {
		dbmoco = new DataAdapter(this);
		int count = dbmoco.deleteData(oldname);
		ArrayList<Observation> observation_restante = new ArrayList<Observation>();
		observation_restante = dbmoco.getAllLocalisation(date);
		int longueur_liste = observation_restante.size();
		if (longueur_liste == 0)
		{
			dbmoco.deleteDate(date);
		}
		Intent intent = new Intent(this, ListeObservationsActivity.class);
		startActivity(intent);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case (android.R.id.home) :
			finish();
		case (R.id.delete):
			deleteObservation();
			
		default :
			return super.onOptionsItemSelected(item);
		}
		
	}
}
