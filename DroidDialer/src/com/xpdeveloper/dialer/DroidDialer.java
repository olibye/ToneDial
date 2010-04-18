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

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 
 * @author byeo
 * 
 */
public class DroidDialer extends Activity {
	public static String PREFS_NAME = "DroidDailer";

	private static final String PREF_ENABLE_TONES = "enableTones";

	private RadioGroup _enableGroup;
	private NewOutgoingCallBroadcastReceiver _receiver;
	private IDTMFModel _model;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		_model = new DTMFModel();

		setupReciver();
		setupUi();

	}

	public void setModel(IDTMFModel model) {
		_model = model;
		setupReciver();
	}

	public void setupReciver() {
		// reset myself from preferences
		setTonesEnabled(getTonesEnabled());
	}

	private final void setupUi() {
		setContentView(R.layout.main);

		_enableGroup = (RadioGroup) findViewById(R.id.RadioGroupEnable);
		_enableGroup.check(checkedButtonId(getTonesEnabled()));
		_enableGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {
				if (checkedId == R.id.RadioButtonTone) {
					setTonesEnabled(true);
				}
				setTonesEnabled(false);
			}
		});

		Button contactsButton = (Button) findViewById(R.id.button_contacts);
		contactsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_VIEW,
						Contacts.CONTENT_URI);
				startActivity(intent);
			}
		});
	}

	private int checkedButtonId(boolean tonesEnabled) {
		if (tonesEnabled) {
			return R.id.RadioButtonTone;
		}
		return R.id.RadioButtonPhone;
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
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(PREF_ENABLE_TONES, enableTones);
		editor.commit();

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