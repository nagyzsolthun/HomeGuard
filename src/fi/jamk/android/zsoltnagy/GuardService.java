package fi.jamk.android.zsoltnagy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/** service is a Runnable that has its own state.
 * State is defined by 2 boolean values: active and available.
 * Available defines whether action of service is able to be used on device.
 * Active defines if service is turned on.
 * Action of service is defined run()
 */
public class GuardService implements Runnable {
	private boolean available;
	private boolean active;
	
	protected MainActivity context;
	protected SharedPreferences sharedPreferences;
	protected SharedPreferences.Editor sharedPreferencesEditor;

	/** constructs a service in given context*/
	public GuardService(MainActivity context) {
		available = true;
		active = true;
		this.context = context;
		sharedPreferences = context.getSharedPreferences("HomeGuardPreferences", Activity.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
	}
	/** action of service*/
	public void run() {}
	
	/** if active, sets inactive; if inactive, sets active*/
	public void resetActive() {
		setActive(! active);
	}
	
	/** setter for active attribute*/
	public void setActive(boolean active) {
		if(! available) {
			Log.w("HomeGuardAction","cannot change state of unavailable action");
			return;
		}
		this.active = active;
	}
	
	/** setter for available attribute*/
	public void setAvailable(boolean available) {
		this.available = available;
		if(! available) active = false;
	}
	
	/** getter for available attribute*/
	public boolean isAvailable() {
		return available;
	}
	
	/** getter for active attribute*/
	public boolean isActive() {
		return active;
	}
}