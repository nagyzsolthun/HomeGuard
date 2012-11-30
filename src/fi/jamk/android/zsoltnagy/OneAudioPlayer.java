package fi.jamk.android.zsoltnagy;

import android.media.MediaPlayer;

/**
 * class for playing an audio file for set interval.
 * Lets to play only 1 audio file at a time.
 * @author zsolt
 */
public class OneAudioPlayer {
	
	private MediaPlayer mp;
	private static OneAudioPlayer playingOne;

	public OneAudioPlayer(MainActivity context, int audioResid) {
		try {
			mp = MediaPlayer.create(context, audioResid);
			mp.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		if(playingOne == this) return;	//starting again..
		stop();	//to stop other playing.. if there is other
		playingOne = this;
		mp.start();
	}
	public static void stop() {
		if(playingOne != null) playingOne.mp.stop();
	}
}