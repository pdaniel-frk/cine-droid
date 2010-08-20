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
import org.apache.http.message.BasicNameValuePair;
import org.cinedroid.data.CinemaLocation;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.CineworldAPIRequestTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kingamajick
 * 
 *         This implementation of {@link AsyncTaskWithCallback} is used to retrieve a list of cinemas from the Cineworld web api. The
 *         cinemas retrieved will be passed to the callback method.
 */
public class RetrieveCinemasTask extends CineworldAPIRequestTask<Void, CinemaLocation> {

	/**
	 * @param callback
	 * @param apiKey
	 */
	public RetrieveCinemasTask(final ActivityCallback callback, final String apiKey) {
		super(callback, apiKey);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#getMethod()
	 */
	@Override
	protected String getMethod() {
		return "cinemas";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#process(org.json.JSONArray, int)
	 */
	@Override
	protected CinemaLocation process(final JSONArray jsonArray, final int index) throws JSONException {
		JSONObject jsonObject = jsonArray.getJSONObject(index);
		CinemaLocation location = new CinemaLocation();

		location.setId(jsonObject.getInt("id"));
		location.setName(jsonObject.getString("name"));
		location.setUrl(jsonObject.getString("cinema_url"));
		location.setAddress(jsonObject.getString("address"));
		location.setPostcode(jsonObject.getString("postcode"));
		location.setPhone(jsonObject.getString("telephone"));

		return location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#addAdditionalParams(java.util.List)
	 */
	@Override
	protected void addAdditionalParams(final List<NameValuePair> params) {
		NameValuePair full = new BasicNameValuePair("full", "true");
		params.add(full);
	}

}