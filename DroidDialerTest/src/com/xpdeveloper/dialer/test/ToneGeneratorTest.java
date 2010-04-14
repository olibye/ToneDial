package com.xpdeveloper.dialer.test;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.test.ActivityInstrumentationTestCase2;

import com.xpdeveloper.dialer.DroidDialer;

public class ToneGeneratorTest extends ActivityInstrumentationTestCase2<DroidDialer> {

	
	public ToneGeneratorTest() {
		super(DroidDialer.class.getPackage().getName(), DroidDialer.class);
	}

	public synchronized void testTwoTone() throws InterruptedException {
		// Stream Types
		// http://developer.android.com/reference/android/media/AudioManager.html
		final ToneGenerator _toneGenerator= new ToneGenerator(AudioManager.STREAM_DTMF, 50);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_1,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_2,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_3,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_4,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_5,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_6,1000));
		this.wait(1000);
		assertTrue(_toneGenerator.startTone(ToneGenerator.TONE_DTMF_7,1000));
		this.wait(1000);
		_toneGenerator.release();
	}
	
}
