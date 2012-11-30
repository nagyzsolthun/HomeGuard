package fi.jamk.android.zsoltnagy;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class AlarmService extends GuardService {
	OneAudioPlayer player;
	TextView delayTextView;
	SeekBar seekBar;
	SeekBar warningSeekbar;	//for inactivating warningseekbar when delay of alarm is smaller
	
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
	
	public void run() {
		if(! isActive()) return;
		player.play();
	}
	
	private void setLayoutElements() {
    	delayTextView = (TextView) context.findViewById(R.id.alarmDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.alarmSeekBar);
    	warningSeekbar = (SeekBar) context.findViewById(R.id.warningSeekBar);
    	
    	delayTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resetState();
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
				delayTextView.setText(context.getString(R.string.brief_alarm_delay)+": " + progress + " sec");
				
				if(warningSeekbar.getProgress() >= progress) context.warningService.setActive(false);
				else context.warningService.setActive(true);
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("alarmDelaySecs", 4));
    }
	
	private void changeColors() {
		if(isActive()) {
			delayTextView.setTextColor(context.getResources().getColor(R.color.activeTextColor));
			seekBar.setEnabled(true);
			return;
		}
		seekBar.setEnabled(false);
		if(! isAvailable()) delayTextView.setTextColor(context.getResources().getColor(R.color.unavailableTextColor));
		else delayTextView.setTextColor(context.getResources().getColor(R.color.inactiveTextColor));
	}
	
	@Override
	public void resetState() {
		super.resetState();
		changeColors();
	}
}
