package fi.jamk.android.zsoltnagy;

import android.media.MediaPlayer;

/**
 * class for playing an audio file for set interval.
 * Lets to play only 1 audio file at each time.
 * @author zsolt
 */
public class GuardAudioPlayer implements Runnable {
	private MediaPlayer mp;
	private static GuardAudioPlayer active;
	
	/**
	 * constructs a new DelayedAudioPlayer that plays audio file defined by resid
	 * @param context
	 * @param resid audio file
	 * @param intervall intervall of playing audio file
	 */
	public GuardAudioPlayer(MainActivity context, int resid) {
		mp = MediaPlayer.create(context, resid);
		mp.setLooping(true);
	}
	
	public void run() {
		if(active == this) return;	//starting again..
		stop();	//to stop other playing.. if there is other
		active = this;
		mp.start();
	}
	public static void stop() {
		if(active != null) active.mp.stop();
	}
}