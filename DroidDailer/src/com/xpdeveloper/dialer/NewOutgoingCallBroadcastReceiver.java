package com.xpdeveloper.dialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	private final String TAG = "NewOutgoingCallBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {

		// http://developer.android.com/reference/android/content/Intent.html#EXTRA_PHONE_NUMBER
		Bundle extras = intent.getExtras();
		String originalDestination = (String) extras.get(Intent.EXTRA_PHONE_NUMBER);
		
		Log.i(TAG, "Intercepted call to:" + originalDestination);
		try {
			DTMFModel.dial(originalDestination);
		} catch (InterruptedException e) {
			Log.e(TAG,"Unable to generate DTMF tones",e);
		}
	}

}
