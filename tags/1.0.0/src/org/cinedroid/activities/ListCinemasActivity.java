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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Kingamajick
 * 
 */
public class ListCinemasActivity extends ListActivity implements ActivityCallback {

	/**
	 * Settings keys
	 */
	private static final String SETTING_TERRITORY = "territory";
	private static final String SETTING_MYCINEMA_NAME = "myCinemaName";
	private static final String SETTING_MYCINEMA_ID = "myCinemaID";
	private static final String SETTING_MYCINEMA_URL = "myCinemaURL";

	/**
	 * Context menu ids.
	 */
	private final static int CONTEXT_SET_FAV = 0;
	private final static int CONTEXT_REM_FAV = 1;
	private final static int CONTEXT_VISIT_WEB = 2;

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

		// Create header if user has a favorite cinema.
		String myCinemaName = this.settings.getString(SETTING_MYCINEMA_NAME, null);
		int myCinemaId = this.settings.getInt(SETTING_MYCINEMA_ID, -1);
		String myCinemaURL = this.settings.getString(SETTING_MYCINEMA_URL, null);
		if (myCinemaName != null && myCinemaId != -1 && myCinemaURL != null) {
			Cinema myCinema = new Cinema();
			myCinema.setName(myCinemaName);
			myCinema.setId(myCinemaId);
			myCinema.setUrl(myCinemaURL);
			createHeader(myCinema);
		}

		this.cinemaLocationAdapter = new CinemaLocationAdapter(this, R.layout.cinema_list_item, R.id.CinemaName);
		setListAdapter(this.cinemaLocationAdapter);

		this.getListView().setTextFilterEnabled(true);

		registerForContextMenu(this.getListView());

		retrieveCinemas();

	}

	/**
	 * Creates a header at the top of the view containing the users favourite cinema.
	 * 
	 * @param myCinema
	 */
	private void createHeader(final Cinema myCinema) {
		ViewStub headerStub = (ViewStub) findViewById(R.id.CinemaListHeaderStub);
		View header;
		if (headerStub != null) {
			header = headerStub.inflate();
			registerForContextMenu(header);
		}
		else {
			header = findViewById(R.id.CinemaListHeader);
			header.setVisibility(View.VISIBLE);
		}
		TextView myCinemaView = (TextView) header.findViewById(R.id.MyCinema);
		myCinemaView.setTag(myCinema);
		myCinemaView.setText(myCinema.getName());

		myCinemaView.setFocusable(true);
		myCinemaView.setHighlightColor(0xFFFF0000);
		if (headerStub != null) {
			registerForContextMenu(myCinemaView);
			myCinemaView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(final View v) {
					Intent viewFilmsIntent = new Intent(ListCinemasActivity.this, ListFilmsActivity.class);
					Cinema c = (Cinema) v.getTag();
					viewFilmsIntent.putExtra(ListFilmsActivity.CINEMA_ID, c.getId());

					ListCinemasActivity.this.startActivity(viewFilmsIntent);
				}
			});
		}
	}

	/**
	 * Retrieve the cinemas from the Cineworld API.
	 */
	public void retrieveCinemas() {
		NameValuePair territory = new BasicNameValuePair(CineworldAPIAssistant.TERRITORY, this.settings.getString(SETTING_TERRITORY, "GB"));
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
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		if (menuInfo instanceof AdapterContextMenuInfo) {
			ListView listView = (ListView) v;
			AdapterContextMenuInfo adptMenuInfo = (AdapterContextMenuInfo) menuInfo;
			Cinema cinema = (Cinema) listView.getItemAtPosition(adptMenuInfo.position);
			menu.setHeaderTitle(cinema.getName());
			menu.add(Menu.NONE, CONTEXT_SET_FAV, Menu.NONE, "Set as favourite");
			menu.add(Menu.NONE, CONTEXT_VISIT_WEB, Menu.NONE, "Visit website");
		}
		else if (v instanceof TextView && v.getTag() != null) {
			Cinema cinema = (Cinema) v.getTag();
			menu.setHeaderTitle(cinema.getName());
			menu.add(Menu.NONE, CONTEXT_REM_FAV, Menu.NONE, "Remove as favourite");
			menu.add(Menu.NONE, CONTEXT_VISIT_WEB, Menu.NONE, "Visit website");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		ContextMenuInfo menuInfo = item.getMenuInfo();
		if (menuInfo instanceof AdapterContextMenuInfo) {
			AdapterContextMenuInfo adptMenuInfo = (AdapterContextMenuInfo) menuInfo;
			Cinema cinema = (Cinema) getListView().getItemAtPosition(adptMenuInfo.position);
			switch (item.getItemId()) {
			case CONTEXT_SET_FAV:
				createHeader(cinema);
				SharedPreferences.Editor editor = this.settings.edit();
				editor.putString(SETTING_MYCINEMA_NAME, cinema.getName());
				editor.putInt(SETTING_MYCINEMA_ID, cinema.getId());
				editor.putString(SETTING_MYCINEMA_URL, cinema.getUrl());
				editor.commit();
				break;
			case CONTEXT_VISIT_WEB:
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(android.net.Uri.parse(cinema.getUrl()));
				ListCinemasActivity.this.startActivity(intent);
				break;
			}
			return true;
		}
		else {
			// Only other type of context menu is for the my cinema text view.
			View header = findViewById(R.id.CinemaListHeader);
			TextView myCinemaView = (TextView) header.findViewById(R.id.MyCinema);
			switch (item.getItemId()) {
			case CONTEXT_REM_FAV:
				SharedPreferences.Editor editor = this.settings.edit();
				editor.remove(SETTING_MYCINEMA_NAME);
				editor.remove(SETTING_MYCINEMA_ID);
				editor.remove(SETTING_MYCINEMA_URL);
				editor.commit();
				header.setVisibility(View.GONE);
				break;
			case CONTEXT_VISIT_WEB:
				Cinema cinema = (Cinema) myCinemaView.getTag();
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(android.net.Uri.parse(cinema.getUrl()));
				ListCinemasActivity.this.startActivity(intent);
				break;
			}

		}
		return super.onContextItemSelected(item);
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
		String current = this.settings.getString(SETTING_TERRITORY, "GB");
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
		String current = this.settings.getString(SETTING_TERRITORY, "GB");
		switch (item.getItemId()) {
		case R.id.GB:
			if (!current.equals("GB")) {
				SharedPreferences.Editor editor = this.settings.edit();
				editor.putString(SETTING_TERRITORY, "GB");
				editor.commit();
				retrieveCinemas();
			}
			break;
		case R.id.IE:
			if (!current.equals("IE")) {
				SharedPreferences.Editor editor = this.settings.edit();
				editor.putString(SETTING_TERRITORY, "IE");
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
