package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialModel;
import net.xpdeveloper.dialer.api1.ToneDialModelAPI1;
import net.xpdeveloper.dialer.api5.ToneDialModelAPI5;
import android.os.Build;
import android.test.ActivityUnitTestCase;

/**
 * Make some noise!
 * 
 * @author byeo
 * 
 */
public class ToneGeneratorUnitTests extends
		ActivityUnitTestCase<ToneDialActivity> {

	public ToneGeneratorUnitTests() {
		super(ToneDialActivity.class);
	}

	public void testToneDialModelMakesNoise() throws InterruptedException {
		ToneDialModel unit = ToneDialModel.buildModel();

		unit.dial("0-123-45-6789#*");
		unit.release();
	}

	public void testDetermineOSLevel() {
		
		checkVersionBuildsClassOrThrowsVerify(1, ToneDialModelAPI1.class,false);
		checkVersionBuildsClassOrThrowsVerify(2, ToneDialModelAPI1.class,false);
		checkVersionBuildsClassOrThrowsVerify(3, ToneDialModelAPI1.class,false);
		checkVersionBuildsClassOrThrowsVerify(4, ToneDialModelAPI1.class,false);
		
		boolean shouldThrowVerifyError = Build.VERSION.SDK_INT < 5;
		checkVersionBuildsClassOrThrowsVerify(5, ToneDialModelAPI5.class,shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(6, ToneDialModelAPI5.class,shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(7, ToneDialModelAPI5.class,shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(8, ToneDialModelAPI5.class,shouldThrowVerifyError);
	}

	private void checkVersionBuildsClassOrThrowsVerify(int sdkVersion,
			Class<?> clazz, boolean shouldThrow) {
		try {
			assertEquals("Expecting a model for SDK level " + sdkVersion,
					clazz, ToneDialModel.buildModel(sdkVersion).getClass());
		} catch (Throwable t) {
			if (shouldThrow) {
				assertEquals("Expecting a verify error", VerifyError.class, t
						.getClass());
			} else {
				fail("Should be able to construct a " + clazz
						+ " at SDK level " + sdkVersion);
			}
		}
	}

}
