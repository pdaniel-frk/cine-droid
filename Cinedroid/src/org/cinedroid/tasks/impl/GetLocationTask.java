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
package org.cinedroid.tasks.impl;

import org.cinedroid.tasks.AsyncTaskWithCallback;

import android.location.Location;
import android.location.LocationManager;

/**
 * @author Kingamajick
 * 
 */
public class GetLocationTask extends AsyncTaskWithCallback<LocationManager, Void, Location> {

	/**
	 * @param callback
	 */
	public GetLocationTask(final ActivityCallback callback) {
		super(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Location doInBackground(final LocationManager... params) {
		Location l = new Location("");
		l.setLatitude(53.381662);
		l.setLongitude(-1.500465);

		return l;
	}
}
