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
import android.content.IntentFilter;
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
public class DroidDialer extends PreferenceActivity {
	public static String PREFS_NAME = "DroidDailer";

	private static final String PREF_ENABLE_TONES = "enableTones";

	private NewOutgoingCallBroadcastReceiver _receiver;
	private IDTMFModel _model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_model = new DTMFModel();

		setupReciver();

		addPreferencesFromResource(R.xml.preferences);
	}

	public void setModel(IDTMFModel model) {
		_model = model;
		setupReciver();
	}

	public void setupReciver() {
		// reset myself from preferences
		setTonesEnabled(getTonesEnabled());
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		boolean reply = super.onPreferenceTreeClick(preferenceScreen, preference);
		
		if (preference instanceof CheckBoxPreference) {
			CheckBoxPreference enableCheckBox = (CheckBoxPreference)preference;
			setTonesEnabled(enableCheckBox.isChecked());
		}
		
		return reply;
	}

	/**
	 * http://developer.android.com/reference/android/content/Intent.html#
	 * ACTION_NEW_OUTGOING_CALL Register/Unregister for
	 * android.intent.action.NEW_OUTGOING_CALL Category:
	 * android.intent.category.ALTERNATIVE
	 * 
	 * @param enableTones
	 */
	public void setTonesEnabled(boolean enableTones) {
		manageRegistration(enableTones);
	}

	private void manageRegistration(boolean enableTones) {
		if (enableTones) {
			if (_receiver == null) {
				_receiver = new NewOutgoingCallBroadcastReceiver(_model);
			}

			registerReceiver(_receiver, new IntentFilter(
					Intent.ACTION_NEW_OUTGOING_CALL));
		} else {
			if (_receiver != null) {
				unregisterReceiver(_receiver);
				_receiver = null; // It's not useable anymore
			}
		}
	}

	public boolean getTonesEnabled() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		return settings.getBoolean(PREF_ENABLE_TONES, true);
	}
}