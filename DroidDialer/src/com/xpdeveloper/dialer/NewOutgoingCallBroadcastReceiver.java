package com.xpdeveloper.dialer;

import java.net.URI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

	@Override
	public void onReceive(Context context, Intent intent) {
		// http://developer.android.com/reference/android/content/Intent.html#EXTRA_PHONE_NUMBER
		String originalDestination = intent
				.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		Log.i(DroidDialer.TAG, "Intercepted call to:" + originalDestination);

		Intent dialIntent = new Intent(context, DTMFModel.class);
		dialIntent.setAction(DTMFModel.ACTION_DIAL);
		dialIntent.setData(Uri.parse("tel:" + originalDestination));

		context.startService(dialIntent);

		Bundle extras = dialIntent.getExtras();
		if (extras != null) {
			if (extras.containsKey(DTMFModel.EXTRA_DTMF_DIALED)) {
				setResultData(null);
			}
		}
	}
}
