package net.xpdeveloper.dialer;


public interface IToneDialModel {

	/**
	 * 
	 * @param dialString
	 *            the tones to dial (including " " and "-" for pauses
	 * @throws InterruptedException
	 *             because we Object.wait() for tones to play.
	 */
	public void dial(String dialString)
			throws InterruptedException;

	/**
	 * Free up OS resources, e.g. the internal ToneGenerator that my
	 * implementation is highly likely to contain
	 */
	public void release();
}