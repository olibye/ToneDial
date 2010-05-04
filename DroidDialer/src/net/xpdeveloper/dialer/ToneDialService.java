package net.xpdeveloper.dialer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.IBinder;
import android.util.Log;

/**
 * I am started by the Launcher Activity when tone dialing is enabled.
 * 
 * I register a NewOutgoingCallBroadcastReciver to noify me when a number is
 * dialed. I capture this interation and send a dial command to my model
 */
public class ToneDialService extends Service {
	public static final String ACTION_DIAL = "DIAL";
	private static final int TONE_DIAL_SERVICE_TICKER_ID = 1;

	private NewOutgoingCallBroadcastReceiver _receiver;
	private IToneDialModel _model;

	// TODO I think the tone generator should go in the model, with a release on
	// the model
	private ToneGenerator _toneGenerator;

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
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {
			// Have I been as
			if (shouldToneDial(intent)) {
				String originalDestination = intent.getDataString();
				toneDial(originalDestination);
			} else {
				// I've just been started
				displayNotification();
			}
		}

		return START_STICKY_COMPATIBILITY;
	}

	/**
	 * 
	 * @param originalDestination
	 * @return the number actually dialled
	 */
	public String toneDial(String originalDestination) {
		String dialString = adjustNumber(originalDestination);
		try {
			_model.dial(dialString, _toneGenerator);
		} catch (InterruptedException e) {
			Log.e(ToneDialActivity.TAG, "Unable to generate DTMF tones", e);
		}
		return dialString;
	}

	private String adjustNumber(String originalDestination) {
		String reply = originalDestination;
		
		if (originalDestination.startsWith("+")) {
			if (originalDestination.startsWith("+1")) {
				reply = originalDestination.substring(1);
			}
		}
		
		return reply;
	}

	private boolean shouldToneDial(Intent intent) {
		return ACTION_DIAL.equals(intent.getAction());
	}

	@Override
	public void onCreate() {
		super.onCreate();

		_toneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 80);

		manageRegistration(true);
	}

	@Override
	public void onDestroy() {
		manageRegistration(false);
		_toneGenerator.release();
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
	 */
	private void displayNotification() {
		int icon = R.drawable.stat_service;
		CharSequence tickerText = getText(R.string.ticker_tone_dial_on);

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
		CharSequence contentText = getText(R.string.notification_text);
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
