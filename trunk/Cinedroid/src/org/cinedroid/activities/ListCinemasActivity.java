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

import org.cinedroid.R;
import org.cinedroid.adapters.CinemaLocationAdapter;
import org.cinedroid.data.CinemaLocation;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.AsyncTaskWithCallback.ActivityCallback;
import org.cinedroid.tasks.impl.RetrieveCinemasTask;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Kingamajick
 * 
 */
public class ListCinemasActivity extends ListActivity {

	public static final String SORT_TYPE = "sort_type";
	public static final int SORT_ALPHA = 0;
	public static final int SORT_NEAREST = 1;

	ArrayAdapter<CinemaLocation> cinemaLocationAdapter;
	private ProgressDialog progressDialog;

	/**
	 * @param results
	 */
	public void onRetrieveCinemasTaskFinished(final List<CinemaLocation> results) {
		for (CinemaLocation location : results) {
			this.cinemaLocationAdapter.add(location);
		}
		this.cinemaLocationAdapter.notifyDataSetChanged();
		this.progressDialog.cancel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);
		CinemaLocation cinemaLocation = this.cinemaLocationAdapter.getItem(position);

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

		this.cinemaLocationAdapter = new CinemaLocationAdapter(this, R.layout.cinema_list_item, R.id.CinemaName);
		setListAdapter(this.cinemaLocationAdapter);

		ActivityCallback retrieveCinemasTaskCallback = AsyncTaskWithCallback.createCallback(this, "onRetrieveCinemasTaskFinished",
				List.class);
		new RetrieveCinemasTask(retrieveCinemasTaskCallback).execute();

		this.progressDialog = ProgressDialog.show(this, "", "Retrieving cinemas, please wait...", false);
	}
}
