package fi.jamk.android.zsoltnagy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;

/**
 * Class for comparing two images taken by camera
 * @author zsolt
 *
 */
class PictureComparer implements PictureCallback {
	MainActivity parent;	//parent is stored for callback
	Bitmap preBitmap;	//previously compared data
	private double sensitivity = 1.0;
	
	//returns the brightness of given color on a [0,1] scale
	private double brightness(int color) {
		double result = Color.red(color) + Color.green(color) + Color.blue(color);
		return (result/3)/255;
	}
	public void onPictureTaken(byte[] data, Camera camera) {
		camera.release();
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		if(preBitmap == null) {
			preBitmap = bitmap;
			return;
		}

		if(bitmap.getHeight() != preBitmap.getHeight()) return;	//TODO
		if(bitmap.getWidth() != preBitmap.getWidth()) return;	//TODO
		
		double diff = 0;
		for(int i=0; i<bitmap.getWidth(); i++)
			for(int j=0; j<bitmap.getHeight(); j++) {
				double brightness1 = brightness(bitmap.getPixel(i, j));
				double brightness2 = brightness(preBitmap.getPixel(i, j));
				diff += Math.abs(brightness1 - brightness2);
			}
		if(diff > sensitivity*bitmap.getWidth()*bitmap.getHeight()) {
			Log.v("picture check","diff is bigger..");
			//TODO
		}
	}
	
	/**
	 * sets sensitivity of comparison
	 * @param sensitivity
	 */
	public void setSensivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
}
