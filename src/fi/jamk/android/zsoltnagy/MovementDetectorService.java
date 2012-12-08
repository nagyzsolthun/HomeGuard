package fi.jamk.android.zsoltnagy;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Camera;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/** service of movement detection
 * Each time movement is detected, onMovementDetected method of context is called
 */
public class MovementDetectorService extends GuardService {
	private Timer timer;
	private long checkPeriod;	//time between comparison  of 2 pictures in millisecs
	private long lastMovement;
	private Camera camera;
	private PictureComparer pictureComparer;
	
	private TextView textView;
	private SeekBar seekBar;
	
	public MovementDetectorService(MainActivity context) {
		super(context);
		setLayoutElements();

		this.checkPeriod = 1000;
		lastMovement = 0;
		try {
			timer = new Timer();
			camera = Camera.open(0);
			pictureComparer = new PictureComparer(0, this);
			
			Camera.Size optimal = getOptimalPreviewSize(camera);
	        camera.getParameters().setPreviewSize(optimal.width,optimal.height);
	        //TODO: change FPS. be aware: emulator don't have getSupportedPreviewFpsRange (returns null)
	        pictureComparer.setSensivity(0.01);	//TODO: outside
	        pictureComparer.setInputPictureSize(optimal.width, optimal.height);
	        camera.startPreview();
		} catch (Exception e) {
			e.printStackTrace();
			setAvailable(false);
		}
	}
	
	/** starts process of detecting*/
	public void run() {
		if(! isActive()) return;
    	context.debugTextView.setText("movement detection started");
		timer.scheduleAtFixedRate(new TimerTask() {public void run() {check();}}, 0, checkPeriod);
	}
	
	//finds the smallest supported preview size that has width <= 320. No match => get smallest size 
	private Camera.Size getOptimalPreviewSize(Camera camera) {
		List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
		for(Camera.Size i: list) {
			if(i.width <= 320) return i;
		}
		return list.get(list.size()-1);
	}
	
	//takes picture and compares with previously taken one
	private void check() {
		if(! isActive()) return;
		camera.setOneShotPreviewCallback(pictureComparer);
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
	
	/**
	 * checks if there was detection in the past interval period
	 * @param interval checked interval
	 * @return true if there was movement detection in past interval period, false otherwise
	 */
	public boolean isDetectedRecently(long interval) {
		if(System.currentTimeMillis() <= lastMovement+interval) return true;
		return false;
	}
	
	//connects this to layout
	private void setLayoutElements() {
		textView = (TextView)context.findViewById(R.id.startDelayTextView);
    	seekBar = (SeekBar)context.findViewById(R.id.startDelaySeekBar);

    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_start_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("startDelaySecs", progress);
				sharedPreferencesEditor.commit();
				textView.setText(context.getString(R.string.brief_start_delay)+": " + progress + " sec");
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("startDelaySecs", 10));
	}

	/** sets color of layout elements depending on being active or available*/
	private void setColors() {
		if(isActive()) {
			textView.setTextColor(context.getResources().getColor(R.color.activeTextColor));
			seekBar.setEnabled(true);
			return;
		}
		seekBar.setEnabled(false);
		if(! isAvailable()) textView.setTextColor(context.getResources().getColor(R.color.unavailableTextColor));
		else textView.setTextColor(context.getResources().getColor(R.color.inactiveTextColor));
	}
	
	@Override
	/** Activates or inactivates service and changes color of layout elements.*/
	public void setActive(boolean active) {
		if(isActive() == active) return;
		if(active) {	//if activating
			camera = Camera.open(0);
			Camera.Size optimal = getOptimalPreviewSize(camera);
	        camera.getParameters().setPreviewSize(optimal.width,optimal.height);
			camera.startPreview();
		}
		else {	//if inactivating
			timer.cancel();
			camera.stopPreview();
			camera.release();
		}
		super.setActive(active);
		setColors();
	}
}
