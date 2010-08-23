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
package org.cinedroid.tasks.handler;

import org.cinedroid.tasks.AsyncTaskWithCallback;

import android.app.Activity;

/**
 * This classes purpose is to allow a {@link AsyncTaskWithCallback} to callback to an {@link Activity} implementing this interface with an
 * instance of its self when the task completes.
 * 
 * @author Kingamajick
 */
public interface ActivityCallback {

	/**
	 * If an instance of {@link AsyncTaskWithCallback} has been created with no ref argument, when the owning implemention of
	 * {@link ActivityCallback} is called, this will be the ref argument.
	 */
	public static int NO_REF = -1;

	/**
	 * This method should be implemented to handle a callback from a {@link AsyncTaskWithCallback}, this method will be called from the UI
	 * thread once the task completes, from its implementation of onPostExecute).
	 * 
	 * @param ref
	 * @param task
	 */
	@SuppressWarnings("rawtypes")
	public void handleCallback(final AsyncTaskWithCallback task, final int ref);
}
