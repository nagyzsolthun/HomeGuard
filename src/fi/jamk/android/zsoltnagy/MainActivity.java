package fi.jamk.android.zsoltnagy;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
	
	MovementDetector detector;
	Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        detector = new MovementDetector(this);
        timer = new Timer();
        
        final Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				startProcess();
			}
		});
    }
    
    @Override
    public void onStop() {
    	timer.cancel();
    	detector.releaseCameras();
    }
    
    @Override
    public void onDestroy() {
    	timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void startProcess() {
    	SharedPreferences sharedPreferences = getSharedPreferences("HomeGuardPreferences", MODE_PRIVATE);
    	int startDelay = sharedPreferences.getInt("startDelay", 1);
    	
    	Log.d("zsolt","processstarted");

		timer.scheduleAtFixedRate(
				new TimerTask() {
					public void run() {detector.check();}
				}, startDelay*1000, 1000);
	}
}
