package net.xpdeveloper.dialer.test;

import java.util.ArrayList;

import net.xpdeveloper.android.IIntentHelper;
import net.xpdeveloper.dialer.ToneDialActivity;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jayway.android.robotium.solo.Solo;

public class ToneDialUITest extends
		ActivityInstrumentationTestCase2<ToneDialActivity> {

	private Solo _solo;

	/**
	 * Tests require a default constructor
	 */
	public ToneDialUITest() {
		super(ToneDialActivity.class.getPackage().getName(),
				ToneDialActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		_solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {

		try {
			_solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();

	}

	/**
	 * TODO consider using Expectation objects somehow. We need to check the
	 * intent which gets very verbose in JMock.
	 * Maybe I have to write a matcher
	 */
	public void testEnableServiceRaisesStartServiceIntent() {

		class MockIntentHelper implements IIntentHelper {
			boolean isSatisfied = false;

			@Override
			public void startService(Intent intent) {
				assertEquals(ToneDialActivity.ACTION_PREFERENCE_CHANGE, intent
						.getAction());
				assertEquals("+44", intent
						.getStringExtra(ToneDialActivity.EXTRA_COUNTRY_CODE));
				assertEquals("0", intent
						.getStringExtra(ToneDialActivity.EXTRA_TRUNK_CODE));
				isSatisfied = true;
			}

			public void stopService(Intent intent) {
				fail("Not expecting this");
			}
		};
		MockIntentHelper mockIntentHelper = new MockIntentHelper();

		ToneDialActivity unit = getActivity();
		unit.setIIntentHelper(mockIntentHelper);
		unit.enableService(true);

		assertTrue("Is not satisfied", mockIntentHelper.isSatisfied);
	}

	public void testToneDial() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial"));

		ArrayList<Button> buttons = _solo.getCurrentButtons();
		ArrayList<ListView> lists = _solo.getCurrentListViews();
		ListView preferences = lists.get(0);
		View enableRow = preferences.getChildAt(0);
		_solo.clickOnScreen(enableRow);
	}

}
