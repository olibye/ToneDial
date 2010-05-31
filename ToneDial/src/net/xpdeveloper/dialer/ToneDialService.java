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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * I am started by the Launcher Activity when tone dialing is enabled.
 * 
 * I register a NewOutgoingCallBroadcastReciver to notify me when a number is
 * dialed. I capture this iteration and send a dial command to my model
 */
public class ToneDialService extends Service {
	public static final String ACTION_DIAL = "net.xpdeveloper.dialer.DIAL";
	public static final String ACTION_SERVICE_STATE_CHANGE = "net.xpdeveloper.dialer.SERVICE_STATE_CHANGE";

	private static final int TONE_DIAL_SERVICE_TICKER_ID = 1;

	private NewOutgoingCallBroadcastReceiver _receiver;
	private IToneDialModel _model;
	private boolean _stopped = false;

	private String _countryCode = "";
	private String _trunkCode = "";

	/**
	 * I'm needed by my unit tests
	 * 
	 * @param model
	 */
	public ToneDialService(IToneDialModel model) {
		setModel(model);
	}

	public ToneDialService() {
		this(new ToneDialModel());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int flags) {

		if (intent != null && _stopped == false) {
			String action = intent.getAction();
			if (ACTION_DIAL.equals(action)) {
				Uri data = Uri.parse(intent.getDataString());
				String originalDestination = data
						.getEncodedSchemeSpecificPart();
				toneDial(originalDestination);
				
				if (shouldDialOnlyOnce()) {
					_stopped = true;
					
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
					Editor editor = prefs.edit();
					editor.putBoolean(ToneDialActivity.PREF_ENABLE_TONES, false);
					editor.commit();
					 
					stopSelf();					
				}
			} else if (ToneDialActivity.ACTION_PREFERENCE_CHANGE.equals(action)) {
				saveCodes(intent);
			} else if (ACTION_SERVICE_STATE_CHANGE.equals(action)) {
				saveCodes(intent);
				displayNotification(getText(R.string.ticker_tone_dial_on),
						getText(R.string.notification_text));
			} else {
				// ignore it
				_stopped = true;
				stopSelf();
			}
		}
	}

	private boolean shouldDialOnlyOnce() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getBoolean(ToneDialActivity.PREF_ENABLE_TONES_ONCE, false);
	}

	private void saveCodes(Intent intent) {
		_countryCode = intent
				.getStringExtra(ToneDialActivity.EXTRA_COUNTRY_CODE);
		_trunkCode = intent.getStringExtra(ToneDialActivity.EXTRA_TRUNK_CODE);
	}

	/**
	 * 
	 * @param originalDestination
	 * @return the number actually dialled
	 */
	public String toneDial(String originalDestination) {
		String dialString = adjustNumber(originalDestination);
		try {
			displayNotification(
					getText(R.string.ticker_tone_dial) + dialString,
					getText(R.string.notification_text_tone_dial) + dialString);
			_model.dial(dialString);
		} catch (InterruptedException e) {
			Log.e(ToneDialActivity.TAG, "Unable to generate DTMF tones", e);
		}
		return dialString;
	}

	private String adjustNumber(String originalDestination) {
		String reply = originalDestination;

		if (originalDestination.startsWith(_countryCode)) {
			StringBuffer replyBuffer = new StringBuffer();
			replyBuffer.append(_trunkCode);
			replyBuffer.append(originalDestination.substring(_countryCode
					.length()));
			reply = replyBuffer.toString();
		}

		return reply;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		manageRegistration(true);
	}

	@Override
	public void onDestroy() {
		manageRegistration(false);
		_model.release();
		cancelNotification();

		super.onDestroy();
	}

	public void setModel(IToneDialModel model) {
		_model = model;
	}

	/**
	 * http://developer.android.com/reference/android/content/Intent.html#
	 * ACTION_NEW_OUTGOING_CALL Register/Unregister for
	 * android.intent.action.NEW_OUTGOING_CALL Category:
	 * android.intent.category.ALTERNATIVE
	 * 
	 * @param enableTones
	 */
	private void manageRegistration(boolean enableTones) {
		if (enableTones) {
			if (_receiver == null) {
				_receiver = new NewOutgoingCallBroadcastReceiver();
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

	/**
	 * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
	 * 
	 * @param contentText
	 *            TODO
	 * @param contentText
	 *            TODO
	 */
	private void displayNotification(CharSequence tickerText,
			CharSequence contentText) {
		int icon = R.drawable.stat_service;

		Notification notification = new Notification(icon, tickerText, 0);
		// We will cancel this notification
		notification.flags |= Notification.FLAG_ONGOING_EVENT
				| Notification.FLAG_NO_CLEAR;

		// Setup the pending intent to launch the UI
		Intent launchToneDialActivity = new Intent(this, ToneDialActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				launchToneDialActivity, 0);

		// Build the notifications
		Context context = getApplicationContext();
		CharSequence contentTitle = getText(R.string.notification_title);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// Raise the notification
		NotificationManager notificationManager = notificationManager();
		notificationManager.notify(TONE_DIAL_SERVICE_TICKER_ID, notification);
	}

	private NotificationManager notificationManager() {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
		return notificationManager;
	}

	private void cancelNotification() {
		notificationManager().cancelAll();
	}
}
