package com.lilahammer.mocoreso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
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

public class AjoutObservation2 extends Activity {

	private Uri fileUri = null;
	private File photoFile = null;
	private double lat = 0;
	private double lon = 0;
	private Location location;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private String mCurrentPhotoPath;
	private DataAdapter dbmoco;
	private int targetW;
	private int targetH;
	private int scaleFactor;
	private Object bmp;
	private Bitmap bitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ajout_observation2_layout);
		
		if(savedInstanceState !=null){ 
			ImageView mImageButton = (ImageView) findViewById(R.id.imageButton1);
			mImageButton.setImageBitmap(bitmap); }
		
		ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
		// retrouver la meilleure localisation enregistrée
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		location = locationManager.getLastKnownLocation(locationManager
				.getBestProvider(criteria, false));
		//Intent intent = getIntent();
		//String imageUri = intent.getStringExtra("imageUri");
		//Uri photoUri = Uri.parse(imageUri);
		//setPic(photoUri);
	}

	// méthode pour lancer l'activité de localisation
	public void locateOnMap(View view) {
		Intent i = new Intent(this, LocationMap.class);
		startActivityForResult(i, 2);
	}

	// méthode pour prendre la photo
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
	
	//récupère les résultats des deux méthodes précédentes
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
		//traitement de la localisation 
		if (requestCode == 2 && resultCode == RESULT_OK) {
			String stringlatitude = data.getStringExtra("latitude");
			String stringlongitude = data.getStringExtra("longitude");
			lat = Double.parseDouble(stringlatitude);
			lon = Double.parseDouble(stringlongitude);
		}
	}

	// creer un nom de fichier unique pour la photo
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "/JPEG_" + timeStamp + "_.jpg";
		String imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
			    getAbsolutePath() + imageFileName;  
			    File imageFile = new File(imageFilePath); 
		//File storageDir = Environment
		//		.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		//if (!storageDir.exists()) {
		//	if (!storageDir.mkdirs()) {
		//		Log.d("moco", "failed to create directory");
		//		return null;
			//}
		//}
		//File image = File.createTempFile(imageFileName, /* prefix */
		//		".jpg", /* suffix */
		//		storageDir /* directory */
		//);
		// Save a file: path for use with ACTION_VIEW intents
		//mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return imageFile;
	}

	private void setPic(Uri photoUri) {
		if (photoUri == null) {
			Log.d("d", "photoUri est null");
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageView mImageButton = (ImageView) findViewById(R.id.imageButton1);
			// Get the dimensions of the View
			
			 targetW = mImageButton.getWidth();
			 targetH = mImageButton.getHeight();
			// Get the dimensions of the bitmap
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(imageFile.getAbsolutePath(), bmOptions);
			int photoW = bmOptions.outWidth;
			int photoH = bmOptions.outHeight;
			Log.d("hauteur",Integer.toString(photoH));
			Log.d("largeur",Integer.toString(photoW));

			// Determine how much to scale down the image
			if (targetW == 0 || targetH ==0)
			{
				Log.d("d","oups");
			}
			 scaleFactor = Math.min(photoW / 100, photoH / 50);
			
			// Decode the image file into a Bitmap sized to fill the View
			bmOptions.inJustDecodeBounds = false;
			bmOptions.inSampleSize = scaleFactor;
			bmOptions.inPurgeable = true;

		 bitmap = BitmapFactory.decodeFile(
					imageFile.getAbsolutePath(), bmOptions);
			if (mImageButton !=null)
			{
				Log.d("bitmap","non null");
			}
			mImageButton.setImageBitmap(bitmap);
		}
	}

	public void saveObservation(View view) {

		dbmoco = new DataAdapter(this);
		EditText mEdit = (EditText) findViewById(R.id.name);
		String name = mEdit.getText().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String currentDateandTime = sdf.format(new Date());
		if (photoFile.getPath() == null) {
			Toast.makeText(this, "Photo requise", Toast.LENGTH_LONG).show();
			return;
		}
		if (name.isEmpty()) {
			Toast.makeText(this, "Nom requis", Toast.LENGTH_LONG).show();
			return;
		}
		dbmoco.insertObservation(name, lat, lon, currentDateandTime,
				photoFile.getPath());
		Toast.makeText(this, "Observation enregistrée",
				Toast.LENGTH_LONG).show();
		finish();
	}

	public void annuler(View view) {
		finish();
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
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   super.onSaveInstanceState(outState);
	   if(photoFile != null){
	  outState.putParcelable("outputFileUri", Uri.fromFile(photoFile));
	   }
	  
	}
	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState){
		fileUri = savedInstanceState.getParcelable("outputFileUri");
		photoFile = new File(fileUri.getPath());
		setPic(Uri.fromFile(photoFile));
	}
}
