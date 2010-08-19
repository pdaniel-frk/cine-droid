/**
 * Copyright 2010 R King
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cinedroid.activities;

import android.content.Intent;

/**
 * @author Kingamajick
 * 
 */
public class ActivityUtils {

	private final static String ERR_EXTRA_NOT_FOUND = "Extra '%s' not found";

	/**
	 * Checks a given intent has the specified extras and throw an {@link IllegalArgumentException} if not.
	 * 
	 * @param intent
	 * @param extras
	 */
	final static void checkExtras(final Intent intent, final String... extras) {
		for (String extra : extras) {
			if (!intent.hasExtra(extra)) {
				throw new IllegalArgumentException(String.format(ActivityUtils.ERR_EXTRA_NOT_FOUND, extra));
			}
		}
	}

}
