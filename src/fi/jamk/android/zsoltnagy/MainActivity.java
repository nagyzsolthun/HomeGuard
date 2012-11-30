package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private Handler handler;
	
	MovementDetector detector;
	GuardAudioPlayer warningPlayer;
	GuardAudioPlayer alarmPlayer;
	EmailSender sender;
	
	private boolean isDetectedProcessStarted;	//is the process started (because of detection)
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;
	
	EmailSettingsActivity emailSettingsActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler();
        
        detector = new MovementDetector(this,1000);
        warningPlayer = new GuardAudioPlayer(this, R.raw.warning);
        alarmPlayer = new GuardAudioPlayer(this, R.raw.alarm);
        sender = new EmailSender();
        
        isDetectedProcessStarted = false;
        
        sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        setStartDelayElements();
        setWarningDelayElements();
        setAlarmDelayElements();
        setEmailDelayElements();
        setStartButton();
        
    }
    
    private void setStartDelayElements() {
    	final TextView startDelayTextView = (TextView) findViewById(R.id.startDelayTextView);
    	final SeekBar startDelaySeekBar = (SeekBar) findViewById(R.id.startDelaySeekBar);
        startDelaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(MainActivity.this, R.string.detailed_start_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("startDelaySecs", progress);
				sharedPreferencesEditor.commit();
				startDelayTextView.setText(getString(R.string.brief_start_delay)+": " + progress + " sec");
			}
		});
        startDelaySeekBar.setProgress(sharedPreferences.getInt("startDelaySecs", 10));
    }
    private void setWarningDelayElements() {
    	final TextView warningDelayTextView = (TextView) findViewById(R.id.warningDelayTextView);
    	final SeekBar warningSeekBar = (SeekBar) findViewById(R.id.warningSeekBar);
    	warningSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(MainActivity.this, R.string.detailed_warning_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("warningDelaySecs", progress);
				sharedPreferencesEditor.commit();
				warningDelayTextView.setText(getString(R.string.brief_warning_delay)+": " + progress + " sec");
			}
		});
    	warningSeekBar.setProgress(sharedPreferences.getInt("warningDelaySecs", 4));
    }
    private void setAlarmDelayElements() {
    	final TextView alarmDelayTextView = (TextView) findViewById(R.id.alarmDelayTextView);
    	final SeekBar alarmSeekBar = (SeekBar) findViewById(R.id.alarmSeekBar);
    	alarmSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(MainActivity.this, R.string.detailed_alarm_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("alarmDelaySecs", progress);
				sharedPreferencesEditor.commit();
				alarmDelayTextView.setText(getString(R.string.brief_alarm_delay)+": " + progress + " sec");
			}
		});
    	alarmSeekBar.setProgress(sharedPreferences.getInt("alarmDelaySecs", 10));
    }
    
    private void setEmailDelayElements() {
    	final TextView emailDelayTextView = (TextView) findViewById(R.id.emailDelayTextView);
    	final SeekBar emailSeekBar = (SeekBar) findViewById(R.id.emailSeekBar);
    	
    	emailDelayTextView.setTextColor(getResources().getColor(R.color.inactiveTextColor));
    	emailSeekBar.setEnabled(false);
    	
    	emailDelayTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MainActivity.this.startActivity(new Intent(MainActivity.this, fi.jamk.android.zsoltnagy.EmailSettingsActivity.class));
			}
		});
    	
    	emailSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(MainActivity.this, R.string.detailed_email_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("emailDelaySecs", progress);
				sharedPreferencesEditor.commit();
				emailDelayTextView.setText(getString(R.string.brief_email_delay)+": " + progress + " sec");
			}
		});
    	emailSeekBar.setProgress(sharedPreferences.getInt("alarmDelaySecs", 10));
    }
    
    private void setStartButton() {
    	final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				startDetection();
			}
		});
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	detector.stop();
    	GuardAudioPlayer.stop();
    }
    
    /**
     * Waits for the set starting delay and starts movement detection.
     */
    public void startDetection() {
    	Log.d("zsolt","processstarted");
    	long startDelay = sharedPreferences.getInt("startDelaySecs", 1)*1000;
    	handler.postDelayed(detector, startDelay);
	}
    
    /**
     * method asked when movement is detected
     * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
     */
    public void onMovementDetected(int camId, byte[] jpegBytes) {
    	Log.d("MainActivity","movement detected");

    	//if decetor detected movement more then reStartDelayMins time ago, then 
    	
    	if(isDetectedProcessStarted) {
    		if(! detector.isDetectedRecently(sharedPreferences.getInt("reStartDelayMins", 10)*60*1000)) {
    			isDetectedProcessStarted = false;
    		} else return;
    	}
    	
    	isDetectedProcessStarted = true;
    	
    	handler.postDelayed(sender, 0);
    	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"nagy.zsolt.hun.public@gmail.com"});
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "text");

    	long warningDelay = sharedPreferences.getInt("warningDelaySecs", 0)*1000;
    	long alarmDelay = sharedPreferences.getInt("alarmDelaySecs", 10)*1000;
    	if(warningDelay < alarmDelay) handler.postDelayed(warningPlayer, warningDelay);
    	handler.postDelayed(alarmPlayer, alarmDelay);
    }
}
