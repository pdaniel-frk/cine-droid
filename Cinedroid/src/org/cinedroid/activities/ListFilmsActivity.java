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
package org.cinedroid.activities;

import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.cinedroid.R;
import org.cinedroid.adapters.FilmAdapter;
import org.cinedroid.constants.Extras;
import org.cinedroid.data.impl.Film;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.tasks.impl.RetrieveFilmsTask;
import org.cinedroid.util.ActivityUtils;
import org.cinedroid.util.CineworldAPIAssistant;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * @author Kingamajick
 * 
 */
public class ListFilmsActivity extends ListActivity implements ActivityCallback {

	private FilmAdapter filmAdapter;
	private ProgressDialog progressDialog;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);

		Intent viewFilmsPerformancesIntent = new Intent(this, ListFilmPerformances.class);
		Film selectedFilm = this.filmAdapter.getItem(position);
		Bundle extras = getIntent().getExtras();
		viewFilmsPerformancesIntent.putExtra(Extras.CINEMA_ID, extras.getInt(Extras.CINEMA_ID));
		viewFilmsPerformancesIntent.putExtra(Extras.TERRITORY, extras.getString(Extras.TERRITORY));
		viewFilmsPerformancesIntent.putExtra(Extras.FILM_EDI, selectedFilm.getEdi());
		viewFilmsPerformancesIntent.putExtra(Extras.FILM_TITLE, selectedFilm.getTitle());
		viewFilmsPerformancesIntent.putExtra(Extras.POSTER_URL, selectedFilm.getPosterUrl());
		viewFilmsPerformancesIntent.putExtra(Extras.FILM_URL, selectedFilm.getFilmUrl());
		viewFilmsPerformancesIntent.putExtra(Extras.RATING, selectedFilm.getClassification());
		viewFilmsPerformancesIntent.putExtra(Extras.ADVISORY, selectedFilm.getAdvisory());
		this.startActivity(viewFilmsPerformancesIntent);
	}

	public void onRetrieveFilmTaskFinished(final List<Film> films) {
		this.filmAdapter.addItems(films);
		this.progressDialog.cancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cinema_list);

		ActivityUtils.checkExtras(getIntent(), Extras.CINEMA_ID, Extras.TERRITORY);

		this.filmAdapter = new FilmAdapter(this);
		setListAdapter(this.filmAdapter);
		getListView().setTextFilterEnabled(true);

		NameValuePair key = new BasicNameValuePair(CineworldAPIAssistant.KEY, getString(R.string.cineworld_api_key));
		NameValuePair full = new BasicNameValuePair(CineworldAPIAssistant.FULL, "true");

		RetrieveFilmsTask retrieveFilmsTask = new RetrieveFilmsTask(this, ActivityCallback.NO_REF, this);

		Bundle extras = getIntent().getExtras();
		NameValuePair cinema = new BasicNameValuePair(CineworldAPIAssistant.CINEMA, Integer.toString(extras.getInt(Extras.CINEMA_ID)));
		NameValuePair territory = new BasicNameValuePair(CineworldAPIAssistant.TERRITORY, extras.getString(Extras.TERRITORY));

		retrieveFilmsTask.execute(key, territory, full, cinema);

		this.progressDialog = ProgressDialog.show(this, "", "Retrieving films, please wait...", false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.tasks.handler.ActivityCallback#handleCallback(org.cinedroid.tasks.AsyncTaskWithCallback, int)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void handleCallback(final AsyncTaskWithCallback task, final int ref) {
		if (task.getError() == AsyncTaskWithCallback.SUCCESS) {
			if (task instanceof RetrieveFilmsTask) {
				onRetrieveFilmTaskFinished(((RetrieveFilmsTask) task).getResult());
			}
		}
		else {
			if (task instanceof AbstractCineworldTask) {
				final AbstractCineworldTask cineworldTask = (AbstractCineworldTask) task;
				CineworldAPIAssistant.createCineworldAPIErrorDialog(this, cineworldTask);
			}
			else {
				throw new IllegalArgumentException(String.format("Unexpected type %s", task.getClass().getName()));
			}
		}

	}
}
