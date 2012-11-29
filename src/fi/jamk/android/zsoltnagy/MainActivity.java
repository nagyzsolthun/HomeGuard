package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	public enum Status {
		IDLE, DETECTING, DETECTED
	}
	private Status status;
	private Handler handler;
	
	MovementDetector detector;
	GuardAudioPlayer warningPlayer;
	GuardAudioPlayer alarmPlayer;
	
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        status = Status.IDLE;
        handler = new Handler();
        
        detector = new MovementDetector(this,1000);
        warningPlayer = new GuardAudioPlayer(this, R.raw.warning);
        alarmPlayer = new GuardAudioPlayer(this, R.raw.alarm);
        
        sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        setStartDelayElements();
        setWarningDelayElements();
        setAlarmDelayElements();
        setStartButton();
        
    }
    
    private void setStartDelayElements() {
    	final TextView startDelayTextView = (TextView) findViewById(R.id.startDelayTextView);
    	final SeekBar startDelaySeekBar = (SeekBar) findViewById(R.id.startDelaySeekBar);
        startDelaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("startDelay", progress);
				sharedPreferencesEditor.commit();
				startDelayTextView.setText(getString(R.string.brief_start_delay)+": " + progress + " sec");
			}
		});
        startDelaySeekBar.setProgress(sharedPreferences.getInt("startDelay", 10));
    }
    private void setWarningDelayElements() {
    	final TextView warningDelayTextView = (TextView) findViewById(R.id.warningDelayTextView);
    	final SeekBar warningSeekBar = (SeekBar) findViewById(R.id.warningSeekBar);
    	warningSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("warningDelay", progress);
				sharedPreferencesEditor.commit();
				warningDelayTextView.setText(getString(R.string.brief_warning_delay)+": " + progress + " sec");
			}
		});
    	warningSeekBar.setProgress(sharedPreferences.getInt("startDelay", 10));
    }
    private void setAlarmDelayElements() {
    	final TextView alarmDelayTextView = (TextView) findViewById(R.id.alarmDelayTextView);
    	final SeekBar alarmSeekBar = (SeekBar) findViewById(R.id.alarmSeekBar);
    	alarmSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("alarmDelay", progress);
				sharedPreferencesEditor.commit();
				alarmDelayTextView.setText(getString(R.string.brief_alarm_delay)+": " + progress + " sec");
			}
		});
    	alarmSeekBar.setProgress(sharedPreferences.getInt("startDelay", 10));
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
    	status = Status.DETECTING;
    	int startDelay = sharedPreferences.getInt("startDelay", 1);
    	handler.postDelayed(detector, startDelay*1000);
	}
    
    /**
     * method asked when movement is detected
     * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
     */
    public void onMovementDetected(int camId, byte[] jpegBytes) {
    	Log.d("MainActivity","movement detected");

    	if(status == Status.DETECTED) return;	//sound files should not be played again and again while movement
    	status = Status.DETECTED;
    	
    	Log.d("MainActivity","soundfiles played");

    	int warningDelay = sharedPreferences.getInt("warningDelay", 0);
    	int alarmDelay = sharedPreferences.getInt("alarmDelay", 10);
    	if(warningDelay < alarmDelay) handler.postDelayed(warningPlayer, warningDelay*1000);
    	handler.postDelayed(alarmPlayer, alarmDelay*1000);
    }
    
    
}
