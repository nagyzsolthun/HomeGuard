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
 * If this number is higher than a set value, then callback method is called.
 */
class PictureComparer implements PreviewCallback {
	int camId;
	MovementDetectorService parent;
	private Bitmap preBitmap;	//previously taken picture
	private int width,height;	//size of picture
	private double sensitivity;	//ratio of area that has to be different to trigger changedPictureCallback
	private final double difflimit = 0.05;	//maximum difference between brightness of pixels on 2 following picture [0,1] scale
	
	/**
	 * Constructor for PictureComparer
	 * @param camId id of camera that  is used for movement detection
	 * @param parent MovementDetectorService that uses this
	 */
	public PictureComparer(int camId, MovementDetectorService parent) {
		this.camId = camId;
		this.parent = parent;
	}
	
	//returns the brightness of given color on a [0,1] scale
	private double brightness(int color) {
		double result = Color.red(color) + Color.green(color) + Color.blue(color);
		return (result/3)/255;
	}

	//returns true if movement is detected, false otherwise
	private boolean compareBitmap(Bitmap bitmap) {
		if(preBitmap == null) {	//first shot
			preBitmap = bitmap;
			return false;
		}
		
		long diffcount = 0;	//number of deviant pixels
		for(int i=0; i<width; i++)
			for(int j=0; j<height; j++) {
				double brightness1 = brightness(bitmap.getPixel(i, j));
				double brightness2 = brightness(preBitmap.getPixel(i, j));
				if(Math.abs(brightness1 - brightness2) > difflimit) diffcount++;
			}
		preBitmap = bitmap;
		
		//debug
		parent.context.debugTextView.setText("changing ratio: "+String.format("%.4f", Float.valueOf((float)diffcount/(width*height)*100))+"%");
		parent.context.debugImageView.setImageBitmap(bitmap);
		
		return (diffcount > sensitivity*width*height);
	}
	
	/** 
	 * Called each time a picture is taken by camera.
	 * Entry point of picture comparing
	 */
	public void onPreviewFrame(byte[] data, Camera camera) {
		int previewFormat;
		try {
			//camera is sometimes already released when this function is called
			previewFormat = camera.getParameters().getPreviewFormat();
		} catch (Exception e) {
			previewFormat = ImageFormat.NV21;	//default of preview
		}

		YuvImage img = new YuvImage(data, previewFormat, width, height, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		img.compressToJpeg(new Rect(0,0,width, height), 100, out);
		try {out.close();} catch (Exception e) {return;}	//TODO

		byte[] jpegBytes = out.toByteArray();
		Bitmap bitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.length);
		if(compareBitmap(bitmap)) parent.onMovementDetected(camId, jpegBytes);
	}
	
	/**
	 * sets sensitivity of comparison
	 * @param sensitivity sensitivity of comparison
	 */
	public void setSensivity(double sensitivity) {
		this.sensitivity = sensitivity;
	}
	
	/**
	 * setter for size of picture got in onPreviewFrame 
	 * @param width width of picture
	 * @param height height of picture
	 */
	public void setInputPictureSize(int width, int height) {
		preBitmap = null;
		this.width = width;
		this.height = height;
	}
}
