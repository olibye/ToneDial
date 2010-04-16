package com.xpdeveloper.dialer;

import android.media.ToneGenerator;

public interface IDTMFModel {

	/**
	 * 
	 * @param dialString the tones to dial (including " " and "-" for pauses
	 * @param toneGenerator passed in as we do not want to hold the resources for ever
	 * @throws InterruptedException because we Object.wait() for tones to play.
	 */
	public void dial(String dialString, ToneGenerator toneGenerator) throws InterruptedException;

}