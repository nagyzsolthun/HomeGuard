package fi.jamk.android.zsoltnagy;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MovementDetectorService extends GuardService {
	private Timer timer;
	private long checkPeriod;
	private long lastMovement;
	private Camera[] cameras;
	private PictureComparer[] pictureComparers;
	
	public MovementDetectorService(MainActivity context) {
		super(context);
		setLayoutElements();

		this.checkPeriod = 1000;
		lastMovement = 0;
		try {
			timer = new Timer();
			pictureComparers = new PictureComparer[1];
			cameras = new Camera[1];

			for(int i=0; i < 1; i++) {
				pictureComparers[i] = new PictureComparer(i,this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setAvailable(false);
		}
	}
	
	/**
	 * starts process of detecting
	 * @param delay delay of starting comparing in millisecs
	 * @param period time between comparing images if camera in millisecs
	 */
	public void run() {
		if(! isAvailable()) return;
		TextView debugTextView = (TextView) context.findViewById(R.id.debugTextView);
    	debugTextView.setText("movement detection started");
		for(int i=0; i < 1; i++) {
        	cameras[i] = Camera.open(i);
        	cameras[i].getParameters().setPreviewSize(352,288);
        	//cameras[i].getParameters().setPreviewFormat(ImageFormat.JPEG);
        	//TODO: change fps. be aware: emulator dont have getSupportedPreviewFpsRange (returns null)
        	pictureComparers[i].setSensivity(0.01);	//TODO
        	pictureComparers[i].setInputPictureSize(352,288);
        	cameras[i].startPreview();
        }
		timer.scheduleAtFixedRate(new TimerTask() {public void run() {check();}}, 0, checkPeriod);
	}
	
	/**
	 * stops detection
	 */
	public void stop() {
		//TODO: only if started..
		timer.cancel();
		for(int i=0; i < 1; i++) {
			if(cameras[i] == null) break;	//if it was not opened at all
			cameras[i].stopPreview();
			cameras[i].release();
		}
	}
	
	/**
	 * Takes pictures with all available cameras and compares these images to previously taken ones.
	 */
	private void check() {
		for(int i=0; i < 1; i++)
			cameras[i].setOneShotPreviewCallback(pictureComparers[i]);
	}
	
	/**
	 * Callback function that runs when a PictureComparer detects movement
	 * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
	 */
	public void onMovementDetected(int camId, byte[] jpegBytes) {
		lastMovement = System.currentTimeMillis();
		context.onMovementDetected(camId, jpegBytes);
	}
	
	public boolean isDetectedRecently(long intervall) {
		if(System.currentTimeMillis() <= lastMovement+intervall) return true;
		return false;
	}
	
	private void setLayoutElements() {
		final TextView startDelayTextView = (TextView) context.findViewById(R.id.startDelayTextView);
    	final SeekBar startDelaySeekBar = (SeekBar) context.findViewById(R.id.startDelaySeekBar);

        startDelaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_start_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("startDelaySecs", progress);
				sharedPreferencesEditor.commit();
				startDelayTextView.setText(context.getString(R.string.brief_start_delay)+": " + progress + " sec");
			}
		});
        startDelaySeekBar.setProgress(sharedPreferences.getInt("startDelaySecs", 10));
	}
}
