package net.xpdeveloper.dialer.test;

import java.util.ArrayList;

import net.xpdeveloper.dialer.ToneDialActivity;
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

	public void testEnableService() {
		ToneDialActivity unit = getActivity();
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
