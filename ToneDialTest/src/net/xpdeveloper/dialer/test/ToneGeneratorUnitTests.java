package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialModel;
import net.xpdeveloper.dialer.api4.ToneDialModelAPI4;
import android.test.ActivityUnitTestCase;

/**
 * Make some noise!
 * @author byeo
 *
 */
public class ToneGeneratorUnitTests extends ActivityUnitTestCase<ToneDialActivity> {

	public ToneGeneratorUnitTests() {
		super(ToneDialActivity.class);
	}
	
	public void testToneDialModelMakesNoise() throws InterruptedException {
		ToneDialModel unit = new ToneDialModelAPI4(80);
		
		unit.dial("0123456789");
		unit.release();
	}

}
