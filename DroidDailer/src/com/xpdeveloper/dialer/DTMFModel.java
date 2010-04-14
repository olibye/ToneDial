package com.xpdeveloper.dialer;

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
import android.media.AudioManager;
import android.media.ToneGenerator;

/**
 * I generate DTMF tones.
 * They may me sent to me from a GUI or a BroadCast Receiver 
 */
public class DTMFModel {

	static final int TONE_DURATION = 200;
	static final int TONE_PAUSE = 50;
	static final ToneGenerator _toneGenerator = new ToneGenerator(
	AudioManager.STREAM_DTMF, 70);
	static final int[] toneCodes = new int[] { TONE_DTMF_0, TONE_DTMF_1,
	TONE_DTMF_2, TONE_DTMF_3, TONE_DTMF_4, TONE_DTMF_5, TONE_DTMF_6,
	TONE_DTMF_7, TONE_DTMF_8, TONE_DTMF_9 };

	public static void dial(String dialString) throws InterruptedException {
		int digitCount = dialString.length();
		for (int digitIndex = 0; digitIndex < digitCount; digitIndex++) {
			char digit = dialString.charAt(digitIndex);
			dial(digit);
		}
	
	}

	private static synchronized void dial(char digit) throws InterruptedException {
		if (Character.isDigit(digit)) {
			// ignore non digits
			int numericValue = Character.getNumericValue(digit);
			_toneGenerator.startTone(toneCodes[numericValue],TONE_DURATION);
			DTMFModel.class.wait(TONE_DURATION + TONE_PAUSE); // TODO should not wait on a UI thread?
		}
		
		if (Character.isSpace(digit)) {
			DTMFModel.class.wait(TONE_DURATION + TONE_PAUSE);
		}
	}

}
