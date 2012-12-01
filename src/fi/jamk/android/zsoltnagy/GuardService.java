package fi.jamk.android.zsoltnagy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

public class GuardService implements Runnable {
	private boolean available;
	private boolean active;
	
	protected MainActivity context;
	protected SharedPreferences sharedPreferences;
	protected SharedPreferences.Editor sharedPreferencesEditor;

	public GuardService(MainActivity context) {
		available = true;
		active = true;
		this.context = context;
		sharedPreferences = context.getSharedPreferences("HomeGuardPreferences", Activity.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
	}
	public void run() {}
	public void resetState() {
		setActive(! active);
	}
	public void setActive(boolean active) {
		if(! available) {
			Log.w("HomeGuardAction","cannot change state of unavailable action");
			return;
		}
		this.active = active;
	}
	public void setAvailable(boolean available) {
		this.available = available;
		if(! available) active = false;
	}
	public boolean isAvailable() {
		return available;
	}
	public boolean isActive() {
		return active;
	}
}