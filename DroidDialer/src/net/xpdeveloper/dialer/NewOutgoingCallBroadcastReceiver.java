package net.xpdeveloper.dialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * I intercept the NEW_OUTGOING_CALL Broadcast. Generate DTMF tones to the
 * speaker and stop the outgoing mobile call by nulling the phone number.
 * 
 * Register and Unregister me to enable/disable this behaviour. If I'm declared
 * in AndroidManifest.xml then I can't be controlled in this way.
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
		
		if(ToneDialService.invoke(context, originalDestination)) {
			cancelNetworkDial();	
		}
	}

	private void cancelNetworkDial() {
		setResultData(null);
	}
}
