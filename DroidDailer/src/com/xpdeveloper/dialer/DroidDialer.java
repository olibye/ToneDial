package com.xpdeveloper.dialer;

import android.app.Activity;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DroidDialer extends Activity implements View.OnClickListener {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Button b = (Button) findViewById(R.id.dialButton);
		b.setOnClickListener(this);

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}