package fi.jamk.android.zsoltnagy;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Class for comparing two images taken by camera
 * @author zsolt
 *
 */
class PictureComparer implements PreviewCallback {
	MainActivity parent;	//parent is stored for callback
	Bitmap preBitmap;	//previously compared data
	private double sensitivity = 1.0;
	
	/**
	 * Constructor for PictureComparer
	 * @param parent
	 */
	public PictureComparer(MainActivity parent) {
		this.parent = parent;
	}
	
	//returns the brightness of given color on a [0,1] scale
	private double brightness(int color) {
		double result = Color.red(color) + Color.green(color) + Color.blue(color);
		return (result/3)/255;
	}
	public void onPreviewFrame(byte[] data, Camera camera) {
		YuvImage img = new YuvImage(data, ImageFormat.NV21, 320, 240, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		img.compressToJpeg(new Rect(0,0,20, 20), 50, out);
		byte[] imageBytes = out.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

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
		preBitmap = bitmap;
		((ImageView)parent.findViewById(R.id.imageView1)).setImageBitmap(bitmap);

		if(diff < sensitivity*bitmap.getWidth()*bitmap.getHeight()) {
			Log.d("PictureComparer","no movement detected: " + diff + " < " + sensitivity*bitmap.getWidth()*bitmap.getHeight());
			return;
		}
		Log.d("PictureComparer","movement detected: " + diff + " >= " + sensitivity*bitmap.getWidth()*bitmap.getHeight());
		//parent.startProcess();
	}
	
	/**
	 * sets sensitivity of comparison
	 * @param sensitivity sensitivity of comparison
	 */
	public void setSensivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
}
