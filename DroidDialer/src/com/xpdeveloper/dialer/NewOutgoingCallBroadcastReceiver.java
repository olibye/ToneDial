package com.xpdeveloper.dialer;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Html.TagHandler;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

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

		// Disable the real outgoing call
		// TODO is this logged as an outgoing call?
		if (shouldGenerateTonesNotCall(context)) {
			setResultData(null);
		}

		Log.i(TAG, "Intercepted call to:" + originalDestination);
		try {
			DTMFModel.dial(originalDestination);
		} catch (InterruptedException e) {
			Log.e(TAG, "Unable to generate DTMF tones", e);
		}
	}

	/**
	 * Initially for fun, and to avoid user input I'll generate DTMF to speaker
	 * and not make a real call if I'm running on my side. Since the Dialtact
	 * app doesn't run in portrait mode this appears OK to get me going.
	 * 
	 * People will complain and I'll have to provide options.
	 * 
	 * Notes: Screen Orientation doesn't change in the dialer so we can't us
	 * that to tell
	 * 
	 * @return true to generate DTMF to the speaker and cancel the real call
	 */
	static private boolean shouldGenerateTonesNotCall(Context context) {

		// http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[],
		// float[], float[], float[])
		float gravity[] = new float[3];
		float geomagnetic[] = new float[3];
		boolean success = SensorManager.getRotationMatrix(null, null, gravity,
				geomagnetic);

		if (success) {
			Log.i(TAG, "x is:" + gravity[0]);
			Log.i(TAG, "y is:" + gravity[1]);
			Log.i(TAG, "z is:" + gravity[2]);

			// Oo lets see what we have here
			SensorManager manager = (SensorManager) context
					.getSystemService(Context.SENSOR_SERVICE);
			List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ALL);
			for (Sensor sensor : sensors) {
				Log.i(TAG, "Found sensor:" + sensor.getName());
			}
		}

		return false;
	}
}
