package com.lilahammer.mocoreso;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddObservation extends FragmentActivity implements OnMapReadyCallback,ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private Uri fileUri = null;
	private File photoFile = null;
	private double lat = 0;
	private double lon = 0;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private DataAdapter dbmoco;
	private int targetW;
	private int targetH;
	private int scaleFactor;
	private Bitmap bitmap;
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;
	

	private GoogleMap map_ref;
	
	String IMAGE_BUNDLE_KEY = "image";
	private String imageFilePath;
	private MediaScannerConnection conn; 
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ajout_observation2_layout);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		View mActionBarView = getLayoutInflater().inflate(R.layout.location_bar, null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView textview_annuler= (TextView)findViewById(R.id.annuler);
		textview_annuler.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
               finish();

            }
        });
		TextView textview_enregistrer= (TextView)findViewById(R.id.enregistrer);
		textview_enregistrer.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
               saveObservation();

            }
        });
		if(savedInstanceState !=null){ 
			bitmap = (Bitmap)savedInstanceState.get(IMAGE_BUNDLE_KEY);
			if (bitmap != null) {
			ImageButton mImageButton = (ImageButton) findViewById(R.id.add_photo);
			mImageButton.setImageBitmap(bitmap); }
		}
		
		MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		
		buildGoogleApiClient();
		
	}
	
	
	protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
	
	 @Override
	    protected void onStart() {
	        super.onStart();
	        mGoogleApiClient.connect();
	    }
	 	
	    @Override
	    protected void onStop() {
	        super.onStop();
	        if (mGoogleApiClient.isConnected()) {
	            mGoogleApiClient.disconnect();
	        }
	    }
	    
	    @Override
		protected void onPause() {
		    super.onPause();
		    if (mGoogleApiClient.isConnected()){
		    stopLocationUpdates();
		    }
		}
	    
	    @Override
		public void onResume() {
		    super.onResume();
		    if (mGoogleApiClient.isConnected()) {
		        startLocationUpdates();
		    }
		}
	    
	   
		
	    @Override
	    public void onConnected(Bundle connectionHint) {
	    	mLocationRequest = LocationRequest.create();
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	        mLocationRequest.setInterval(1000);

	        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	    	
	        

	    }
	    
	    protected void startLocationUpdates() {
	        LocationServices.FusedLocationApi.requestLocationUpdates(
	                mGoogleApiClient, mLocationRequest, this);
	    }
	    
	    @Override
	    public void onLocationChanged(Location location) {
	        mCurrentLocation = location;
	        updateUI();
	    }

	    private void updateUI() {
	    	
	    	if (mCurrentLocation != null){
	    	LatLng observation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
	    	lat = mCurrentLocation.getLatitude();
	    	lon = mCurrentLocation.getLongitude();
	        map_ref.setMyLocationEnabled(true);
	        map_ref.moveCamera(CameraUpdateFactory.newLatLngZoom(observation, 13));

	        map_ref.addMarker(new MarkerOptions()
	                .position(observation));}
	    	
	    	
	    	

	     //   mLatitudeTextView.setText(String.valueOf(mCurrentLocation.getLatitude()));
	       // mLongitudeTextView.setText(String.valueOf(mCurrentLocation.getLongitude()));
	        //LastUpdateTimeTextView.setText(mLastUpdateTime);
	    }

	    @Override
	    public void onConnectionFailed(ConnectionResult result) {
	       
	        Log.i("problème localisation", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
	    }

	    @Override
	    public void onConnectionSuspended(int cause) {
	        // The connection to Google Play services was lost for some reason. We call connect() to
	        // attempt to re-establish the connection.
	        Log.i("problème connexion", "Connection suspended");
	        mGoogleApiClient.connect();
	    }
	
	    protected void createLocationRequest() {
	        LocationRequest mLocationRequest = new LocationRequest();
	        mLocationRequest.setInterval(10000);
	        mLocationRequest.setFastestInterval(5000);
	        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    }
	
	public void onMapReady(GoogleMap map) {
		map_ref = map;
		 
	}
	
	protected void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(
	            mGoogleApiClient, this);
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
	// méthode pour prendre la photo
		public void TakePicture(View view) {
			if (photoFile !=null) {
				photoFile.delete();
			}
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
					MediaScannerConnection.scanFile(getApplicationContext(), new String[] { photoFile.getAbsolutePath() }, null, new OnScanCompletedListener() {

		                @Override
		                public void onScanCompleted(String path, Uri uri) {
		                    // TODO Auto-generated method stub

		                }
		            });
					//sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE ,
					//	Uri.fromFile(photoFile)));
					
				}
			}
		}
	// creer un nom de fichier unique pour la photo
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "/JPEG_" + timeStamp + "_.jpg";
		
		imageFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
			    getAbsolutePath() + imageFileName;  
			    File imageFile = new File(imageFilePath); 
		return imageFile;
	}

	private void setPic(Uri photoUri) {
		if (photoUri == null) {
			Log.d("d", "photoUri est null");
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageButton mImageButton = (ImageButton) findViewById(R.id.add_photo);
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

	public void saveObservation() {
			
		EditText mEdit = (EditText) findViewById(R.id.name);
		String name = mEdit.getText().toString();
		if (photoFile != null  && !name.isEmpty() && (lat !=0 || lon !=0)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String currentDateandTime = sdf.format(new Date());
			dbmoco = new DataAdapter(this);
			dbmoco.insertObservation(name, lat, lon, currentDateandTime,
				photoFile.getPath());
		Toast.makeText(this, "Observation enregistrée",
				Toast.LENGTH_LONG).show();
		finish();}
		else {
			if (photoFile == null) {
				Toast.makeText(this, "Photo requise", Toast.LENGTH_LONG).show();
				return;
			}
			if (name.isEmpty()) {
				Toast.makeText(this, "Nom requis", Toast.LENGTH_LONG).show();
				return;
			}
			if (lat ==0 || lon ==0) {
				Toast.makeText(this, "Localisation requise", Toast.LENGTH_LONG).show();
				return;
			}
		}
	}

	public void annuler(View view) {
		finish();
	}

/*public void onSavedInstance(Bundle b){
	if (bitmap != null){
	b.putParcelable(IMAGE_BUNDLE_KEY, bitmap);}
}
	

public void onRestoreInstance(Bundle b){
	if(bitmap !=null){
	bitmap = (Bitmap)b.get(IMAGE_BUNDLE_KEY);
	ImageButton mImageButton = (ImageButton) findViewById(R.id.add_photo);
	mImageButton.setImageBitmap(bitmap);}
}
*/
public void onSaveInstanceState(Bundle savedInstanceState) {
	 if(photoFile != null){
		  savedInstanceState.putParcelable("outputFileUri", Uri.fromFile(photoFile));
	 }
  
   super.onSaveInstanceState(savedInstanceState);
}

@Override
public void onRestoreInstanceState (Bundle savedInstanceState){
	fileUri = savedInstanceState.getParcelable("outputFileUri");
	if(fileUri != null) {
	photoFile = new File(fileUri.getPath());
	setPic(Uri.fromFile(photoFile));}
}
	
}
