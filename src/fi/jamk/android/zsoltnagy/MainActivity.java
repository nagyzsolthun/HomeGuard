package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** connects and manages services*/
public class MainActivity extends Activity {

	private Handler handler;
	
	MovementDetectorService detectorService;
	WarningService warningService;
	AlarmService alarmService;
	EmailService emailService;
	SmsService smsService;

	Button startButton;
	
	private boolean detectedProcessStarted;	//actions should not be started every second - this value holds if actions are already started
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;
	TextView debugTextView;
	ImageView debugImageView;

	/** creates services and start button*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        
        detectorService = new MovementDetectorService(this);
        warningService = new WarningService(this);
        alarmService = new AlarmService(this);
        emailService = new EmailService(this);
        smsService = new SmsService(this);
        
        detectedProcessStarted = false;
        
        sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        
        debugTextView = (TextView) findViewById(R.id.debugTextView);
        debugImageView = (ImageView) findViewById(R.id.debugImageView);

        setStartButton();
        
    }
    
    /** sets button for starting process*/
    private void setStartButton() {
    	startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				startProcess();
			}
		});
    }
    
    /** deactivates all services*/
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	OneAudioPlayer.stop();
    	detectorService.setActive(false);
    	warningService.setActive(false);
    	alarmService.setActive(false);
    	emailService.setActive(false);
    	smsService.setActive(false);
    }
    
    /** Waits for the set starting delay and starts movement detection.*/
    public void startProcess() {
    	startButton.setEnabled(false);
    	handler.postDelayed(detectorService, sharedPreferences.getInt("startDelaySecs", 1)*1000);

    	debugTextView.setText("process started");
    	Log.d("zsolt","processstarted");
	}
    
    /**
     * method asked when movement is detected
     * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
     */
    public void onMovementDetected(int camId, byte[] jpegBytes) {
    	Log.d("MainActivity","movement detected");
    	debugTextView.setText("movement detected");

    	//if detector detected movement more then reStartDelayMins time ago, then..
    	if(detectedProcessStarted) {
    		if(! detectorService.isDetectedRecently(sharedPreferences.getInt("reStartDelayMins", 10)*60*1000)) {
    			detectedProcessStarted = false;
    		} else return;
    	}
    	detectedProcessStarted = true;

    	handler.postDelayed(warningService, sharedPreferences.getInt("warningDelaySecs", 10)*1000);
    	handler.postDelayed(alarmService, sharedPreferences.getInt("alarmDelaySecs", 20)*1000);
    	handler.postDelayed(emailService, sharedPreferences.getInt("emailDelaySecs", 10)*1000);
    	handler.postDelayed(smsService, sharedPreferences.getInt("smsDelaySecs", 10)*1000);
    }
}
