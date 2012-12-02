package fi.jamk.android.zsoltnagy;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Camera;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MovementDetectorService extends GuardService {
	private Timer timer;
	private long checkPeriod;
	private long lastMovement;
	private Camera camera;
	private PictureComparer pictureComparer;
	
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
	        //TODO: change fps. be aware: emulator dont have getSupportedPreviewFpsRange (returns null)
	        pictureComparer.setSensivity(0.01);	//TODO: outside
	        pictureComparer.setInputPictureSize(optimal.width, optimal.height);
	        camera.startPreview();
	       
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
		if(! isActive()) return;
    	context.debugTextView.setText("movement detection started");
		timer.scheduleAtFixedRate(new TimerTask() {public void run() {check();}}, 0, checkPeriod);
	}
	
	private Camera.Size getOptimalPreviewSize(Camera camera) {
		List<Camera.Size> list = camera.getParameters().getSupportedPreviewSizes();
		for(Camera.Size i: list) {
			if(i.width <= 320) return i;
		}
		return list.get(list.size()-1);
	}
	
	/**
	 * Takes pictures with camera and compares image to previously taken one.
	 */
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
	
	@Override
	public void setActive(boolean active) {
		if(! isActive() && active) {
			camera = Camera.open(0);
			Camera.Size optimal = getOptimalPreviewSize(camera);
	        camera.getParameters().setPreviewSize(optimal.width,optimal.height);
			camera.startPreview();
		}
		if(isActive() && ! active) {
			timer.cancel();
			camera.stopPreview();
			camera.release();
		}
		super.setActive(active);
	}
}
