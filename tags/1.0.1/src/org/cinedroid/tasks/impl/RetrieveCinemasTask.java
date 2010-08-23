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

import java.util.List;

import org.apache.http.NameValuePair;
import org.cinedroid.data.impl.Cinema;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.util.CineworldAPIAssistant;
import org.cinedroid.util.CineworldAPIAssistant.API_METHOD;

import android.content.Context;

/**
 * Retrieves a list of cinemas from the Cineworld API.
 * 
 * @author Kingamajick
 */
public class RetrieveCinemasTask extends AbstractCineworldTask<Void, Cinema> {

	/**
	 * The territory these cinemas are for.
	 */
	private String territory = "GB";

	/**
	 * @param callback
	 * @param ref
	 * @param context
	 */
	public RetrieveCinemasTask(final ActivityCallback callback, final int ref, final Context context) {
		super(callback, ref, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#doInBackground(org.apache.http.NameValuePair[])
	 */
	@Override
	protected List<Cinema> doInBackground(final NameValuePair... params) {
		for (NameValuePair vp : params) {
			if (CineworldAPIAssistant.TERRITORY.equals(vp.getName())) {
				this.territory = vp.getValue();
				break;
			}
		}
		return super.doInBackground(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#onPostExecute(java.util.List)
	 */
	@Override
	protected void onPostExecute(final List<Cinema> result) {
		for (Cinema cinema : result) {
			cinema.setTerritory(this.territory);
		}
		super.onPostExecute(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getTaskDetails()
	 */
	@Override
	protected String getTaskDetails() {
		return "Retrieving cinemas, please wait...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getApiMethod()
	 */
	@Override
	protected API_METHOD getApiMethod() {
		return API_METHOD.CINEMAS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.ResurrectableTask#resurrect()
	 */
	@Override
	public void resurrect() {
		RetrieveCinemasTask zombie = new RetrieveCinemasTask(this.completionCallback, this.taskReference, this.context);
		zombie.execute(this.params);
	}

}