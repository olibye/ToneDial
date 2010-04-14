package com.xpdeveloper.dialer.test;

import android.test.ActivityInstrumentationTestCase2;

import com.xpdeveloper.dialer.DTMFModel;
import com.xpdeveloper.dialer.DroidDialer;

public class ToneGeneratorTest extends
		ActivityInstrumentationTestCase2<DroidDialer> {

	public ToneGeneratorTest() {
		super(DroidDialer.class.getPackage().getName(), DroidDialer.class);
	}

	public synchronized void testTwoTone() throws InterruptedException {
		// Stream Types
		// http://developer.android.com/reference/android/media/AudioManager.html
		DroidDialer unit = getActivity();
		DTMFModel.dial("1 2");
	}

	public void testToneOffsetCharacters() {
		int zeroNumericValue = Character.getNumericValue('0');
		int oneNumericValue = Character.getNumericValue('1');
		int aNumericValue = Character.getNumericValue('a');
		assertEquals("Expecting Zero", 0, zeroNumericValue);
		assertEquals("Expecting One", 1, oneNumericValue);
		assertEquals("Expecting Ten", 10, aNumericValue);
	}

}
