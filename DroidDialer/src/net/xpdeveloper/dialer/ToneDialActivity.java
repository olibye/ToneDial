/*
 * Copyright (c) Oliver Bye 2010
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.xpdeveloper.dialer;

import net.xpdeveloper.android.IIntentHelper;
import net.xpdeveloper.android.IntentHelper;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * I am the UI. Since my persistence is simple preferences these are managed by
 * my super class.
 * 
 * Reference: http://developer.android.com/reference/android/app/Activity.html#
 * ActivityLifecycle
 * 
 * @author byeo
 * 
 */
public class ToneDialActivity extends PreferenceActivity {
	public static final String TAG = "DroidDialer";
	public static final String PREFS_NAME = "DroidDailer";

	public static final String ACTION_PREFERENCE_CHANGE = "net.xpdeveloper.dialer.PREFERENCE_CHANGE";
	public static final String EXTRA_COUNTRY_CODE = "net.xpdeveloper.dialer.EXTRA_COUNTRY_CODE";
	public static final String EXTRA_TRUNK_CODE = "net.xpdeveloper.dialer.EXTRA_TRUNK_CODE";

	private static final String PREF_ENABLE_TONES = "enableTones";

	private IIntentHelper _intentHelper;

	public ToneDialActivity() {
		_intentHelper = new IntentHelper(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// Start service if that was the previous preference
		enableService(isServiceEnabled());
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean reply = super.onPreferenceTreeClick(preferenceScreen,
				preference);

		if (preference instanceof CheckBoxPreference) {
			CheckBoxPreference enableCheckBox = (CheckBoxPreference) preference;
			enableService(enableCheckBox.isChecked());
		}

		return reply;
	}

	private String getEditTextPreference(String key) {
		EditTextPreference preference = (EditTextPreference) findPreference(key);
		return preference.getText();
	}

	public void enableService(boolean enableTones) {
		Intent toneDial = new Intent(ACTION_PREFERENCE_CHANGE);

		toneDial.putExtra(EXTRA_COUNTRY_CODE,
				getEditTextPreference(EXTRA_COUNTRY_CODE));
		toneDial.putExtra(EXTRA_TRUNK_CODE,
				getEditTextPreference(EXTRA_TRUNK_CODE));

		if (enableTones) {
			_intentHelper.startService(toneDial);
		} else {
			_intentHelper.stopService(toneDial);
		}
	}

	public boolean isServiceEnabled() {
		CheckBoxPreference enabled = (CheckBoxPreference) findPreference(PREF_ENABLE_TONES);
		return enabled.isChecked();
	}

	public void setIIntentHelper(IIntentHelper intentHelper) {
		_intentHelper = intentHelper;
	}
}