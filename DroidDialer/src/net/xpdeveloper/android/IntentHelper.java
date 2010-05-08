package net.xpdeveloper.android;

import android.content.Context;
import android.content.Intent;

/**
 * I am the business as usual implementation that simply delegates the method
 * back to my context.
 * 
 * I am normally replaced in unit tests to check intent expectations
 * 
 * @author byeo
 * 
 */
public class IntentHelper implements IIntentHelper {
	private Context _context;

	public IntentHelper(Context context) {
		_context = context;
	}

	@Override
	public void startService(Intent intent) {
		_context.startService(intent);
	}

	@Override
	public void stopService(Intent intent) {
		_context.stopService(intent);
	}

}
