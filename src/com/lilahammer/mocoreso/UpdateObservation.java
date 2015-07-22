package com.lilahammer.mocoreso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdateObservation extends Activity {


	private String oldname;
	private DataAdapter dbmoco;
	private String date;
	private File photoFile;
	
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
		date = observation.getDate();
		//File file = new File(observation.getPath());
		//photoFile = new File(observation.getPath());
		//Uri bitmapuri = Uri.parse(file.toString());
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
			Log.d("d", "photouri est null");
		}
		File imageFile = new File(photoUri.getPath());
		if (imageFile.exists()) {
			ImageView mImageButton = (ImageView) findViewById(R.id.photo);
			

			mImageButton.setImageBitmap(getImage(photoUri));
		}
	}

	public Bitmap getImage(Uri uri) throws FileNotFoundException,
	IOException {
InputStream input =this.getContentResolver().openInputStream(uri);
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
		int count = dbmoco.deleteData(oldname);
		
		Intent intent = new Intent(this, ListeObservationsActivity.class);
		startActivity(intent);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case (android.R.id.home) :
			finish();
			return true;
		case (R.id.delete):
			deleteObservation();
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
		
	}
}
