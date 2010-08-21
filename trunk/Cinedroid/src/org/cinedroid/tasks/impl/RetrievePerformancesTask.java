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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.cinedroid.data.impl.FilmDate;
import org.cinedroid.data.impl.FilmPerformance;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.util.CineworldAPIAssistant;
import org.cinedroid.util.CineworldAPIAssistant.API_METHOD;
import org.cinedroid.util.CineworldAPIAssistant.CineworldAPIException;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

/**
 * This class retrieves the performances for dates supplied via the constuctor.
 * 
 * @author Kingamajick
 * 
 */
public class RetrievePerformancesTask extends AbstractCineworldTask<Void, FilmDate> {
	private final static String TAG = "cinedroid:" + RetrievePerformancesTask.class.getSimpleName();
	public final static int UNABLE_TO_CONTACT_CINEWORLD_API = 0;
	public final static int UNABLE_TO_PROCESS_RESPONSE = 1;

	/**
	 * Film dates to find performances for.
	 */
	protected final FilmDate[] dates;

	/**
	 * The {@link ActivityCallback} method should take two param's, the first been the {@link FilmDate} this
	 * {@link RetrievePerformancesTask} was created with and the second been a {@link List} or {@link FilmPerformance}s.
	 * 
	 * @param callback
	 * @param ref
	 * @param context
	 */
	public RetrievePerformancesTask(final ActivityCallback callback, final int ref, final Context context, final FilmDate... dates) {
		super(callback, ref, context);
		this.dates = dates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#doInBackground(org.apache.http.NameValuePair[])
	 */
	@Override
	protected List<FilmDate> doInBackground(final NameValuePair... params) {
		CineworldAPIAssistant assistance = new CineworldAPIAssistant();
		// Add extra item at end of params for dates
		NameValuePair[] paramsWithDate = new NameValuePair[params.length + 1];
		for (int i = 0; i < params.length; i++) {
			paramsWithDate[i] = params[i];
		}
		for (FilmDate date : this.dates) {
			NameValuePair dateNVP = new BasicNameValuePair(CineworldAPIAssistant.DATE, date.getDate());
			paramsWithDate[params.length] = dateNVP;
			String response;
			try {
				response = assistance.sendRequest(new DefaultHttpClient(), API_METHOD.PERFORMANCES, paramsWithDate);
				List<FilmPerformance> performances = assistance.process(response, API_METHOD.PERFORMANCES);
				date.setPerformances(performances);
				filterPastDates(date);
			}
			catch (CineworldAPIException e) {
				setError(UNABLE_TO_QUERY_CINEWORLD_API);
				Log.e(TAG, e.getMessage(), e);
				return null;
			}
		}
		return Arrays.asList(this.dates);

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
				Log.d(TAG, String.format("Checking %s", performanceDate.format("%d %b %H:%M")));
				if (currentTime.after(performanceDate)) {
					Log.d(TAG, String.format("Removed %s", performanceDate.format("%d %b %H:%M")));
					i.remove();
				}
			}
		}
		Log.d(TAG, String.format("Current Time %s", currentTime.toString()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getTaskDetails()
	 */
	@Override
	protected String getTaskDetails() {
		return "Retrieving film performance times, please wait...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getApiMethod()
	 */
	@Override
	protected API_METHOD getApiMethod() {
		// Blank implementation as performTask has been overridden.
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.ResurrectableTask#resurrect()
	 */
	@Override
	public void resurrect() {
		RetrievePerformancesTask zombie = new RetrievePerformancesTask(this.completionCallback, this.taskReference, this.context,
				this.dates);
		zombie.execute(this.params);

	}
}
