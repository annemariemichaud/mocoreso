package com.lilahammer.mocoreso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class AddObservation extends FragmentActivity implements
		OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener {

	private Uri fileUri = null;
	private File photoFile = null;
	private double lat = 0;
	private double lon = 0;
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private DataAdapter dbmoco;
	private int targetW;
	private int targetH;
	private Bitmap bitmap;
	protected GoogleApiClient mGoogleApiClient;
	protected Location mLastLocation;
	private LocationRequest mLocationRequest;
	private Location mCurrentLocation;

	private GoogleMap map_ref;

	String IMAGE_BUNDLE_KEY = "image";
	private String imageFilePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.addobservation_layout);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		View mActionBarView = getLayoutInflater().inflate(R.layout.location_bar, null);
		actionBar.setCustomView(mActionBarView);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView textview_annuler = (TextView) findViewById(R.id.annuler);
		textview_annuler.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();

			}
		});
		TextView textview_enregistrer = (TextView) findViewById(R.id.enregistrer);
		textview_enregistrer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveObservation();

			}
		});
		if (savedInstanceState != null) {
			bitmap = (Bitmap) savedInstanceState.get(IMAGE_BUNDLE_KEY);
			if (bitmap != null) {
				ImageButton mImageButton = (ImageButton) findViewById(R.id.add_photo);
				mImageButton.setImageBitmap(bitmap);
			}
		}

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		buildGoogleApiClient();

	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
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
		if (mGoogleApiClient.isConnected()) {
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

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

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

		if (mCurrentLocation != null && map_ref != null) {
			map_ref.clear();
			LatLng observation = new LatLng(mCurrentLocation.getLatitude(),
					mCurrentLocation.getLongitude());
			lat = mCurrentLocation.getLatitude();
			lon = mCurrentLocation.getLongitude();
			map_ref.setMyLocationEnabled(true);
			map_ref.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			map_ref.moveCamera(CameraUpdateFactory.newLatLngZoom(observation,
					13));

			map_ref.addMarker(new MarkerOptions().position(observation));
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

		Log.i("problème localisation",
				"Connection failed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We
		// call connect() to
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

	@Override
	public void onMapReady(GoogleMap map) {
		map_ref = map;

	}

	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}

	// récupère les résultats des deux méthodes précédentes
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// traitement de la photo
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			
			if (photoFile != null) {
				// A known bug here! The image should have saved in fileUri
				Toast toast = Toast.makeText(this, "Image sauvegardée",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
				toast.show();

			} else {
				
				Toast.makeText(this, "Image not saved " 
						, Toast.LENGTH_LONG).show();
			}

			try {
				setPic(Uri.fromFile(photoFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// traitement de la localisation
		if (requestCode == 2 && resultCode == RESULT_OK) {
			String stringlatitude = data.getStringExtra("latitude");
			String stringlongitude = data.getStringExtra("longitude");
			lat = Double.parseDouble(stringlatitude);
			lon = Double.parseDouble(stringlongitude);
		}
	}

	// méthode pour prendre la photo
	public void TakePicture(View view) {
		if (photoFile != null) {
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

			}
		}
	}

	// creer un nom de fichier unique pour la photo
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "/JPEG_" + timeStamp + "_.jpg";

		imageFilePath = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ imageFileName;
		File imageFile = new File(imageFilePath);
		return imageFile;
	}

	private void setPic(Uri photoUri) throws IOException {
		if (photoUri == null) {
			Log.d("d", "photoUri est null");
		}
		File imageFile = new File(photoUri.getPath());

		if (imageFile.exists()) {
			ImageButton mImageButton = (ImageButton) findViewById(R.id.add_photo);

			// Get the dimensions of the View
			targetW = mImageButton.getWidth();
			targetH = mImageButton.getHeight();
			bitmap = decodeSampledBitmapFromResource(photoUri, targetW-50, targetH-50);

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					0, android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1f);
			DisplayMetrics dm = this.getResources().getDisplayMetrics();
			layoutParams.setMargins(convertDpToPx(10, dm), convertDpToPx(20, dm), convertDpToPx(10, dm),convertDpToPx(20, dm));
			mImageButton.setLayoutParams(layoutParams);
			mImageButton.setImageBitmap(bitmap);
		}
	}

	//conversion en tenant compte de la dp
	
	private int convertDpToPx(int dp, DisplayMetrics displayMetrics) {
	    float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
	    return Math.round(pixels);
	}
	public void saveObservation() {

		EditText mEdit = (EditText) findViewById(R.id.name);
		String name = mEdit.getText().toString();
		if (photoFile != null && !name.isEmpty() && (lat != 0 || lon != 0)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String currentDateandTime = sdf.format(new Date());
			dbmoco = new DataAdapter(this);
			dbmoco.insertObservation(name, lat, lon, currentDateandTime,
					photoFile.getPath());
			Toast toast = Toast.makeText(this, "Observation enregistrée",
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
			toast.show();
			finish();
		} else {
			if (photoFile == null) {
				Toast toast = Toast.makeText(this, "Il faut ajouter une photo !",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
				toast.show();
				
				return;
			}
			if (name.isEmpty()) {
				Toast toast = Toast.makeText(this, "Il faut donner un nom !",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			if (lat == 0 || lon == 0) {
				Toast.makeText(this, "Localisation requise", Toast.LENGTH_LONG)
						.show();
				return;
			}
		}
	}

	public void annuler(View view) {
		finish();
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

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (photoFile != null) {
			savedInstanceState.putParcelable("outputFileUri",
					Uri.fromFile(photoFile));
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		fileUri = savedInstanceState.getParcelable("outputFileUri");
		if (fileUri != null) {
			photoFile = new File(fileUri.getPath());
			try {
				setPic(Uri.fromFile(photoFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
