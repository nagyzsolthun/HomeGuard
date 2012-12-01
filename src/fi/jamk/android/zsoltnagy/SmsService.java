package fi.jamk.android.zsoltnagy;

import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SmsService extends GuardService {
	
	TextView delayTextView;
	SeekBar seekBar;
	
	SmsManager smsManager;

	public SmsService(MainActivity context) {
		super(context);
		setLayoutElements();
		try {
			smsManager = SmsManager.getDefault();
		} catch (Exception e) {
			setAvailable(false);
		}
	}
	
	@Override
	public void run() {
		if(! isAvailable()) return;
		try {
			smsManager.sendTextMessage(sharedPreferences.getString("receivingNumber", ""), null, sharedPreferences.getString("smsText", "HomeGuard: movement detected!"), null, null);
		} catch (Exception e) {
			e.printStackTrace();
			setAvailable(false);
		}
		Log.d("SmsService","sms sent.. or not");
	}
	
	private void setLayoutElements() {
    	delayTextView = (TextView) context.findViewById(R.id.smsDelayTextView);
    	seekBar = (SeekBar) context.findViewById(R.id.smsDelaySeekBar);
    	
    	delayTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				context.startActivity(new Intent(context, fi.jamk.android.zsoltnagy.SmsSettingsActivity.class));
			}
		});
    	
    	seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {
				Toast.makeText(context, R.string.detailed_sms_delay, Toast.LENGTH_LONG).show();
			}
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sharedPreferencesEditor.putInt("smsDelaySecs", progress);
				sharedPreferencesEditor.commit();
				delayTextView.setText(context.getString(R.string.brief_sms_delay)+": " + progress + " sec");
			}
		});
    	seekBar.setProgress(sharedPreferences.getInt("smsDelaySecs", 10));
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
	public void setAvailable(boolean available) {
		super.setAvailable(available);
		changeColors();
	}
	

}
