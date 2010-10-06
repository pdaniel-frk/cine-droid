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

import org.cinedroid.data.impl.Film;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.util.CineworldAPIAssistant.API_METHOD;

import android.content.Context;

/**
 * @author Kingamajick
 * 
 */
public class RetrieveFilmsTask extends AbstractCineworldTask<Void, Film> {

	/**
	 * @param callback
	 * @param ref
	 * @param context
	 */
	public RetrieveFilmsTask(final ActivityCallback callback, final int ref, final Context context) {
		super(callback, ref, context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getTaskDetails()
	 */
	@Override
	protected String getTaskDetails() {
		return "Retrieving films, please wait...";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.AbstractCineworldTask#getApiMethod()
	 */
	@Override
	protected API_METHOD getApiMethod() {
		return API_METHOD.FILMS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.ResurrectableTask#resurrect()
	 */
	@Override
	public void resurrect() {
		RetrieveFilmsTask zombie = new RetrieveFilmsTask(this.completionCallback, this.taskReference, this.context);
		zombie.execute(this.params);
	}
}