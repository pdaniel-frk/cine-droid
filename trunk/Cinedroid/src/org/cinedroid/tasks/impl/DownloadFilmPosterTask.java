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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.cinedroid.tasks.AsyncTaskWithCallback;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author Kingamajick
 * 
 */
public class DownloadFilmPosterTask extends AsyncTaskWithCallback<String, Void, Bitmap> {

	/**
	 * @param callback
	 */
	public DownloadFilmPosterTask(final org.cinedroid.tasks.AsyncTaskWithCallback.ActivityCallback callback) {
		super(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Bitmap doInBackground(final String... params) {
		if (params.length != 1) {
			throw new IllegalArgumentException("DownloadFilmPosterTask requires a single String param");
		}
		String urlStr = params[0];
		try {
			URL url = new URL(urlStr);
			return BitmapFactory.decodeStream(url.openStream());
		}
		catch (MalformedURLException e) {
			Log.e("org.cineworld", String.format("Film poster url %s malformed", urlStr), e);
			return null;
		}
		catch (IOException e) {
			Log.e("org.cineworld", String.format("Unable to download film poster from url %s", urlStr), e);
			return null;
		}

	}

}
