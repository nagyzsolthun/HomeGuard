package fi.jamk.android.zsoltnagy;

import java.util.Timer;
import java.util.TimerTask;
import android.hardware.Camera;

public class MovementDetector {
	MainActivity parent;
	Timer timer;
	private Camera[] cameras;
	private PictureComparer[] pictureComparers;
	
	public MovementDetector(MainActivity parent) {
		this.parent = parent;
		timer = new Timer();
		pictureComparers = new PictureComparer[Camera.getNumberOfCameras()];
		cameras = new Camera[Camera.getNumberOfCameras()];

        for(int i=0; i < Camera.getNumberOfCameras(); i++) {
        	pictureComparers[i] = new PictureComparer(i,this);
        }
	}
	
	/**
	 * starts process of detecting
	 * @param delay delay of starting comparing in millisecs
	 * @param period time between comparing images if camera in millisecs
	 */
	public void startProcess(long delay, long period) {

		for(int i=0; i < Camera.getNumberOfCameras(); i++) {
        	cameras[i] = Camera.open(i);
        	cameras[i].getParameters().setPreviewSize(20, 20);
        	//TODO: change fps. be aware: emulator dont have getSupportedPreviewFpsRange (returns null)
        	pictureComparers[i].setSensivity(0);	//TODO
        	pictureComparers[i].setInputPictureSize(20, 20);
        	cameras[i].startPreview();
        }
		timer.scheduleAtFixedRate(new TimerTask() {public void run() {check();}}, delay, period);
	}
	
	/**
	 * stops detection
	 */
	public void stopProcess() {
		//TODO: only if started..
		timer.cancel();
		for(int i=0; i < Camera.getNumberOfCameras(); i++) {
			cameras[i].stopPreview();
			cameras[i].release();
		}
	}
	
	/**
	 * Takes pictures with all available cameras and compares these images to previously taken ones.
	 */
	private void check() {
		for(int i=0; i < Camera.getNumberOfCameras(); i++)
			cameras[i].setOneShotPreviewCallback(pictureComparers[i]);
	}
	
	/**
	 * Callback function that runs when a PictureComparer detects movement
	 * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
	 */
	public void onMovementDetected(int camId, byte[] jpegBytes) {
		parent.onMovementDetected(camId, jpegBytes);
	}
}
