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

import net.xpdeveloper.dialer.IToneGeneratorStrategy;
import net.xpdeveloper.dialer.ToneDialModel;

import org.jmock.Expectations;
import org.jmock.Mockery;

import android.media.ToneGenerator;

import junit.framework.TestCase;

/**
 * 
 * @author byeo
 * 
 */
public class ToneDialModelUnitTestCase extends TestCase {
	private Mockery _mockery = new Mockery();

	public void testDTMFCodes() throws InterruptedException {
		final IToneGeneratorStrategy mockStrategy = _mockery
				.mock(IToneGeneratorStrategy.class);
		
		_mockery.checking(new Expectations() {
			{
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_0, ToneDialModel.TONE_PAUSE * 3);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_1, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_2, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_3, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_4, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_5, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_6, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_7, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_8, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_9, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_P, ToneDialModel.TONE_PAUSE);
				one(mockStrategy).generateTone(ToneGenerator.TONE_DTMF_S, ToneDialModel.TONE_PAUSE);
			}
		});

		ToneDialModel unit = new ToneDialModel(mockStrategy);
		unit.dial("0123456789#*- ");
		_mockery.assertIsSatisfied();
	}
}
