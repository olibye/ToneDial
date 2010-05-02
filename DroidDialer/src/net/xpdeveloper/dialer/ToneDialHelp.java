package net.xpdeveloper.dialer;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * I show the help
 * 
 * @author byeo
 * 
 */
public class ToneDialHelp extends DialogPreference {

	public ToneDialHelp(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWidgetLayoutResource(R.xml.preference_help);
	}

}
