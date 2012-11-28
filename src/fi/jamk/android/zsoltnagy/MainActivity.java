package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	MovementDetector detector;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        detector = new MovementDetector(this);
        sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        setStartDelayElements();
        setStartButton();
        
    }
    
    private void setStartDelayElements() {
    	final TextView delayText = (TextView) findViewById(R.id.startDelayTextView);
    	final SeekBar startDelaySeekBar = (SeekBar) findViewById(R.id.startDelaySeekBar);
        startDelaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("startDelay", progress);
				sharedPreferencesEditor.commit();
				delayText.setText(getString(R.string.brief_start_delay)+": " + progress + " sec");
			}
		});
        startDelaySeekBar.setProgress(sharedPreferences.getInt("startDelay", 10));
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
    	detector.stopProcess();
    }
    
    /**
     * Waits for the set starting delay and starts movement detection.
     */
    public void startDetection() {
    	Log.d("zsolt","processstarted");
    	int startDelay = sharedPreferences.getInt("startDelay", 1);
    	detector.startProcess(startDelay*1000, 1000);
	}
    
    /**
     * method asked when movement is detected
     * @param camId id of camera that detected movement
	 * @param jpegBytes picture where movement was detected
     */
    public void onMovementDetected(int camId, byte[] jpegBytes) {
    	Log.d("MainActivity","movement detected");
    }
    
    /**
     * plays warning sound delayed with value set in MainActivity
     */
    private void playWarningDelayed() {
    	
    }
}
