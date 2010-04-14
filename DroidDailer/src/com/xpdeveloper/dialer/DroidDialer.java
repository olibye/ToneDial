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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author byeo
 * 
 */
public class DroidDialer extends Activity implements View.OnClickListener {
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
			DTMFModel.dial(number);
		} catch (InterruptedException e) {
			Log.e("DroidDialer", "Could not dial:"+number);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}