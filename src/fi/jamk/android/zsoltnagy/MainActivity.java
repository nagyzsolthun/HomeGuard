package fi.jamk.android.zsoltnagy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private PictureCallback mPicture = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			Log.d("width: ", "" + bitmap.getHeight());
			camera.release();
		}
	};
	
	void takePicutre() {
		//for(int i=0; i <= Camera.getNumberOfCameras(); i++)
		Camera camera = Camera.open(0);
		
		//I would use raw data, but..
		//http://stackoverflow.com/questions/4514862/android-impossible-to-obtain-raw-image-data-from-camera
		camera.takePicture(null, null, mPicture);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final Button startButton = (Button) findViewById(R.id.buttonStart);
        startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d("button", "clicked");
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
