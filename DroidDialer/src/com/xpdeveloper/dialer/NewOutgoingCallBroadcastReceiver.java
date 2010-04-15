package com.xpdeveloper.dialer;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * I intercept the NEW_OUTGOING_CALL Broadcast. I decide if I'm going to
 * generate DTMF tones to the speaker and stop the outgoing mobile call by
 * nulling the phone number.
 * 
 * see http://developer.android.com/reference/android/content/Intent.html#
 * ACTION_NEW_OUTGOING_CALL
 * 
 * @author byeo
 * 
 */
public class NewOutgoingCallBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "NewOutgoingCallBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// http://developer.android.com/reference/android/content/Intent.html#EXTRA_PHONE_NUMBER
		String originalDestination = intent
				.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Log.i(TAG, "Intercepted call to:" + originalDestination);

		// Disable the real outgoing call
		// TODO is this logged as an outgoing call?
		if (shouldGenerateTonesNotCall(context)) {
			setResultData(null);

			try {
				DTMFModel.dial(originalDestination);
			} catch (InterruptedException e) {
				Log.e(TAG, "Unable to generate DTMF tones", e);
			}
		}

	}

	static private boolean shouldGenerateTonesNotCall(Context context) {
		return DroidDialer.isRunning();
	}
}
