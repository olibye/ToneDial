package net.xpdeveloper.dialer.test;

import java.util.ArrayList;

import net.xpdeveloper.android.IIntentHelper;
import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialService;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.provider.Contacts;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListAdapter;
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
		
		// Setback for UK for my everyday use!
		setupPreferences(getActivity(), "+44", "0");
		
		super.tearDown();
	}

	/**
	 * TODO consider using Expectation objects somehow. We need to check the
	 * intent which gets very verbose in JMock. Maybe I have to write a matcher
	 */
	public void testEnableServiceRaisesStartServiceIntent() {

		class MockIntentHelper implements IIntentHelper {
			boolean isSatisfied = false;

			@Override
			public void startService(Intent intent) {
				assertEquals(ToneDialService.ACTION_SERVICE_STATE_CHANGE,
						intent.getAction());
				isSatisfied = true;
			}

			public void stopService(Intent intent) {
				fail("Not expecting this");
			}
		}

		MockIntentHelper mockIntentHelper = new MockIntentHelper();

		ToneDialActivity unit = getActivity();
		unit.setIIntentHelper(mockIntentHelper);

		// Can not change preferences directly from this thread
		unit.enableService(true);

		assertTrue("Is not satisfied", mockIntentHelper.isSatisfied);
	}

	private void setupPreferences(ToneDialActivity unit, String countryCode,
			String trunkCode) {
		setPreference(unit, countryCode, ToneDialActivity.EXTRA_COUNTRY_CODE);
		setPreference(unit, trunkCode, ToneDialActivity.EXTRA_TRUNK_CODE);
	}

	private void setPreference(ToneDialActivity unit, String code, String key) {
		EditTextPreference preference = (EditTextPreference) unit
				.findPreference(key);
		assertNotNull("Could not find preference:" + key, preference);
		Editor editor = preference.getEditor();
		editor.putString(key, code);
		editor.commit();
	}

	public void testCountryCodeSummaryChange() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial"));

		_solo.clickInList(5);
		_solo.clearEditText(0);
		_solo.enterText(0, "+1");
		_solo.clickOnText("OK");
		
		ArrayList<ListView> lists = _solo.getCurrentListViews();
		ListAdapter preferences = lists.get(0).getAdapter();
		Preference preference = (Preference)preferences.getItem(2);
		assertEquals("Summary didn't change", "Replace +1 with Trunk Code",preference.getSummary()); 
	}

	public void testTrunkCodeSummaryChange() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial"));

		_solo.clickInList(7);
		_solo.clearEditText(0);
		_solo.enterText(0, "0");
		_solo.clickOnText("OK");
		
		ArrayList<ListView> lists = _solo.getCurrentListViews();
		ListAdapter preferences = lists.get(0).getAdapter();
		Preference preference = (Preference)preferences.getItem(3);
		assertEquals("Summary didn't change", "Country Code replaced by 0",preference.getSummary()); 
	}

	public void testContactsURI() {
		assertEquals("content://contacts", Contacts.CONTENT_URI.toString());
	}
}
