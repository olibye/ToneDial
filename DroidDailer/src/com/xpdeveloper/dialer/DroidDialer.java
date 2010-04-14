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
package com.xpdeveloper.dialer;

import android.app.Activity;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import static android.media.ToneGenerator.*;

/**
 * 
 * @author byeo
 * 
 */
public class DroidDialer extends Activity implements View.OnClickListener {
	private static final int TONE_DURATION = 200;
	private static final int TONE_PAUSE = 50;
	private final ToneGenerator _toneGenerator = new ToneGenerator(
			AudioManager.STREAM_DTMF, 70);

	private EditText _numberEditText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button dialButton = (Button) findViewById(R.id.dialButton);
		dialButton.setOnClickListener(this);

		_numberEditText = (EditText) findViewById(R.id.numberEdit);
	}

	@Override
	public void onClick(View dialButton) {
		String number = _numberEditText.getText().toString(); 
		try {
			dial(number);
		} catch (InterruptedException e) {
			Log.e("DroidDialer", "Could not dial:"+number);
		}
	}

	public void dial(String dialString) throws InterruptedException {
		int digitCount = dialString.length();
		for (int digitIndex = 0; digitIndex < digitCount; digitIndex++) {
			char digit = dialString.charAt(digitIndex);
			dial(digit);
		}

	}

	private final int[] toneCodes = new int[] { TONE_DTMF_0, TONE_DTMF_1,
			TONE_DTMF_2, TONE_DTMF_3, TONE_DTMF_4, TONE_DTMF_5, TONE_DTMF_6,
			TONE_DTMF_7, TONE_DTMF_8, TONE_DTMF_9 };

	private synchronized void dial(char digit) throws InterruptedException {
		if (Character.isDigit(digit)) {
			// ignore non digits
			int numericValue = Character.getNumericValue(digit);
			_toneGenerator.startTone(toneCodes[numericValue],TONE_DURATION);
			this.wait(TONE_DURATION + TONE_PAUSE); // TODO should not wait on a UI thread?
		}
		
		if (Character.isSpace(digit)) {
			this.wait(TONE_DURATION + TONE_PAUSE);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}