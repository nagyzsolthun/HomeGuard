package fi.jamk.android.zsoltnagy;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class WarningService extends GuardService {
	private OneAudioPlayer player;
	private TextView delayTextView;
	private SeekBar seekBar;
	
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
	
	public void run() {
		if(! isActive()) return;
		player.play();
	}
	
	private void setLayoutElements() {
    	delayTextView = (TextView) context.findViewById(R.id.warningDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.warningSeekBar);
    	
    	delayTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				resetState();
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
				delayTextView.setText(context.getString(R.string.brief_warning_delay)+": " + progress + " sec");
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("warningDelaySecs", 0));
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
	public void setActive(boolean active) {
		super.setActive(active);
		changeColors();
	}
}
