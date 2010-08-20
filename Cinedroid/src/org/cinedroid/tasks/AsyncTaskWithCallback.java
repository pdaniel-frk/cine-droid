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

import java.lang.reflect.Method;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Kingamajick
 * 
 */
public abstract class AsyncTaskWithCallback<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
	private final static String TAG = AsyncTaskWithCallback.class.getName();
	protected final ActivityCallback callback;

	/**
	 * Helper call to store a references to an {@link Activity} and its callback {@link Method}.
	 */
	public final static class ActivityCallback {
		public final Activity receiver;
		public final Method callback;

		public ActivityCallback(final Activity receiver, final Method callback) {
			this.receiver = receiver;
			this.callback = callback;
		}

		/**
		 * Attempts to invoke the callback method with the given args. If this is not possible the error is logged.
		 * 
		 * @param args
		 */
		public void invoke(final Object... args) {
			try {
				this.callback.invoke(this.receiver, args);

			}
			catch (Exception e) {
				Log.e(TAG, String.format("Callback %s failed on %s", this.callback.getName(), this.receiver.getClass().getName()), e);
			}
		}
	}

	/**
	 * Utility method to retrieve a reference to the callback method on a {@link Activity} which will act as the receiver. If a callback
	 * can't be created, this method will return null and log an error message.
	 * 
	 * @param reciever
	 * @param methodName
	 * @param paramtypes
	 * @return
	 */
	public static ActivityCallback createCallback(final Activity reciever, final String methodName, final Class<?>... paramtypes) {
		try {
			ActivityCallback callback = new ActivityCallback(reciever, reciever.getClass().getMethod(methodName, paramtypes));
			return callback;
		}
		catch (Exception e) {
			Log.e(TAG, String.format("Unable to create callback function %s#%s", reciever.getClass().getName(), methodName), e);
		}
		return null;
	}

	/**
	 * Creates a {@link AsyncTask} which will call the {@link Method} callback on a given {@link Activity} in its
	 * {@link #onPostExecute(Object)} method. The callback method should receive a single argument of the Result type.
	 * 
	 * @param activity
	 * @param callback
	 */
	public AsyncTaskWithCallback(final ActivityCallback callback) {
		this.callback = callback;
	}

	@Override
	protected void onPostExecute(final Result result) {
		if (this.callback == null) {
			return;
		}
		this.callback.invoke(result);
	}
}
