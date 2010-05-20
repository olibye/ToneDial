package net.xpdeveloper.dialer.api1;

import net.xpdeveloper.dialer.ToneDialModel;
import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * I implement the ToneDial model using the API4 ToneGenerator API API4 does not
 * have a duration argument in the startTone method. Leaving the timing up to
 * the caller. Since ToneGeneration is on another thread the actual duration is
 * far from predictable. 
 * 
 * @author byeo
 * 
 */
public class ToneDialModelAPI1 extends ToneDialModel {
	private ToneGenerator _toneGenerator;
	
	public ToneDialModelAPI1() {
		this(80);
	}
	
	public ToneDialModelAPI1(int volume) {
		// http://developer.android.com/reference/android/media/ToneGenerator.html#ToneGenerator(int, int)
		_toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
	}
	
	@Override
	protected synchronized void dialDigit(final char digit, final int pause) throws InterruptedException {
				// Pause for to pronounce duplicate keys
				// Pause at start to give amp time to power up
				wait(pause);
			
				int numericValue = Character.getNumericValue(digit);
				_toneGenerator.startTone(toneCodes[numericValue]);
			
				// Wait after tone start to support Donut which lacks
				// startTone( , duration) method
				wait(TONE_DURATION);
				_toneGenerator.stopTone();
			}

	@Override
	public void release() {
		_toneGenerator.release();
	}

}
