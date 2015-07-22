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
    	String nombres = ""+nombre_images;
    	Log.i("nombre images",nombres);
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
     
		String [] tab_images = new String [nombre_images];
		String [] tab_Texte = new String [nombre_images];
		images_bitmap = new Bitmap[nombre_images];
		for (int i=0;i<nombre_images;i++) {
			
			tab_images[i] = obs.get(i).getPath();
			tab_Texte[i] = obs.get(i).getName();			
		}
		Log.i("uri image 1", tab_images[0]);
		for (int i=0;i<nombre_images;i++) {
			//chargement des images
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inJustDecodeBounds = true;
			File file = new File(tab_images[i]);
			Uri bitmapuri = Uri.fromFile(file);
		
			try {
				images_bitmap[i] = getThumbnail(bitmapuri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			finally {
				
		}
    }
    
        ImageView imageView;
        if (convertView == null) {
        	// get layout from mobile.xml
        				gridView = inflater.inflate(R.layout.element_liste, null);
        	 
        				
        	 
        				// set image based on selected text
        				imageView = (ImageView) gridView
        						.findViewById(R.id.grid_item_image);
        		        imageView.setImageBitmap(images_bitmap[position]);

            // if it's not recycled, initialize some attributes
           // imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
           // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
        	gridView = (View) convertView;
        }

        return gridView;
    }
    
    
    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException,
	IOException {
InputStream input = mContext.getContentResolver().openInputStream(uri);
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
double ratio = (originalSize > 1000) ? (originalSize / 250) : 1.0;
BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
bitmapOptions.inDither = true;// optional
bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
input = mContext.getContentResolver().openInputStream(uri);
Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
input.close();
return bitmap;
}
/*    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
    
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}*/
private static int getPowerOfTwoForSampleRatio(double ratio) {
int k = Integer.highestOneBit((int) Math.floor(ratio));
if (k == 0)
	return 1;
else
	return k;
}
    
   
}