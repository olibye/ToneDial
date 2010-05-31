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
package net.xpdeveloper.dialer.api1;

import net.xpdeveloper.dialer.IToneGeneratorStrategy;
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
public class API1ToneGeneratorStrategy implements IToneGeneratorStrategy {
	private ToneGenerator _toneGenerator;

	public API1ToneGeneratorStrategy() {
		this(80);
	}

	public API1ToneGeneratorStrategy(int volume) {
		// http://developer.android.com/reference/android/media/ToneGenerator.html#ToneGenerator(int,
		// int)
		_toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);
	}

	@Override
	public synchronized void generateTone(final int dtmfCode, final int pause)
			throws InterruptedException {
		// Pause for to pronounce duplicate keys
		// Pause at start to give amp time to power up
		wait(pause);

		_toneGenerator.startTone(dtmfCode);

		// Wait after tone start to support Donut which lacks
		// startTone( , duration) method
		wait(pause);
		_toneGenerator.stopTone();
	}

	@Override
	public void release() {
		_toneGenerator.release();
	}

}
