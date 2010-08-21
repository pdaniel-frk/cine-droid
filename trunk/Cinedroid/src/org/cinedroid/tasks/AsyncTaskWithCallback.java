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

import org.cinedroid.tasks.handler.ActivityCallback;

import android.os.AsyncTask;

/**
 * @author Kingamajick
 * 
 */
public abstract class AsyncTaskWithCallback<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

	/**
	 * Code returned by {@link AsyncTaskWithCallback#getError()} if the task executed successfully.
	 */
	public final static int SUCCESS = -1;
	/**
	 * Callback for task completion.
	 */
	protected final ActivityCallback completionCallback;
	/**
	 * Tasks reference.
	 */
	protected final int taskReference;
	/**
	 * Should return a defined code for an error occurring in this task
	 */
	private int errorCode = -1;
	/**
	 * Result of the task if any;
	 */
	private Result result;

	/**
	 * Creates and instance of {@link AsyncTaskWithCallback} which on completion will call the
	 * {@link ActivityCallback#handleCallback(AsyncTaskWithCallback, int)} with the given reference. The reference must be <code>>= 0</code>
	 * or {@link ActivityCallback#NO_REF}.
	 * 
	 * @param callback
	 *            a non <code>null</code> implementor of {@link ActivityCallback}.
	 * @param ref
	 *            where <code>ref >= 0</code>
	 */
	public AsyncTaskWithCallback(final ActivityCallback callback, final int ref) {
		if (callback == null) {
			throw new IllegalArgumentException("callback cannot be null");
		}
		if (ref != ActivityCallback.NO_REF && ref < 0) {
			throw new IllegalArgumentException("ref must either be ActivityCallback.NO_REF or >= 0");
		}
		this.completionCallback = callback;
		this.taskReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(final Result result) {
		this.result = result;
		this.completionCallback.handleCallback(this, this.taskReference);
	};

	/**
	 * @return the reference this task was created with.
	 */
	public final int getRef() {
		return this.taskReference;
	}

	/**
	 * @return the result of the task, may be null if the task failed or has no result
	 */
	public final Result getResult() {
		return this.result;
	}

	/**
	 * This method should be called if an error has occurred during the execution of the task, which has cause the results (if any) to be
	 * invalid.
	 * 
	 * @param errorMessage
	 */
	protected final void setError(final int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * If an error occurred during the execution of this task, this method will return a user readable {@link String}, if no error has
	 * occurred, this method will return {@link #SUCCESS}
	 * 
	 * @return
	 */
	public final int getError() {
		return this.errorCode;
	}

}
