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
import org.cinedroid.data.impl.Film;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.tasks.impl.RetrieveCinemasTask;
import org.cinedroid.tasks.impl.RetrieveFilmsTask;
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

	public final static String CINEMA_ID = "cinema_id";
	public final static String TERRITORY = "territory";
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
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.CINEMA_ID, getIntent().getExtras().getInt(CINEMA_ID));
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.FILM_EDI, selectedFilm.getEdi());
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.FILM_TITLE, selectedFilm.getTitle());
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.POSTER_URL, selectedFilm.getPosterUrl());
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.FILM_URL, selectedFilm.getFilmUrl());
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.RATING, selectedFilm.getClassification());
		viewFilmsPerformancesIntent.putExtra(ListFilmPerformances.ADVISORY, selectedFilm.getAdvisory());

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

		this.filmAdapter = new FilmAdapter(this);
		setListAdapter(this.filmAdapter);

		NameValuePair key = new BasicNameValuePair(CineworldAPIAssistant.KEY, getString(R.string.cineworld_api_key));
		NameValuePair full = new BasicNameValuePair(CineworldAPIAssistant.FULL, "true");

		NameValuePair territory = new BasicNameValuePair(CineworldAPIAssistant.TERRITORY, "GB");

		RetrieveFilmsTask retrieveFilmsTask = new RetrieveFilmsTask(this, ActivityCallback.NO_REF, this);
		if (getIntent().hasExtra(CINEMA_ID)) {
			int cinemaID = getIntent().getExtras().getInt(CINEMA_ID);
			NameValuePair cinema = new BasicNameValuePair(CineworldAPIAssistant.CINEMA, Integer.toString(cinemaID));
			retrieveFilmsTask.execute(key, territory, full, cinema);
		}
		else {
			retrieveFilmsTask.execute(key, territory, full);
		}

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
			if (task instanceof RetrieveCinemasTask) {
				final AbstractCineworldTask cineworldTask = (AbstractCineworldTask) task;
				CineworldAPIAssistant.createCineworldAPIErrorDialog(this, cineworldTask);
			}
			else {
				throw new IllegalArgumentException(String.format("Unexpected type %s", task.getClass().getName()));
			}
		}

	}
}
