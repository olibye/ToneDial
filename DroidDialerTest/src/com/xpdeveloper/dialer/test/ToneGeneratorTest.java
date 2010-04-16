package com.xpdeveloper.dialer.test;

import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.test.ActivityInstrumentationTestCase2;

import com.xpdeveloper.dialer.DTMFModel;
import com.xpdeveloper.dialer.DroidDialer;
import com.xpdeveloper.dialer.IDTMFModel;
import com.xpdeveloper.dialer.NewOutgoingCallBroadcastReceiver;

public class ToneGeneratorTest extends
		ActivityInstrumentationTestCase2<DroidDialer> {
	
	Mockery _mockery = new JUnit3Mockery();
	
	public ToneGeneratorTest() {
		super(DroidDialer.class.getPackage().getName(), DroidDialer.class);
	}

	public synchronized void testTwoTone() throws InterruptedException {
		// Stream Types
		// http://developer.android.com/reference/android/media/AudioManager.html
		new DTMFModel().dial("1 2", new ToneGenerator(AudioManager.STREAM_DTMF,
				80));
	}

	public void testToneOffsetCharacters() {
		int zeroNumericValue = Character.getNumericValue('0');
		int oneNumericValue = Character.getNumericValue('1');
		int aNumericValue = Character.getNumericValue('a');
		assertEquals("Expecting Zero", 0, zeroNumericValue);
		assertEquals("Expecting One", 1, oneNumericValue);
		assertEquals("Expecting Ten", 10, aNumericValue);
	}

	public void testTonesOnPreference() throws InterruptedException {
		DroidDialer activity = (DroidDialer)getActivity();
		activity.setTonesEnabled(true);
		
		final IDTMFModel mockModel = _mockery.mock(IDTMFModel.class);
		_mockery.checking(new Expectations() {{
			one(mockModel).dial(with("42"), with(any(ToneGenerator.class)));
		}});
		
		NewOutgoingCallBroadcastReceiver unit = new NewOutgoingCallBroadcastReceiver(mockModel);
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_PHONE_NUMBER, "42");
		unit.onReceive(getActivity(), intent);
		
		_mockery.assertIsSatisfied();
	}

	public void testTonesOffPreference() throws InterruptedException {
		DroidDialer activity = (DroidDialer)getActivity();
		activity.setTonesEnabled(false);
		
		final IDTMFModel mockModel = _mockery.mock(IDTMFModel.class);
		_mockery.checking(new Expectations() {{
			never(mockModel);
		}});
		
		NewOutgoingCallBroadcastReceiver unit = new NewOutgoingCallBroadcastReceiver(mockModel);
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_PHONE_NUMBER, "42");
		unit.onReceive(getActivity(), intent);
		
		_mockery.assertIsSatisfied();
	}
}
