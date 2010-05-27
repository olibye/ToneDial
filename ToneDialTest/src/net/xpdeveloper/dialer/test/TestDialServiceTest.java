package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.IToneDialModel;
import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialService;

import org.jmock.Expectations;
import org.jmock.Mockery;

import android.content.Intent;
import android.net.Uri;
import android.test.ServiceTestCase;

public class TestDialServiceTest extends ServiceTestCase<ToneDialService> {
	private Mockery _mockery = new Mockery();

	public TestDialServiceTest() {
		super(ToneDialService.class);
	}

	
	
	public void testUsesCountryAndTrunkCode() throws InterruptedException {
		final IToneDialModel mockModel = _mockery.mock(IToneDialModel.class);

		_mockery.checking(new Expectations() {
			{
				one(mockModel).dial("01202123456");
				
				// Ignore or expections are thrown teardown when the service is shutdown
				ignoring(mockModel).release(); 
			}
		});

		Intent intent = new Intent(ToneDialActivity.ACTION_PREFERENCE_CHANGE);
		intent.putExtra(ToneDialActivity.EXTRA_COUNTRY_CODE, "+44");
		intent.putExtra(ToneDialActivity.EXTRA_TRUNK_CODE, "0");

		startService(intent); // or there is no service

		ToneDialService unit = getService();
		unit.setModel(mockModel);

		// It appears to handle the start elsewhere, so call again
		unit.onStart(intent, 0);
		
		Intent dialIntent = new Intent();
		dialIntent.setAction(ToneDialService.ACTION_DIAL);
		dialIntent.setData(Uri.parse("tel:+441202123456"));
		unit.onStart(dialIntent, 0); // startService doesn't appear to
		// work twice

		_mockery.assertIsSatisfied();
	}
}
