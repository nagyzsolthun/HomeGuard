package fi.jamk.android.zsoltnagy;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements ChangedPictureCallback {
	
	MovementDetector detector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        detector = new MovementDetector(this);
        
        final Button startButton = (Button) findViewById(R.id.buttonStart);
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
    
    public void startDetection() {
    	SharedPreferences sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
    	int startDelay = sharedPreferences.getInt("startDelay", 1);
    	
    	Log.d("zsolt","processstarted");

    	detector.startProcess(startDelay*1000, 1000);
	}

    /**
     * method runs whenever movement is detected
     */
	public void onPictureChange(Bitmap bitmap) {
		Log.d("ManActivity","movement detected");
	}
}
