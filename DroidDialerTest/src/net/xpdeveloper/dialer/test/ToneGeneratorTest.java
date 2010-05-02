package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialModel;
import net.xpdeveloper.dialer.ToneDialService;

import org.jmock.Mockery;
import org.jmock.integration.junit3.JUnit3Mockery;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.test.ActivityInstrumentationTestCase2;


public class ToneGeneratorTest extends
		ActivityInstrumentationTestCase2<ToneDialActivity> {
	
	Mockery _mockery = new JUnit3Mockery();
	
	public ToneGeneratorTest() {
		super(ToneDialActivity.class.getPackage().getName(), ToneDialActivity.class);
	}

	public synchronized void testTwoTone() throws InterruptedException {
		// Stream Types
		// http://developer.android.com/reference/android/media/AudioManager.html
		new ToneDialModel().dial("1 2", new ToneGenerator(AudioManager.STREAM_DTMF,
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
	
	
	public void testServiceIgnoresEmergencyNumbers() {		
		assertFalse("Should not dial 999",ToneDialService.invoke(getActivity(),"999"));
		assertFalse("Should not dial 999",ToneDialService.invoke(getActivity(),"911"));
	}
}
