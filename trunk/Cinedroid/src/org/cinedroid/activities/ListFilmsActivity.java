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
import org.cinedroid.data.Film;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.AsyncTaskWithCallback.ActivityCallback;
import org.cinedroid.tasks.impl.RetrieveFilmsTask;

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
public class ListFilmsActivity extends ListActivity {

	public final static String CINEMA_ID = "cinema_id";

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

		ActivityCallback retrieveFilmTaskCallback = AsyncTaskWithCallback.createCallback(this, "onRetrieveFilmTaskFinished", List.class);
		RetrieveFilmsTask retrieveFilmsTask = new RetrieveFilmsTask(retrieveFilmTaskCallback, getString(R.string.cineworld_api_key));
		if (getIntent().hasExtra(CINEMA_ID)) {
			NameValuePair cinemaId = new BasicNameValuePair(RetrieveFilmsTask.CINEMA_PARAM_KEY, Integer.toString(getIntent().getExtras()
					.getInt(CINEMA_ID)));
			retrieveFilmsTask.execute(cinemaId);
		}
		else {
			retrieveFilmsTask.execute();
		}

		this.progressDialog = ProgressDialog.show(this, "", "Retrieving films, please wait...", false);
	}
}
