/*
 * Copyright (c) Oliver Bye 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.xpdeveloper.dialer.common.model;

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
import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.common.api1.API1ToneGeneratorStrategy;
import net.xpdeveloper.dialer.common.api5.API5ToneGeneratorStrategy;
import android.content.SharedPreferences;
import android.media.ToneGenerator;
import android.os.Build;

/**
 * I generate DTMF tones. They may be sent to me from a GUI or a
 * BroadCastReceiver. My implementations handle the API differences
 */
public class ToneDialModel implements IToneDialModel {

	public static final int TONE_PAUSE = 150;

	protected static final int[] toneCodes = new int[] { TONE_DTMF_0,
			TONE_DTMF_1, TONE_DTMF_2, TONE_DTMF_3, TONE_DTMF_4, TONE_DTMF_5,
			TONE_DTMF_6, TONE_DTMF_7, TONE_DTMF_8, TONE_DTMF_9 };

	public static final String EMERGENCY_999 = "999";
	public static final String EMERGENCY_911 = "911";

	private IToneGeneratorStrategy _toneGeneratorStrategy;
	private SharedPreferences _preferences;

	public ToneDialModel(IToneGeneratorStrategy strategy,
			SharedPreferences preferences) {
		_toneGeneratorStrategy = strategy;
		_preferences = preferences;
	}

	public ToneDialModel(SharedPreferences preferences) {
		this(buildModel(), preferences);
	}

	public DialMemento localise(String originalDestination) {
		DialMemento reply = new DialMemento(this,
				adjustNumber(originalDestination));
		return reply;
	}

	public void dial(DialMemento memento) throws InterruptedException {
		String dialString = memento.getDialString();
		int digitCount = dialString.length();
		if (digitCount > 0) {
			// Big pause before for the first tone
			// We typically see "AudioFlinger write blocked for 172 ms
			dialDigitOrPause(dialString.charAt(0), TONE_PAUSE * 3);

			for (int digitIndex = 1; digitIndex < digitCount; digitIndex++) {
				dialDigitOrPause(dialString.charAt(digitIndex), TONE_PAUSE);
			}

			// Pause to make sure this app doesn't quit before the tone is
			// finished
			// For example when running in tests
			dialDigitOrPause(' ', TONE_PAUSE);
		}
	}

	/*
	 * TODO needs a hash lookup table
	 */
	private synchronized void dialDigitOrPause(final char digit, final int pause)
			throws InterruptedException {

		if (Character.isDigit(digit)) {
			// ignore non digits
			int numericValue = Character.getNumericValue(digit);
			_toneGeneratorStrategy.generateTone(toneCodes[numericValue], pause);
		}

		if (Character.isWhitespace(digit) || '-' == digit) {
			wait(pause * 2);
		}

		if ('#' == digit) {
			_toneGeneratorStrategy.generateTone(ToneGenerator.TONE_DTMF_P,
					pause);
		}

		if ('*' == digit) {
			_toneGeneratorStrategy.generateTone(ToneGenerator.TONE_DTMF_S,
					pause);
		}
	}

	@Override
	public void release() {
		_toneGeneratorStrategy.release();
	}

	private String adjustNumber(String originalDestination) {
		String reply = originalDestination;

		String countryCode = lookupCode(ToneDialActivity.EXTRA_COUNTRY_CODE);
		String trunkCode = lookupCode(ToneDialActivity.EXTRA_TRUNK_CODE);

		if (originalDestination.startsWith(countryCode)) {
			StringBuffer replyBuffer = new StringBuffer();
			replyBuffer.append(trunkCode);
			replyBuffer.append(originalDestination.substring(countryCode
					.length()));
			reply = replyBuffer.toString();
		}

		return reply;
	}

	private String lookupCode(String key) {
		return _preferences.getString(key, "");
	}

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
	 * 
	 * @param buildSDKVersion
	 * @return
	 */
	public static IToneGeneratorStrategy buildModel(int buildSDKVersion) {
		if (buildSDKVersion < 5) {
			return new API1ToneGeneratorStrategy();
		}
		return new API5ToneGeneratorStrategy();
	}

	public static IToneGeneratorStrategy buildModel() {
		return buildModel(Integer.parseInt(Build.VERSION.SDK));
	}

}
