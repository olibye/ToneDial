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
package net.xpdeveloper.dialer.common.service;

import net.xpdeveloper.dialer.common.model.ToneDialModel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

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
		
		if(invoke(context, originalDestination)) {
			cancelNetworkDial();	
		}
	}

	private void cancelNetworkDial() {
		setResultData(null);
	}

	/**
	 * Helper method to send commands to this service. This allows the calling
	 * broadcast receiver to decide if it should disable mobile network dialing
	 * 
	 * @param context
	 * @param originalDestination
	 * @return true if the message intent to sent to the service
	 */
	public static boolean invoke(Context context, String originalDestination) {
		if (!ToneDialModel.isEmergencyNumer(originalDestination)) {
			Intent dialIntent = new Intent(context, ToneDialService.class);
			dialIntent.setAction(ToneDialService.ACTION_DIAL);
			dialIntent.setData(Uri.parse("tel:" + originalDestination));
	
			context.startService(dialIntent);
			return true;
		}
		return false;
	}
}
