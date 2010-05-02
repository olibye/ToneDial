package net.xpdeveloper.dialer;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * I show the help
 * @author byeo
 *
 */
public class ToneDialHelp extends Preference {

	public ToneDialHelp(Context context, AttributeSet attrs) {
		super(context, attrs);
        setWidgetLayoutResource(R.xml.preference_help);        
	}

}
