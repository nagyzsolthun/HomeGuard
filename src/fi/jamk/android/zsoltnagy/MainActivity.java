package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.admin.DevicePolicyManager;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

	private Handler handler;
	
	MovementDetectorService detectorService;
	WarningService warningService;
	AlarmService alarmService;
	EmailService emailService;
	SmsService smsService;
	
	private boolean isDetectedProcessStarted;	//is the process started (because of detection)
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;

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
        
        isDetectedProcessStarted = false;
        
        sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        setStartButton();
        
    }
    
    private void setStartButton() {
    	final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				startProcess();
			}
		});
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	detectorService.stop();
    	OneAudioPlayer.stop();
    	warningService.setActive(false);
    	alarmService.setActive(false);
    	emailService.setActive(false);
    	smsService.setActive(false);
    	finish();
    }
    
    /**
     * Waits for the set starting delay and starts movement detection.
     */
    public void startProcess() {
    	TextView debugTextView = (TextView) findViewById(R.id.debugTextView);
    	debugTextView.setText("process started");

    	Log.d("zsolt","processstarted");
    	handler.postDelayed(detectorService, sharedPreferences.getInt("startDelaySecs", 1)*1000);
	}
    
    /**
     * method asked when movement is detected
     * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
     */
    public void onMovementDetected(int camId, byte[] jpegBytes) {
    	Log.d("MainActivity","movement detected");
    	TextView debugTextView = (TextView) findViewById(R.id.debugTextView);
    	debugTextView.setText("movement detected");

    	//if decetor detected movement more then reStartDelayMins time ago, then 
    	
    	if(isDetectedProcessStarted) {
    		if(! detectorService.isDetectedRecently(sharedPreferences.getInt("reStartDelayMins", 10)*60*1000)) {
    			isDetectedProcessStarted = false;
    		} else return;
    	}
    	isDetectedProcessStarted = true;

    	handler.postDelayed(warningService, sharedPreferences.getInt("warningDelaySecs", 10)*1000);
    	handler.postDelayed(alarmService, sharedPreferences.getInt("alarmDelaySecs", 20)*1000);
    	handler.postDelayed(emailService, sharedPreferences.getInt("emailDelaySecs", 10)*1000);
    	handler.postDelayed(smsService, sharedPreferences.getInt("smsDelaySecs", 10)*1000);
    }
}
