package fi.jamk.android.zsoltnagy;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private PictureComparer[] pictureComparers;
	private void takePicutre() {
		for(int i=0; i < Camera.getNumberOfCameras(); i++) {
			Camera camera = Camera.open(i);
			//I would use raw data, but..
			//http://stackoverflow.com/questions/4514862/android-impossible-to-obtain-raw-image-data-from-camera
			camera.takePicture(null, null, pictureComparers[i]);
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        pictureComparers = new PictureComparer[Camera.getNumberOfCameras()];
        for(int i=0; i < Camera.getNumberOfCameras(); i++) {
        	pictureComparers[i] = new PictureComparer(this);
        	pictureComparers[i].setSensivity(0);	//TODO
        }
        
        final Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("zsolt", "button clicked");
				takePicutre();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
