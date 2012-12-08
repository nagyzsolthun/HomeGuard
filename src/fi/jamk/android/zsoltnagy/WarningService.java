package fi.jamk.android.zsoltnagy;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * service for playing warning and manage layout elements of warning settings
 * If other services (using OneAudioPlayer) already playing sound then they are stopped and only warning will be played. 
 */
public class WarningService extends GuardService {
	private OneAudioPlayer player;
	private TextView textView;
	private SeekBar seekBar;
	SeekBar alarmSeekbar;	//for inactivating this when delay of alarm is smaller
	
	/** constructs a WarningService with given context*/
	public WarningService(MainActivity context) {
		super(context);
		setLayoutElements();
		
		try {
			player = new OneAudioPlayer(context, R.raw.warning);
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
    	textView = (TextView) context.findViewById(R.id.warningDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.warningSeekBar);
    	alarmSeekbar = (SeekBar) context.findViewById(R.id.alarmSeekBar);
    	
    	textView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resetActive();
			}
		});
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_warning_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("warningDelaySecs", progress);
				sharedPreferencesEditor.commit();
				textView.setText(context.getString(R.string.brief_warning_delay)+": " + progress + " sec");
				
				if(context.alarmService == null) return;
				if(progress >= alarmSeekbar.getProgress()) context.alarmService.setActive(false);
				else context.alarmService.setActive(true);
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("warningDelaySecs", 1));
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
	
	@Override
	/** Activates or inactivates service and changes color of layout elements.*/
	public void setActive(boolean active) {
		super.setActive(active);
		setColors();
	}
}
