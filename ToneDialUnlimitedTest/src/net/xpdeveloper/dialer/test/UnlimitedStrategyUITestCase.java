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
package net.xpdeveloper.dialer.test;

import com.jayway.android.robotium.solo.Solo;

import net.xpdeveloper.dialer.unlimited.ToneDialUnlimited;
import android.test.ActivityInstrumentationTestCase2;

public class UnlimitedStrategyUITestCase extends
		ActivityInstrumentationTestCase2<ToneDialUnlimited> {

	private Solo _solo;

	public UnlimitedStrategyUITestCase() {
		super(ToneDialUnlimited.class.getPackage().getName(),
				ToneDialUnlimited.class);
	}

	@Override
	public void setUp() throws Exception {
		_solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {

		try {
			_solo.finalize();
		} catch (Throwable e) {

			e.printStackTrace();
		}
		getActivity().finish();
				
		super.tearDown();
	}

	public void testDialOnceDisabled() {
		assertTrue("Expecting the Tone Dial Page", _solo
				.searchText("Tone Dial Unlimited"));

	}
}
