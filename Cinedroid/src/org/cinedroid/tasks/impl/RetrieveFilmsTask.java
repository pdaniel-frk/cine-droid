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
import org.cinedroid.data.Film;
import org.cinedroid.tasks.CineworldAPIRequestTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Kingamajick
 * 
 */
public class RetrieveFilmsTask extends CineworldAPIRequestTask<Void, Film> {

	/**
	 * The id of the cinema to filter by.
	 */
	public final static String CINEMA_PARAM_KEY = "cinema";

	/**
	 * @param callback
	 */
	public RetrieveFilmsTask(final ActivityCallback callback) {
		super(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#getMethod()
	 */
	@Override
	protected String getMethod() {
		return "films";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#process(org.json.JSONArray, int)
	 */
	@Override
	protected Film process(final JSONArray jsonArray, final int index) throws JSONException {
		JSONObject jsonObject = jsonArray.getJSONObject(index);
		Film film = new Film();
		film.setEdi(jsonObject.getInt("edi"));
		film.setTitle(jsonObject.getString("title"));
		if (!jsonObject.isNull("classification")) {
			film.setClassification(jsonObject.getString("classification"));
		}
		else {
			film.setClassification("??");
		}
		if (!jsonObject.isNull("advisory")) {
			film.setAdvisory(jsonObject.getString("advisory"));
		}
		film.setPosterUrl(jsonObject.getString("poster_url"));
		film.setStillUrl(jsonObject.getString("still_url"));
		film.setFilmUrl(jsonObject.getString("film_url"));

		return film;
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
