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
package org.cinedroid.constants;

import android.content.SharedPreferences;

/**
 * Shared preference setting keys.
 * 
 * @author Kingamajick
 * 
 */
public interface Settings {

	/**
	 * Settings keys
	 */
	public static final String SETTING_TERRITORY = "territory";
	public static final String SETTING_MYCINEMA_NAME = "myCinemaName";
	public static final String SETTING_MYCINEMA_ID = "myCinemaID";
	public static final String SETTING_MYCINEMA_URL = "myCinemaURL";
	/**
	 * Key to use when retrieving values from {@link SharedPreferences}.
	 */
	public static final String SHARED_PREF_KEY = "org.cineworld";

}
