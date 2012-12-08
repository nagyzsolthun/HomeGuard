package fi.jamk.android.zsoltnagy;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * service for playing alarm and manage layout elements of alarm settings
 * If other services (using OneAudioPlayer) already playing sound then they are stopped and only alarm will be played. 
 */
public class AlarmService extends GuardService {
	OneAudioPlayer player;
	TextView textView;
	SeekBar seekBar;
	SeekBar warningSeekbar;	//for deactivating warningseekbar when delay of alarm is smaller
	
	/** constructs a AlarmService with given context*/
	public AlarmService(MainActivity context) {
		super(context);
		setLayoutElements();
		
		try {
			player = new OneAudioPlayer(context, R.raw.alarm);
		} catch (Exception e) {
			e.printStackTrace();
			setAvailable(false);
		}
	}
	
	/** starts playing*/
	public void run() {
		if(! isActive()) return;
		player.play();
	}
	
	/** connects layout elements to this*/
	private void setLayoutElements() {
		textView = (TextView) context.findViewById(R.id.alarmDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.alarmSeekBar);
    	warningSeekbar = (SeekBar) context.findViewById(R.id.warningSeekBar);
    	
    	textView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resetActive();
			}
		});
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_alarm_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("alarmDelaySecs", progress);
				sharedPreferencesEditor.commit();
				textView.setText(context.getString(R.string.brief_alarm_delay)+": " + progress + " sec");
				
				if(context.warningService == null) return;
				if(warningSeekbar.getProgress() >= progress) context.warningService.setActive(false);
				else context.warningService.setActive(true);
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("alarmDelaySecs", 10));
    }
	
	/** sets color of layout elements depending on being active or available*/
	private void setColors() {
		if(isActive()) {
			textView.setTextColor(context.getResources().getColor(R.color.activeTextColor));
			seekBar.setEnabled(true);
			return;
		}
		seekBar.setEnabled(false);
		if(! isAvailable()) textView.setTextColor(context.getResources().getColor(R.color.unavailableTextColor));
		else textView.setTextColor(context.getResources().getColor(R.color.inactiveTextColor));
	}
	
	/** Activates or deactivates service and changes color of layout elements.*/
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		setColors();
	}
}
