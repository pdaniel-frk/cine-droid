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
package org.cinedroid.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;
import org.cinedroid.data.CineworldData;
import org.cinedroid.data.impl.Cinema;
import org.cinedroid.data.impl.Film;
import org.cinedroid.data.impl.FilmDate;
import org.cinedroid.data.impl.FilmPerformance;
import org.cinedroid.tasks.ResurrectableTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * This class provides utility methods for retrieving results from the <a href="http://www.cineworld.co.uk/developer">Cineworld API</a>.
 * 
 * @author Kingamajick
 * 
 */
public class CineworldAPIAssistant {

	/**
	 * Cineworld API key for 'territory'.
	 */
	public static final String TERRITORY = "territory";
	/**
	 * Cineworld API key for 'full'.
	 */
	public static final String FULL = "full";
	/**
	 * Cineworld API key for 'cinema'.
	 */
	public static final String CINEMA = "cinema";
	/**
	 * Cineworld API key for 'film'.
	 */
	public static final String FILM = "film";
	/**
	 * Cineworld API key for 'date'.
	 */
	public static final String DATE = "date";
	/**
	 * Cineworld API key for 'key'.
	 */
	public static final String KEY = "key";
	private static final String CINEWORLD_API_URL = "www.cineworld.co.uk/api/quickbook";

	/**
	 * Wrapper {@link Exception} for all Exceptions occurring while attempting to interact with the CIneworld API>
	 * 
	 * @author Kingamajick
	 * 
	 */
	@SuppressWarnings("serial")
	public static class CineworldAPIException extends Exception {

		public CineworldAPIException(final String detailMessage) {
			super(detailMessage);
		}

		public CineworldAPIException(final String detailMessage, final Throwable throwable) {
			super(detailMessage, throwable);
		}

	}

	/**
	 * {@link Enum} representing available api methods;
	 * 
	 * @author Kingamajick
	 * 
	 */
	public enum API_METHOD {
		CINEMAS("cinemas", Cinema.class), FILMS("films", Film.class), DATES("dates", FilmDate.class), PERFORMANCES("performances",
				FilmPerformance.class);
		private final String method;
		private final Class<? extends CineworldData> type;

		private API_METHOD(final String method, final Class<? extends CineworldData> type) {
			this.method = method;
			this.type = type;
		}

		/**
		 * @return the method to append to the Cineworld API URL.
		 */
		final String getMethod() {
			return this.method;
		}

		/**
		 * @return the key used to extract the results from the Cineworld response.
		 */
		final String getResultKey() {
			return this.method;
		}

		final Class<? extends CineworldData> getType() {
			return this.type;
		}
	}

	/**
	 * This method processes a response from the Cineworld API, and transforms it into a {@link List} of the given type.
	 * 
	 * @param cineworldResponse
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends CineworldData> List<T> process(final String cineworldResponse, final API_METHOD method) throws CineworldAPIException {
		JSONObject json;
		try {
			json = new JSONObject(cineworldResponse);
		}
		catch (JSONException e) {
			throw new CineworldAPIException("Unable to create JSON object from response", e);
		}
		JSONArray dataArray;
		try {
			dataArray = json.getJSONArray(method.getResultKey());
		}
		catch (JSONException e) {
			throw new CineworldAPIException("Unable to get results array from response", e);
		}
		List<T> asObjects = new ArrayList<T>(dataArray.length());
		for (int i = 0; i < dataArray.length(); i++) {
			T dataObject;
			try {
				dataObject = (T) method.getType().newInstance();
				asObjects.add(dataObject);
			}
			catch (IllegalAccessException e) {
				throw new CineworldAPIException("Unable to instatiate instance of CineworldData class", e);
			}
			catch (InstantiationException e) {
				throw new CineworldAPIException("Unable to instatiate instance of CineworldData class", e);
			}
			try {
				dataObject.fromJSON(dataArray.getString(i));
			}
			catch (JSONException e) {
				throw new CineworldAPIException("Unable to process json result item", e);
			}
		}

		return asObjects;

	}

	/**
	 * Sends a request tot he Cineworld API using a given {@link HttpClient}.
	 * 
	 * @param client
	 * @param method
	 *            the method to execute on the Cineworld server.
	 * @param params
	 *            params to execute the method with, must include a valid key. For other restrictions see the <a
	 *            href="http://www.cineworld.co.uk/developer/api">API documentation</a>
	 * @return the text returned by the server.
	 * @throws CineworldAPIException
	 *             if the request failed for any reason.
	 */
	public String sendRequest(final HttpClient client, final API_METHOD method, final NameValuePair... params) throws CineworldAPIException {
		URI uri;
		try {
			uri = URIUtils.createURI("http", CINEWORLD_API_URL, -1, method.getMethod(),
					URLEncodedUtils.format(Arrays.asList(params), "UTF-8"), null);
		}
		catch (URISyntaxException e) {
			throw new CineworldAPIException("Unable to create API request, invalid URL", e);
		}
		HttpGet request = new HttpGet(uri);

		HttpResponse response;
		try {
			response = client.execute(request);
		}
		catch (ClientProtocolException e) {
			throw new CineworldAPIException("Unable to execute API request", e);
		}
		catch (IOException e) {
			throw new CineworldAPIException("Unable to execute API request", e);
		}
		if (response.getStatusLine().getStatusCode() != HttpsURLConnection.HTTP_OK) {
			throw new CineworldAPIException(String.format("Unable to execute API request, Code:%d", response.getStatusLine()
					.getStatusCode()));
		}

		String responseBody;
		try {
			responseBody = EntityUtils.toString(response.getEntity());
		}
		catch (ParseException e) {
			throw new CineworldAPIException("Unable to parse API response", e);
		}
		catch (IOException e) {
			throw new CineworldAPIException("Unable to read API response", e);
		}

		return responseBody;
	}

	/**
	 * Create a dialog with the text "Problem connecting to the Cineworld API", with two buttons, "Retry", which will re run the task, and
	 * "Back" which will close the owningActivity.
	 * 
	 * @param owningActivity
	 * @param task
	 */
	public static void createCineworldAPIErrorDialog(final Activity owningActivity, final ResurrectableTask task) {
		AlertDialog.Builder builder = new AlertDialog.Builder(owningActivity);
		builder.setMessage("Problem connecting to the Cineworld API");
		builder.setCancelable(false);
		builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				task.resurrect();
			}
		});
		builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				owningActivity.finish();
	
			}
		});
		builder.create().show();
	}

}
