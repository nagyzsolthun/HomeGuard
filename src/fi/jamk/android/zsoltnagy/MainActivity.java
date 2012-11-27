package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


public class MainActivity extends Activity implements ChangedPictureCallback {
	
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

        final TextView delayText = (TextView) findViewById(R.id.delayText);
        final SeekBar delaySeekBar = (SeekBar) findViewById(R.id.delaySeekBar);
        delaySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				delayText.setText("Delay: "+ progress + " sec");
			}
		});
        delaySeekBar.setProgress(sharedPreferences.getInt("startDelay", 10));
        
        final Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				sharedPreferencesEditor.putInt("startDelay", delaySeekBar.getProgress());
				sharedPreferencesEditor.commit();
				startDetection();
			}
		});
        
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	detector.stopProcess();
    }
    
    public void startDetection() {
    	Log.d("zsolt","processstarted");
    	int startDelay = sharedPreferences.getInt("startDelay", 1);
    	detector.startProcess(startDelay*1000, 1000);
	}

    /**
     * method runs whenever movement is detected
     */
	public void onPictureChange(Bitmap bitmap) {
		Log.d("ManActivity","movement detected");
	}
}
