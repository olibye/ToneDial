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

	public static final String EXTRA_COUNTRY_CODE = "net.xpdeveloper.dialer.EXTRA_COUNTRY_CODE";
	public static final String EXTRA_TRUNK_CODE = "net.xpdeveloper.dialer.EXTRA_TRUNK_CODE";
	
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

	public void testEnableServiceRaisesStartServiceIntent() {
		
		final IIntentHelper mockIntentHelper = new IIntentHelper() {
			@Override
			public void startService(Intent intent) {
				assertEquals(ToneDialActivity.ACTION_PREFERENCE_CHANGE,intent.getAction());
				assertEquals("+44", intent.getStringExtra(EXTRA_COUNTRY_CODE));
				assertEquals("+44", intent.getStringExtra(EXTRA_TRUNK_CODE));
			}
		};

		ToneDialActivity unit = getActivity();
		unit.setIIntentHelper(mockIntentHelper);
		unit.enableService(true);

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
