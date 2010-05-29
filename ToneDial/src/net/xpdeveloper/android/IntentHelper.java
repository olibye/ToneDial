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
