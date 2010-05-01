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
package com.xpdeveloper.dialer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

/**
 * 
 * @author byeo
 * 
 */
public class ToneDialActivity extends PreferenceActivity {
	public static final String TAG = "DroidDialer";
	public static final String PREFS_NAME = "DroidDailer";

	private static final String PREF_ENABLE_TONES = "enableTones";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		// Start service if that was the previous preference
		enableService(isServiceEnabled());
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean reply = super.onPreferenceTreeClick(preferenceScreen, preference);
		
		if (preference instanceof CheckBoxPreference) {
			CheckBoxPreference enableCheckBox = (CheckBoxPreference)preference;
			enableService(enableCheckBox.isChecked());
		}
		
		return reply;
	}

	public void enableService(boolean enableTones) {
		Intent toneDial = new Intent(this, ToneDialService.class);
		if (enableTones) {
			startService(toneDial);
		}
		else {
			stopService(toneDial);
		}
	}

	public boolean isServiceEnabled() {
		CheckBoxPreference enabled = (CheckBoxPreference)findPreference(PREF_ENABLE_TONES);
		return enabled.isChecked();
	}
}