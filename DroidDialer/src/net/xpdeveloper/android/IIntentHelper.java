package net.xpdeveloper.android;

import android.content.Intent;

/**
 * This interface makes it possible to test using mocks that expect intents are
 * raised
 * 
 * @author byeo
 * 
 */
public interface IIntentHelper {
	public void startService(Intent intent);
}
