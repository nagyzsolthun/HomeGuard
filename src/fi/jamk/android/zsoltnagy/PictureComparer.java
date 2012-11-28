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

/**
 * Class for comparing two images taken by camera.
 * The method of comparing is the next: each pixels of 2 following pictures are compared.
 * We count the number of positions where difference of brightnesses are bigger then difflimit.
 * If this 
 * @author zsolt
 *
 */
class PictureComparer implements PreviewCallback {
	int camId;
	MovementDetector parent;
	private Bitmap preBitmap;	//previously compared data
	private int width,height;	//size of picture
	private double sensitivity = 0.1;	//ratio of picture that has to be different to trigger changedPictureCallback
	private final double difflimit = 0.1;	//maximum difference between brightness of pixels on 2 following picture [0,1] scale
	
	/**
	 * Constructor for PictureComparer
	 * @param changedPictureCallback called when compared pictures are different
	 */
	public PictureComparer(int camId, MovementDetector parent) {
		this.camId = camId;
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
		img.compressToJpeg(new Rect(0,0,width, height), 50, out);
		byte[] jpegBytes = out.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);

		if(preBitmap == null) {	//first shot
			preBitmap = bitmap;
			return;
		}

		long diffcount = 0;
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++) {
				double brightness1 = brightness(bitmap.getPixel(i, j));
				double brightness2 = brightness(preBitmap.getPixel(i, j));
				if(Math.abs(brightness1 - brightness2) > difflimit) diffcount++;
			}
		preBitmap = bitmap;

		if(diffcount > sensitivity*width*height) parent.onMovementDetected(camId, jpegBytes);
	}
	
	/**
	 * sets sensitivity of comparison
	 * @param sensitivity sensitivity of comparison
	 */
	public void setSensivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	/**
	 * informs object about resolution of pictures.
	 * @param width width of picture
	 * @param height height of picture
	 */
	public void setInputPictureSize(int width, int height) {
		preBitmap = null;
		this.width = width;
		this.height = height;
	}
}
