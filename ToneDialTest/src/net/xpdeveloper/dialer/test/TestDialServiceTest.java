package net.xpdeveloper.dialer.test;

import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialService;
import net.xpdeveloper.dialer.model.DialMemento;
import net.xpdeveloper.dialer.model.IToneDialModel;

import org.jmock.Expectations;
import org.jmock.Mockery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.ServiceTestCase;

public class TestDialServiceTest extends ServiceTestCase<ToneDialService> {
	private Mockery _mockery = new Mockery();

	public TestDialServiceTest() {
		super(ToneDialService.class);
	}

	public void testDialManyIfDialOnceNotSet() throws InterruptedException {
		final IToneDialModel mockModel = _mockery.mock(IToneDialModel.class);
		final DialMemento memento = new DialMemento(mockModel, "somenumber");
		
		_mockery.checking(new Expectations() {
			{
				exactly(2).of(mockModel).localise("+441202123456");
				will(returnValue(memento));
				exactly(2).of(mockModel).dial(memento);

				// Ignore or exceptions are thrown teardown when the service is shutdown
				ignoring(mockModel).release(); 
			}
		});

		setPreference(ToneDialActivity.PREF_ENABLE_TONES_ONCE,false);

		Intent intent = new Intent(ToneDialService.ACTION_SERVICE_STATE_CHANGE);
		startService(intent); // or there is no service

		ToneDialService unit = getService();
		unit.setModel(mockModel);

		// It appears to handle the start elsewhere, so call again
		unit.onStart(intent, 0);
		
		Intent dialIntent = new Intent();
		dialIntent.setAction(ToneDialService.ACTION_DIAL);
		dialIntent.setData(Uri.parse("tel:+441202123456"));
		 
		//can't call startService directly twice
		unit.onStart(dialIntent, 0);
		
		// should remain enabled
		unit.onStart(dialIntent, 0);

		_mockery.assertIsSatisfied();
	}

	public void testOnlyDialOnceIfPreferenceIsSet() throws InterruptedException {
		final IToneDialModel mockModel = _mockery.mock(IToneDialModel.class);
		final DialMemento memento = new DialMemento(mockModel, "01202123456");

		_mockery.checking(new Expectations() {
			{
				one(mockModel).localise("+441202123456");
				will(returnValue(memento));
				one(mockModel).dial(memento);

				// Ignore or expections are thrown teardown when the service is
				// shutdown
				ignoring(mockModel).release();
			}
		});

		setPreference(ToneDialActivity.PREF_ENABLE_TONES_ONCE, true);

		Intent intent = new Intent(ToneDialService.ACTION_SERVICE_STATE_CHANGE);
		startService(intent); // or there is no service

		ToneDialService unit = getService();
		unit.setModel(mockModel);

		// It appears to handle the start elsewhere, so call again
		unit.onStart(intent, 0);

		Intent dialIntent = new Intent();
		dialIntent.setAction(ToneDialService.ACTION_DIAL);
		dialIntent.setData(Uri.parse("tel:+441202123456"));

		unit.onStart(dialIntent, 0);

		// second dial should no be made
		unit.onStart(dialIntent, 0);

		assertFalse("Should have disabled tone dial",
				getPreference(ToneDialActivity.PREF_ENABLE_TONES));

		_mockery.assertIsSatisfied();
	}

	private boolean getPreference(String key) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		return prefs.getBoolean(key, false);
	}

	private void setPreference(String prefEnableTonesOnce, boolean state) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		Editor editor = prefs.edit();
		editor.putBoolean(prefEnableTonesOnce, state);
		editor.commit();
	}
}
