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

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cinedroid.data.CineworldData;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.util.CineworldAPIAssistant;
import org.cinedroid.util.CineworldAPIAssistant.API_METHOD;
import org.cinedroid.util.CineworldAPIAssistant.CineworldAPIException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

/**
 * @author Kingamajick
 * 
 */
public abstract class AbstractCineworldTask<Progress, T extends CineworldData> extends
		AsyncTaskWithCallback<NameValuePair, Progress, List<T>> implements ResurrectableTask {
	private final static String TAG = "cinedroid:" + AbstractCineworldTask.class.getSimpleName();
	/**
	 * Error codes.
	 */
	public final static int UNABLE_TO_QUERY_CINEWORLD_API = 0;
	/**
	 * Context to use to show task dialog.
	 */
	protected final Context context;
	/**
	 * Params execute was called with.
	 */
	protected NameValuePair[] params;

	/**
	 * Dialog instance displayed to the user.
	 */
	private Dialog dialog;

	/**
	 * @param callback
	 * @param ref
	 */
	public AbstractCineworldTask(final ActivityCallback callback, final int ref, final Context context) {
		super(callback, ref);
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		this.dialog = ProgressDialog.show(this.context, "", getTaskDetails(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected List<T> doInBackground(final NameValuePair... params) {
		this.params = params;
		CineworldAPIAssistant assistance = new CineworldAPIAssistant();
		String response;
		try {
			response = assistance.sendRequest(new DefaultHttpClient(), getApiMethod(), params);
			return assistance.process(response, getApiMethod());
		}
		catch (CineworldAPIException e) {
			setError(UNABLE_TO_QUERY_CINEWORLD_API);
			Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(final List<T> result) {
		this.dialog.cancel();
		super.onPostExecute(result);
	}

	/**
	 * This method should be implemented to return the details of the task to display to the user while the task is ongoing.
	 * 
	 * @return
	 */
	protected abstract String getTaskDetails();

	/**
	 * During the processing in {@link #performTask(NameValuePair...)}, this type will be used to determine the query type, array key, and
	 * data type.
	 * 
	 * @return
	 */
	protected abstract API_METHOD getApiMethod();
}
