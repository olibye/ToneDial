package net.xpdeveloper.dialer;

import static android.media.ToneGenerator.TONE_DTMF_0;
import static android.media.ToneGenerator.TONE_DTMF_1;
import static android.media.ToneGenerator.TONE_DTMF_2;
import static android.media.ToneGenerator.TONE_DTMF_3;
import static android.media.ToneGenerator.TONE_DTMF_4;
import static android.media.ToneGenerator.TONE_DTMF_5;
import static android.media.ToneGenerator.TONE_DTMF_6;
import static android.media.ToneGenerator.TONE_DTMF_7;
import static android.media.ToneGenerator.TONE_DTMF_8;
import static android.media.ToneGenerator.TONE_DTMF_9;
import net.xpdeveloper.dialer.api1.ToneDialModelAPI1;
import net.xpdeveloper.dialer.api5.ToneDialModelAPI5;
import android.os.Build;

/**
 * I generate DTMF tones. They may be sent to me from a GUI or a
 * BroadCastReceiver. My implementations handle the API differences
 */
public abstract class ToneDialModel implements IToneDialModel {

	protected static final int TONE_DURATION = 120;
	static final int TONE_PAUSE = 120;

	protected static final int[] toneCodes = new int[] { TONE_DTMF_0,
			TONE_DTMF_1, TONE_DTMF_2, TONE_DTMF_3, TONE_DTMF_4, TONE_DTMF_5,
			TONE_DTMF_6, TONE_DTMF_7, TONE_DTMF_8, TONE_DTMF_9 };
	public static final String EMERGENCY_999 = "999";
	public static final String EMERGENCY_911 = "911";
	
	public final void dial(String dialString) throws InterruptedException {
		int digitCount = dialString.length();

		if (digitCount > 0) {
			// Big pause before for the first tone
			// We typically see "AudioFlinger write blocked for 172 ms
			dialDigitOrPause(dialString.charAt(0), TONE_PAUSE * 3);

			for (int digitIndex = 1; digitIndex < digitCount; digitIndex++) {
				dialDigitOrPause(dialString.charAt(digitIndex), TONE_PAUSE);
			}

			// Pause to make sure this app doesn't quit before the tone is finished
			// For example when running in tests
			dialDigitOrPause(' ', TONE_PAUSE);
		}

	}

	synchronized final void dialDigitOrPause(final char digit, final int pause)
			throws InterruptedException {

		if (Character.isDigit(digit)) {
			// ignore non digits
			dialDigit(digit, pause);
		}

		if (Character.isSpace(digit)) {
			wait(TONE_DURATION + pause);
		}

	}

	protected abstract void dialDigit(char digit, int pause)
			throws InterruptedException;

	/**
	 * Provide a way to filter out Emergency numbers We should not stop the
	 * mobile from dialing these
	 * 
	 * @param originalDestination
	 * @return
	 */
	public static boolean isEmergencyNumer(String originalDestination) {
		if (ToneDialModel.EMERGENCY_999.equals(originalDestination)) {
			return true;
		}

		if (ToneDialModel.EMERGENCY_911.equals(originalDestination)) {
			return true;
		}
		return false;
	}

	/**
	 * Build the correct Model for this SDK version
	 * @param buildSDKVersion
	 * @return
	 */
	public static ToneDialModel buildModel(int buildSDKVersion) {
		if (buildSDKVersion < 5) {
			return new ToneDialModelAPI1();
		}
		return new ToneDialModelAPI5();
	}

	public static ToneDialModel buildModel() {
		return buildModel(Integer.parseInt(Build.VERSION.SDK));
	}
}
