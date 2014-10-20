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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
		EditText edittext = (EditText) findViewById(R.id.name);
		dbmoco = new DataAdapter(this);
		Observation observation = dbmoco.getObservationsByName(name);
		edittext.setText(name);
		File file = new File(observation.getPath());
		photoFile = new File(observation.getPath());
		lat = observation.getLatitude();
		lon=observation.getLongitude();
		date=observation.getDate();
		Uri bitmapuri = Uri.parse(file.toString());
		setPic(bitmapuri);
	}

	public void locateOnMap(View view) {
		Intent i = new Intent(this, LocationMap.class);
		startActivityForResult(i, 2);
	}

	public void TakePicture(View view) {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Log.d("d", "probleme creation fichier");
			}
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.fromFile(photoFile)));
				//"file://"
				//+ Environment.getExternalStorageDirectory())
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//traitement de la photo 
				if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
				//	Uri photoUri = null;
					if (photoFile != null) {
						// A known bug here! The image should have saved in fileUri
						Toast.makeText(this, "Image sauvegardée",
								Toast.LENGTH_LONG).show();
						//photoUri = fileUri;

					} else {
						//photoUri = data.getData();
						Toast.makeText(this,
								"Image not saved " //+ data.getData()
								,Toast.LENGTH_LONG).show();
					}
					setPic(Uri.fromFile(photoFile));
				}
		if (requestCode == 2 && resultCode == RESULT_OK) {
			String stringlatitude = data.getStringExtra("latitude");
			String stringlongitude = data.getStringExtra("longitude");
			lat = Double.parseDouble(stringlatitude);
			lon = Double.parseDouble(stringlongitude);
		}

	}

	// creer un nom de fichier unique pour la photo
	private File createImageFile() throws IOException {
		// / Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "/JPEG_" + timeStamp + "_.jpg";
		String imageFilePath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ imageFileName;
		File imageFile = new File(imageFilePath);
		// File storageDir = Environment
		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		// if (!storageDir.exists()) {
		// if (!storageDir.mkdirs()) {
		// Log.d("moco", "failed to create directory");
		// return null;
		// }
		// }
		// File image = File.createTempFile(imageFileName, /* prefix */
		// ".jpg", /* suffix */
		// storageDir /* directory */
		// );
		// Save a file: path for use with ACTION_VIEW intents
		// mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return imageFile;
	}

	// met à jour l'imageButton avec la photo choisie par l'utilisateur
	private void setPic(Uri photoUri) {
		if (photoUri == null) {
			Log.d("d", "photouri est null");
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageView mImageButton = (ImageView) findViewById(R.id.imageButton1);
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

	public void updateObservation(View view) {
		dbmoco = new DataAdapter(this);
		EditText mEdit = (EditText) findViewById(R.id.name);
		String name = mEdit.getText().toString();
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//String currentDateandTime = sdf.format(new Date());
		if (name.isEmpty()) {
			Toast.makeText(this, "Nom requis", Toast.LENGTH_LONG).show();
			return;
		}
		if (photoFile.getPath() == null) {
			Toast.makeText(this, "Photo requise", Toast.LENGTH_LONG).show();
			return;
		}
		if (lat == 0 || lon == 0) {
			Toast.makeText(this, "Localisation requise", Toast.LENGTH_LONG)
					.show();
			return;
		}
		
		int count = dbmoco.updatedata(oldname, name, lat, lon,
				date, photoFile.getPath());
		Intent intent = new Intent(this, ListeObservationsActivity.class);
		startActivity(intent);
		//finish();
	}
	
	public void deleteObservation(View view) {
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
		//finish();
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
