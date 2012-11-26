package fi.jamk.android.zsoltnagy;

import android.hardware.Camera;
import android.util.Log;

public class MovementDetector {
	private Camera[] cameras;
	private PictureComparer[] pictureComparers;
	
	public MovementDetector(MainActivity parent) {
		pictureComparers = new PictureComparer[Camera.getNumberOfCameras()];
		cameras = new Camera[Camera.getNumberOfCameras()];

        for(int i=0; i < Camera.getNumberOfCameras(); i++) {
        	cameras[i] = Camera.open(i);
        	cameras[i].getParameters().setPreviewSize(20, 20);
        	pictureComparers[i] = new PictureComparer(parent);
        	pictureComparers[i].setSensivity(0);	//TODO
        	cameras[i].startPreview();
        }
	}
	
	/**
	 * Takes pictures with all available cameras and compares these images to previously taken ones.
	 */
	public void check() {
		for(int i=0; i < Camera.getNumberOfCameras(); i++)
			cameras[i].setOneShotPreviewCallback( pictureComparers[i]);
	}
	
	public void releaseCameras() {
		for(int i=0; i < Camera.getNumberOfCameras(); i++) {
			cameras[i].stopPreview();
			cameras[i].release();
		}
	}
}
