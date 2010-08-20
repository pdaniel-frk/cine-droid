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

import java.util.Iterator;
import java.util.List;

import org.cinedroid.data.FilmDate;
import org.cinedroid.data.FilmPerformance;
import org.cinedroid.tasks.CineworldAPIRequestTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.Time;
import android.util.Log;

/**
 * This class retrieves the performances for a given cinema, film and date.
 * 
 * @author Kingamajick
 * 
 */
public class RetrievePerformancesTask extends CineworldAPIRequestTask<Void, FilmPerformance> {
	/**
	 * The id of the cinema to filter by.
	 */
	public final static String CINEMA_PARAM_KEY = "cinema";
	/**
	 * The EDI of the film to filter by.
	 */
	public final static String FILM_PARAM_KEY = "film";
	/**
	 * The date of the performance to filter by.
	 */
	public final static String DATE_PARAM_KEY = "date";
	private FilmDate date;

	/**
	 * The {@link ActivityCallback} method should take two param's, the first been the {@link FilmDate} this
	 * {@link RetrievePerformancesTask} was created with and the second been a {@link List} or {@link FilmPerformance}s.
	 * 
	 * @param callback
	 */
	public RetrievePerformancesTask(final ActivityCallback callback, final String apiKey, final FilmDate date) {
		super(callback, apiKey);
		this.date = date;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#getMethod()
	 */
	@Override
	protected String getMethod() {
		return "performances";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.activities.tasks.CineworldAPIRequestTask#process(org.json.JSONArray, int)
	 */
	@Override
	protected FilmPerformance process(final JSONArray jsonArray, final int index) throws JSONException {
		JSONObject jsonObject = jsonArray.getJSONObject(index);
		FilmPerformance performance = new FilmPerformance();
		performance.setTime(jsonObject.getString("time"));
		performance.setAvailable(jsonObject.getBoolean("available"));
		performance.setType(jsonObject.getString("type"));
		performance.setBookingUrl(jsonObject.getString("booking_url"));

		return performance;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cineworld.tasks.AsyncTaskWithCallback#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(final List<FilmPerformance> result) {
		if (this.callback == null) {
			return;
		}
		this.date.setPerformances(result);
		filterPastDates(this.date);
		this.callback.invoke(this.date);

	}

	/**
	 * Removes any times which have passed from the list
	 * 
	 * @param results
	 */
	private void filterPastDates(final FilmDate filmDate) {
		Time currentTime = new Time();
		currentTime.setToNow();

		Time performanceDate = new Time();
		String date = filmDate.getDate();
		performanceDate.parse(date);

		// Check if the performance date is before the current time. This will only be true in the situation that the filmDate represents
		// the current day, as the cineworld api does not return dates which have passed. If the object is the current date, the film
		// performances need filtering.
		if (currentTime.after(performanceDate)) {
			for (Iterator<FilmPerformance> i = filmDate.getPerformances().iterator(); i.hasNext();) {
				String time = i.next().getTime().replace(":", ""); // Get the time with the : removed.
				performanceDate.parse(String.format("%sT%s00", date, time));
				Log.d("org.cineworld", String.format("Checking %s", performanceDate.format("%d %b %H:%M")));
				if (currentTime.after(performanceDate)) {
					Log.d("org.cineworld", String.format("Removed %s", performanceDate.format("%d %b %H:%M")));
					i.remove();
				}
			}
		}
		Log.d("org.cineworld", String.format("Current Time %s", currentTime.toString()));
	}
}
