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

import java.text.MessageFormat;

import net.xpdeveloper.android.IIntentHelper;
import net.xpdeveloper.android.IntentHelper;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;

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
	public static final String TAG = "ToneDial";

	public static final String ACTION_PREFERENCE_CHANGE = "net.xpdeveloper.dialer.PREFERENCE_CHANGE";
	public static final String EXTRA_COUNTRY_CODE = "net.xpdeveloper.dialer.EXTRA_COUNTRY_CODE";
	public static final String EXTRA_TRUNK_CODE = "net.xpdeveloper.dialer.EXTRA_TRUNK_CODE";

	public static final String PREF_ENABLE_TONES = "enableTones";
	public static final String PREF_ENABLE_TONES_ONCE = "enableTonesOnce";
	public static final String PREF_CONTACTS = "net.xpdeveloper.dialer.PREF_CONTACTS";

	private IIntentHelper _intentHelper;
	private MessageFormat countrySummaryFormat, trunkSummaryFormat;
	
	public ToneDialActivity() {
		_intentHelper = new IntentHelper(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		countrySummaryFormat = new MessageFormat(getText(R.string.summary_country_code_preference).toString());
		trunkSummaryFormat = new MessageFormat(getText(R.string.summary_trunk_code_preference).toString());
		
		// Add listeners
		findPreference(PREF_ENABLE_TONES).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				enableService((Boolean)value);
				return true; // means persist the value
			}
		});
		
		OnPreferenceChangeListener countryCodeChange = 
		new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				firePreferenceChange(value.toString(), getEditTextPreference(EXTRA_TRUNK_CODE));
				setSummary(preference, countrySummaryFormat, value);
				return true; // means persist the value
			}
		};
		
		OnPreferenceChangeListener trunkCodeChange = 
			new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object value) {
					firePreferenceChange(getEditTextPreference(EXTRA_COUNTRY_CODE), value.toString());
					setSummary(preference, trunkSummaryFormat, value);
					return true; // means persist the value
				}
			};

		EditTextPreference countryPreference = (EditTextPreference)findPreference(EXTRA_COUNTRY_CODE);
		countryPreference.setOnPreferenceChangeListener(countryCodeChange);
		setSummary(countryPreference, countrySummaryFormat, countryPreference.getText());

		EditTextPreference trunkPreference = (EditTextPreference)findPreference(EXTRA_TRUNK_CODE);
		trunkPreference.setOnPreferenceChangeListener(trunkCodeChange);
		setSummary(trunkPreference, trunkSummaryFormat, trunkPreference.getText());
		
		enableService(isServiceEnabled());
	}
	
	private void setSummary(Preference pref, MessageFormat format, Object code) {
		if(code.toString().length() == 0) {
			code = "nothing";
		}
		pref.setSummary(format.format(new Object[]{code}));
	}
	
	private String getEditTextPreference(String key) {
		EditTextPreference preference = (EditTextPreference) findPreference(key);
		return preference.getText();
	}

	public void firePreferenceChange(String countryCode, String trunkCode) {
		Intent preferenceChange = new Intent(ACTION_PREFERENCE_CHANGE);
		preferenceChange.putExtra(EXTRA_COUNTRY_CODE,
				countryCode);
		preferenceChange.putExtra(EXTRA_TRUNK_CODE,
				trunkCode);
		_intentHelper.startService(preferenceChange);
	}

	public void enableService(boolean enableTones) {
		Intent toneDial = new Intent(ToneDialService.ACTION_SERVICE_STATE_CHANGE);
		addCodes(toneDial);

		if (enableTones) {
			_intentHelper.startService(toneDial);
		} else {
			_intentHelper.stopService(toneDial);
		}
	}

	private void addCodes(Intent toneDial) {
		toneDial.putExtra(EXTRA_COUNTRY_CODE,
				getEditTextPreference(EXTRA_COUNTRY_CODE));
		toneDial.putExtra(EXTRA_TRUNK_CODE,
				getEditTextPreference(EXTRA_TRUNK_CODE));
	}

	public boolean isServiceEnabled() {
		CheckBoxPreference enabled = (CheckBoxPreference) findPreference(PREF_ENABLE_TONES);
		return enabled.isChecked();
	}

	public void setIIntentHelper(IIntentHelper intentHelper) {
		_intentHelper = intentHelper;
	}
}