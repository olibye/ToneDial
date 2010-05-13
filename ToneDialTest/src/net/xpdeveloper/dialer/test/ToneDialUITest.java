package net.xpdeveloper.dialer.test;

import net.xpdeveloper.android.IIntentHelper;
import net.xpdeveloper.dialer.ToneDialActivity;
import net.xpdeveloper.dialer.ToneDialService;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.preference.EditTextPreference;
import android.provider.Contacts;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

public class ToneDialUITest extends
		ActivityInstrumentationTestCase2<ToneDialActivity> {

	private Solo _solo;
	private Mockery _mockery = new Mockery();

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
	 * intent which gets very verbose in JMock. Maybe I have to write a matcher
	 */
	public void testEnableServiceRaisesStartServiceIntent() {

		class MockIntentHelper implements IIntentHelper {
			boolean isSatisfied = false;

			@Override
			public void startService(Intent intent) {
				assertEquals(ToneDialService.ACTION_SERVICE_STATE_CHANGE,
						intent.getAction());
				assertEquals("+44", intent
						.getStringExtra(ToneDialActivity.EXTRA_COUNTRY_CODE));
				assertEquals("0", intent
						.getStringExtra(ToneDialActivity.EXTRA_TRUNK_CODE));
				isSatisfied = true;
			}

			public void stopService(Intent intent) {
				fail("Not expecting this");
			}
		}

		MockIntentHelper mockIntentHelper = new MockIntentHelper();

		ToneDialActivity unit = getActivity();
		unit.setIIntentHelper(mockIntentHelper);

		setupPreferences(unit, "+44", "0");

		// Can not change preferences directly from this thread
		unit.enableService(true);

		assertTrue("Is not satisfied", mockIntentHelper.isSatisfied);
	}

	private void setupPreferences(ToneDialActivity unit, String countryCode, String trunkCode) {
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

	public void testPreferenceChangeIntentOnCountryCodeChange() {
		final IIntentHelper mockIntentHelper = _mockery
				.mock(IIntentHelper.class);

		class IntentMatcher extends BaseMatcher<Intent> {
			public boolean matches(Object item) {
				if (item instanceof Intent) {
					Intent intent = (Intent) item;
					assertEquals(ToneDialActivity.ACTION_PREFERENCE_CHANGE,
							intent.getAction());
					assertEquals(
							"+44",
							intent
									.getStringExtra(ToneDialActivity.EXTRA_COUNTRY_CODE));
					assertEquals("0", intent
							.getStringExtra(ToneDialActivity.EXTRA_TRUNK_CODE));
					return true;
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Intent checking");
			}
		}

		_mockery.checking(new Expectations() {
			{
				one(mockIntentHelper).startService(with(new IntentMatcher()));
			}
		});

		ToneDialActivity unit = getActivity();
		unit.setIIntentHelper(mockIntentHelper);

		setupPreferences(unit, "+44", "0");

		unit.firePreferenceChange();

		_mockery.assertIsSatisfied();
	}

	public void testCountryCodeSummaryChange() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial"));

		ToneDialActivity unit = getActivity();
		setupPreferences(unit, "+44", "0");

		_solo.clickOnText("Country Code");
		_solo.enterText(0, "+1");
		_solo.clickOnText("OK");
		assertTrue("Should change country code summary", _solo
				.searchText("Replace +1 with Trunk Code"));
	}

	public void testTrunkCodeSummaryChange() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial"));

		ToneDialActivity unit = getActivity();
		setupPreferences(unit, "", "");
		
		_solo.clickOnText("Trunk Code");
		_solo.enterText(0, "1-");
		_solo.clickOnText("OK");
		assertTrue("Should change trunk code summary", _solo
				.searchText("Country Code replaced by 1-"));
	}
	
	public void testContactsURI() {
		assertEquals("content://contacts", Contacts.CONTENT_URI.toString());
	}
}
