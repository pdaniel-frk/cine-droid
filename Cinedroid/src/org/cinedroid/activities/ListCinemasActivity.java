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
import org.cinedroid.adapters.CinemaLocationAdapter;
import org.cinedroid.data.impl.Cinema;
import org.cinedroid.tasks.AbstractCineworldTask;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.handler.ActivityCallback;
import org.cinedroid.tasks.impl.RetrieveCinemasTask;
import org.cinedroid.util.CineworldAPIAssistant;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Kingamajick
 * 
 */
public class ListCinemasActivity extends ListActivity implements ActivityCallback {

	public static final String SORT_TYPE = "sort_type";
	public static final int SORT_ALPHA = 0;
	public static final int SORT_NEAREST = 1;

	ArrayAdapter<Cinema> cinemaLocationAdapter;
	/**
	 * Application settings.
	 */
	private SharedPreferences settings;

	/**
	 * @param results
	 */
	public void onRetrieveCinemasTaskFinished(final List<Cinema> results) {
		this.cinemaLocationAdapter.clear();
		for (Cinema location : results) {
			this.cinemaLocationAdapter.add(location);
		}
		this.cinemaLocationAdapter.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		Cinema cinemaLocation = this.cinemaLocationAdapter.getItem(position);

		Intent viewFilmsIntent = new Intent(this, ListFilmsActivity.class);
		viewFilmsIntent.putExtra(ListFilmsActivity.CINEMA_ID, cinemaLocation.getId());

		this.startActivity(viewFilmsIntent);
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
		this.settings = getSharedPreferences("org.cineworld", MODE_PRIVATE);

		this.cinemaLocationAdapter = new CinemaLocationAdapter(this, R.layout.cinema_list_item, R.id.CinemaName);
		setListAdapter(this.cinemaLocationAdapter);

		this.getListView().setTextFilterEnabled(true);

		retrieveCinemas();

	}

	/**
	 * Retrieve the cinemas from the Cineworld API.
	 */
	public void retrieveCinemas() {
		NameValuePair territory = new BasicNameValuePair(CineworldAPIAssistant.TERRITORY, this.settings.getString("territory", "GB"));
		NameValuePair full = new BasicNameValuePair(CineworldAPIAssistant.FULL, "true");
		NameValuePair key = new BasicNameValuePair(CineworldAPIAssistant.KEY, getString(R.string.cineworld_api_key));
		new RetrieveCinemasTask(this, ActivityCallback.NO_REF, this).execute(territory, full, key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.territory, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		MenuItem gb = menu.findItem(R.id.GB);
		MenuItem ie = menu.findItem(R.id.IE);
		String current = this.settings.getString("territory", "GB");
		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(0.0f);
		ColorFilter filter = new ColorMatrixColorFilter(matrix);
		if (current.equals("GB")) {
			ie.getIcon().setColorFilter(filter);
			gb.getIcon().clearColorFilter();
		}
		else {
			gb.getIcon().setColorFilter(filter);
			ie.getIcon().clearColorFilter();
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		String current = this.settings.getString("territory", "GB");
		switch (item.getItemId()) {
		case R.id.GB:
			if (!current.equals("GB")) {
				SharedPreferences.Editor editor = this.settings.edit();
				editor.putString("territory", "GB");
				editor.commit();
				retrieveCinemas();
			}
			break;
		case R.id.IE:
			if (!current.equals("IE")) {
				SharedPreferences.Editor editor = this.settings.edit();
				editor.putString("territory", "IE");
				editor.commit();
				retrieveCinemas();
			}
			break;
		default:
			return super.onContextItemSelected(item);
		}

		return true;
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
			if (task instanceof RetrieveCinemasTask) {
				onRetrieveCinemasTaskFinished(((RetrieveCinemasTask) task).getResult());
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
