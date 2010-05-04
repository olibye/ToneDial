package net.xpdeveloper.dialer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NumberTweaker {
	private Map<String, String> _swaps = new HashMap<String, String>(); 
	
	/**
	 * Add a swap 
	 * @param prefix what to search for
	 * @param newPrefix what to swap it for
	 */
	public void addSwap(String prefix, String newPrefix) {
		_swaps.put(prefix, newPrefix);
	}

	public String tweak(String numberString) {
		String reply = numberString;
		Set<String> keys = _swaps.keySet();
		for (String key : keys) {
			if (numberString.startsWith(key)) {
				return numberString.replace(key, _swaps.get(key));
			}
		}
		return reply;
	}

}
