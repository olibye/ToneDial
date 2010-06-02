package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.common.api1.API1ToneGeneratorStrategy;
import net.xpdeveloper.dialer.common.api5.API5ToneGeneratorStrategy;
import net.xpdeveloper.dialer.common.model.ToneDialModel;
import android.os.Build;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

/**
 * Make some noise!
 * 
 * @author byeo
 * 
 */
public class ToneGeneratorUnitTests extends
		ActivityInstrumentationTestCase2<ToneDialActivity> {

	public ToneGeneratorUnitTests() {
		super(ToneDialActivity.class.getPackage().getName(),
				ToneDialActivity.class);
	}

	public void testToneDialModelMakesNoise() throws InterruptedException {
		ToneDialModel unit = new ToneDialModel(PreferenceManager
				.getDefaultSharedPreferences(getActivity()));

		unit.localise("0-123-45-6789#*").dial();
		unit.release();
	}

	public void testDetermineOSLevel() {

		checkVersionBuildsClassOrThrowsVerify(1,
				API1ToneGeneratorStrategy.class, false);
		checkVersionBuildsClassOrThrowsVerify(2,
				API1ToneGeneratorStrategy.class, false);
		checkVersionBuildsClassOrThrowsVerify(3,
				API1ToneGeneratorStrategy.class, false);
		checkVersionBuildsClassOrThrowsVerify(4,
				API1ToneGeneratorStrategy.class, false);

		boolean shouldThrowVerifyError = Build.VERSION.SDK_INT < 5;
		checkVersionBuildsClassOrThrowsVerify(5,
				API5ToneGeneratorStrategy.class, shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(6,
				API5ToneGeneratorStrategy.class, shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(7,
				API5ToneGeneratorStrategy.class, shouldThrowVerifyError);
		checkVersionBuildsClassOrThrowsVerify(8,
				API5ToneGeneratorStrategy.class, shouldThrowVerifyError);
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
