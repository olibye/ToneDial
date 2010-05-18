package net.xpdeveloper.dialer.api4;

import android.media.ToneGenerator;
import net.xpdeveloper.dialer.ToneDialModel;

/**
 * I implement the ToneDial model using the API4 ToneGenerator API API4 does not
 * have a duration argument in the startTone method. Leaving the timing up to
 * the caller. Since ToneGeneration is on another thread the actual duration is
 * far from predictable. 
 * 
 * @author byeo
 * 
 */
public class ToneDialModelAPI4 extends ToneDialModel {

	protected synchronized void dialDigit(final char digit, final int pause,
			final ToneGenerator toneGenerator) throws InterruptedException {
				// Pause for to pronounce duplicate keys
				// Pause at start to give amp time to power up
				wait(pause);
			
				int numericValue = Character.getNumericValue(digit);
				toneGenerator.startTone(toneCodes[numericValue]);
			
				// Wait after tone start to support Donut which lacks
				// startTone( , duration) method
				wait(TONE_DURATION);
				toneGenerator.stopTone();
			}

}
