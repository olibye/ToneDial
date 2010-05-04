package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.NumberTweaker;
import net.xpdeveloper.dialer.ToneDialModel;
import net.xpdeveloper.dialer.ToneDialService;
import android.test.ServiceTestCase;

public class ToneGeneratorTest extends ServiceTestCase<ToneDialService> {
	
	public ToneGeneratorTest() {
		super(ToneDialService.class);
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
		assertTrue("Should not dial 999", ToneDialModel
				.isEmergencyNumer("999"));
		assertTrue("Should not dial 999", ToneDialModel
				.isEmergencyNumer("911"));
	}

	public void testSwapLocalCountryPrefixForSTDPrefix()
			throws InterruptedException {
		NumberTweaker unit = new NumberTweaker();
		unit.addSwap("+44", "0");

		assertEquals("Should not dial the country code for our country",
				"01202", unit.tweak("+441202"));
	}
}
