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
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * I generate DTMF tones. They may be sent to me from a GUI or a
 * BroadCastReceiver
 */
public class DTMFModel extends Service implements IDTMFModel {
	public static final String ACTION_DIAL = "DIAL";
	
	static final int TONE_DURATION = 120;
	static final int TONE_PAUSE = 120;

	static final int[] toneCodes = new int[] { TONE_DTMF_0, TONE_DTMF_1,
			TONE_DTMF_2, TONE_DTMF_3, TONE_DTMF_4, TONE_DTMF_5, TONE_DTMF_6,
			TONE_DTMF_7, TONE_DTMF_8, TONE_DTMF_9 };

	private ToneGenerator _toneGenerator;
	private boolean _tonesEnabled = true;
	public static final String EXTRA_DTMF_DIALED = "DTMF_DIALED";

	public void dial(String dialString, ToneGenerator toneGenerator)
			throws InterruptedException {
		int digitCount = dialString.length();

		if (digitCount > 0) {
			// Pause longer for the first tone
			// We typically see "AudioFlinger write blocked for 172 ms
			char digit = dialString.charAt(0);
			dial(digit, TONE_PAUSE * 2, toneGenerator);

			for (int digitIndex = 1; digitIndex < digitCount; digitIndex++) {
				dial(dialString.charAt(digitIndex), TONE_PAUSE, toneGenerator);
			}
		}

	}

	synchronized void dial(final char digit, final int pause,
			final ToneGenerator toneGenerator) throws InterruptedException {
		if (Character.isDigit(digit)) {
			// ignore non digits

			// wait before tone as this helps a sleeping amp wake up
			// last pause isn't needed
			wait(TONE_DURATION + pause);

			int numericValue = Character.getNumericValue(digit);
			toneGenerator.startTone(toneCodes[numericValue], TONE_DURATION);
		}

		if (Character.isSpace(digit)) {
			wait(TONE_DURATION + pause);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			String originalDestination = intent.getDataString();
			
			Log.i(DroidDialer.TAG, "Service tone generation for:"
					+ originalDestination);

			// Disable the real outgoing call
			if (shouldGenerateTonesNotCall()) {
				intent.putExtra(EXTRA_DTMF_DIALED, true);
				try {
					dial(originalDestination, _toneGenerator);
				} catch (InterruptedException e) {
					Log.e(DroidDialer.TAG, "Unable to generate DTMF tones", e);
				}
			}
		}
		
		stopSelf(START_STICKY_COMPATIBILITY); // because we're called from a BroadCastReciever
		return START_STICKY_COMPATIBILITY;
	}

	private boolean shouldGenerateTonesNotCall() {
		return _tonesEnabled;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Free Key On", Toast.LENGTH_LONG).show();

		_toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 80);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Free Key Off", Toast.LENGTH_LONG).show();
	}
}
