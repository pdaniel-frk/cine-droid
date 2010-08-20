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
package org.cinedroid.tasks;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.cinedroid.activities.ListFilmPerformances;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author Kingamajick
 * 
 *         This class is used to provide the common functionality when querying the Cineworld web api. As all results are of the form <br>
 *         { key : [ .... ] : }, this class extracts each of those objects and calls the implementing class's {@link #process(JSONObject)}.
 *         This results are then returned to the callback function as a list. The API key is automatically added to the request, but any
 *         additional params (cinema, territory etc) must be passed to {@link #doInBackground(NameValuePair...)}.
 * @param <Progress>
 * @param <Result>
 *            The Java type which represents each of the json objects returned in the array.
 */
public abstract class CineworldAPIRequestTask<Progress, Result> extends AsyncTaskWithCallback<NameValuePair, Progress, List<Result>> {

	private final static String TAG = ListFilmPerformances.class.getName();
	private final String apiKey;

	/**
	 * @param callback
	 * @param apiKey
	 */
	public CineworldAPIRequestTask(final ActivityCallback callback, final String apiKey) {
		super(callback);
		this.apiKey = apiKey;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<Result> doInBackground(final NameValuePair... params) {
		List<NameValuePair> requestParams = new ArrayList<NameValuePair>(params.length + 1);
		requestParams.addAll(Arrays.asList(params));
		requestParams.add(new BasicNameValuePair("key", this.apiKey));
		addAdditionalParams(requestParams);
		try {
			URI uri = URIUtils.createURI("http", "www.cineworld.co.uk/api/quickbook", -1, getMethod(),
					URLEncodedUtils.format(requestParams, "UTF-8"), null);
			Log.d(TAG, uri.toString());
			JSONArray results = sendRequest(new DefaultHttpClient(), uri);
			List<Result> processedResults = new ArrayList<Result>(results.length());
			try {
				for (int i = 0; i < results.length(); i++) {

					Result processedResult = process(results, i);
					if (processedResult != null) {
						processedResults.add(processedResult);
					}

				}
				return processedResults;
			}
			catch (JSONException e) {
				Log.e(TAG, "Unable to process response from Cineworld web api", e);
			}

		}
		catch (URISyntaxException e) {
			Log.e(TAG, "Unable to create Cineworld web api request URL", e);
		}
		return null;
	}

	/**
	 * @return the method to use on the Cineworld web api.
	 */
	protected abstract String getMethod();

	/**
	 * @return the key used in the returned JSONObject to look up the array of request results. By default this returns {@link #getMethod()}
	 *         .
	 */
	protected String getArrayKey() {
		return getMethod();
	}

	/**
	 * Implementations of this method should process a JSONObject to its Java class representation.
	 * 
	 * @param jsonObject
	 * @return
	 */
	protected abstract Result process(final JSONArray jsonArray, final int index) throws JSONException;

	/**
	 * Implementations can override this method to add additional params to the request. This should be used if the params are standard to
	 * all calls, varying params can be specified when the task is executed.
	 * 
	 * @param params
	 */
	protected void addAdditionalParams(final List<NameValuePair> params) {

	}

	protected JSONArray sendRequest(final HttpClient httpClient, final URI address) {
		HttpGet request = new HttpGet(address);
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() != HttpsURLConnection.HTTP_OK) {
				Log.e(TAG, String.format("None HTTP OK response from Cineworld web api %d:%s", response.getStatusLine().getStatusCode(),
						response.getStatusLine().getReasonPhrase()));
			}
			try {
				String responseBody = EntityUtils.toString(response.getEntity());
				Log.i(TAG, responseBody);
				try {
					JSONObject responseJSON = new JSONObject(responseBody);
					return responseJSON.getJSONArray(getArrayKey());
				}
				catch (JSONException e) {
					Log.e(TAG, String.format("Unable to process response from Cineworld web api : %s", responseBody), e);
				}

			}
			catch (IOException e) {
				Log.e(TAG, "Unable to process response from Cineworld web api", e);
			}

		}
		catch (ClientProtocolException e) {
			Log.e(TAG, "Unable to process request to Cineworld web api", e);
		}
		catch (IOException e) {
			Log.e(TAG, "Unable to process request to Cineworld web api", e);
		}

		return null;
	}
}