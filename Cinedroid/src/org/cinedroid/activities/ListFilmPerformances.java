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
import org.cinedroid.adapters.CinemaPerformanceAdapter;
import org.cinedroid.data.FilmDate;
import org.cinedroid.data.FilmPerformance;
import org.cinedroid.tasks.AsyncTaskWithCallback;
import org.cinedroid.tasks.AsyncTaskWithCallback.ActivityCallback;
import org.cinedroid.tasks.impl.DownloadFilmPosterTask;
import org.cinedroid.tasks.impl.RetrieveDatesTask;
import org.cinedroid.tasks.impl.RetrievePerformancesTask;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author Kingamajick
 * 
 */
public class ListFilmPerformances extends ListActivity {

	public final static String CINEMA_ID = "cinema_id";
	public final static String FILM_EDI = "film_edi";
	public final static String FILM_TITLE = "film_title";
	public final static String POSTER_URL = "poster_url";
	public final static String FILM_URL = "film_url";
	public final static String RATING = "rating";
	public final static String ADVISORY = "advisory";

	private CinemaPerformanceAdapter cinemaPerformanceAdapter;
	private BasicNameValuePair cinemaId;
	private BasicNameValuePair filmEdi;
	private ProgressDialog progressDialog;
	private int requestCount = 0;
	private View contentView;

	/**
	 * Colour for unavailable film performances.
	 */
	private static final int UNAVAILABLE_COLOUR = 0xFFFF0000;
	/**
	 * Colour for available film performances.
	 */
	private static final int AVAILABLE_COLOUR = 0xFF00FF00;

	public void onImageDownloaded(final Bitmap image) {
		ImageView filmPoster = (ImageView) findViewById(R.id.FilmImage);
		filmPoster.setImageBitmap(image);
	}

	public void onDatesRetrieved(final List<FilmDate> filmDates) {
		ActivityCallback onPerformanceRecieved = AsyncTaskWithCallback.createCallback(this, "onPerformanceRecieved", FilmDate.class);
		this.requestCount = filmDates.size();
		for (FilmDate filmDate : filmDates) {
			NameValuePair date = new BasicNameValuePair(RetrievePerformancesTask.DATE_PARAM_KEY, filmDate.getDate());
			new RetrievePerformancesTask(onPerformanceRecieved, getString(R.string.cineworld_api_key), filmDate).execute(this.cinemaId,
					this.filmEdi, date);
		}
	}

	public void onPerformanceRecieved(final FilmDate date) {
		this.requestCount--;
		Log.d("org.cineworld", String.format("Data recieved for %s : %s", date.getDate(), date.getPerformances().toString()));
		if (date.getPerformances().size() > 0) {
			this.cinemaPerformanceAdapter.addDate(date);
		}
		// Cancel once all the performances have been retrieved.
		if (this.requestCount == 0) {
			this.progressDialog.cancel();
		}
	}

	private final static int[] PERFORMANCE_TIME_VIEW_IDS = new int[] { R.id.PerformanceTime0, R.id.PerformanceTime1, R.id.PerformanceTime2 };

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		/*
		 * Creates a popup dialog displaying the times available for the date selected.
		 */
		FilmDate selectedDate = this.cinemaPerformanceAdapter.getItem(position);

		View dialogContextView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.film_performances_select_popup, null);

		Dialog dialog = new Dialog(this);
		dialog.setContentView(dialogContextView);

		List<FilmPerformance> performances = selectedDate.getPerformances();

		int numOfPerformances = performances.size();
		int fullRows = numOfPerformances / PERFORMANCE_TIME_VIEW_IDS.length;
		int remainingColumns = numOfPerformances % PERFORMANCE_TIME_VIEW_IDS.length;
		TableLayout table = (TableLayout) dialogContextView.findViewById(R.id.PerformanceTable);
		TableLayout parent = (TableLayout) dialogContextView.findViewById(R.id.PerformanceTable);
		// Populate each of the full rows.
		for (int i = 0; i < fullRows; i++) {
			TableRow r = getRow(table, i, parent);
			int rowStartingIndex = i * PERFORMANCE_TIME_VIEW_IDS.length;
			for (int j = 0; j < PERFORMANCE_TIME_VIEW_IDS.length; j++) {
				populateTime(PERFORMANCE_TIME_VIEW_IDS[j], performances.get(rowStartingIndex + j), r, dialog);
			}
		}
		// Populate the remaining half filled row, if any.
		if (remainingColumns > 0) {
			int rowStartingIndex = fullRows * PERFORMANCE_TIME_VIEW_IDS.length;
			TableRow r = getRow(table, fullRows + 1, parent); // +1 as first row contains the date
			for (int i = 0; i < remainingColumns; i++) {
				populateTime(PERFORMANCE_TIME_VIEW_IDS[i], performances.get(rowStartingIndex + i), r, dialog);
			}
			// Hide the additional text views in the row which are currently not showing data.
			for (int i = remainingColumns; i < PERFORMANCE_TIME_VIEW_IDS.length; i++) {
				TextView as = (TextView) r.findViewById(PERFORMANCE_TIME_VIEW_IDS[i]);
				as.setVisibility(View.INVISIBLE); // View will be show via call to populateTime
			}
		}

		dialog.setTitle(String.format("Select performance for %s.", FilmDate.formatDate(selectedDate)));
		dialog.getWindow().setBackgroundDrawable(this.getResources().getDrawable(R.drawable.dialog));
		dialog.show();

	}

	/**
	 * Populate the given {@link Button} specified by the id {@link #PERFORMANCE_TIME_VIEW_IDS}, with the performance date. The
	 * {@link TableRow} argument is used to locate the view. The {@link Button} will open {@link FilmPerformance#getBookingUrl()} in the
	 * default browser when pressed.
	 * 
	 * @param id
	 *            of the {@link Button} widget.
	 * @param performance
	 * @param r
	 * @param dialog
	 *            the dialog owning this button to be closed when the button is pressed.
	 */
	public void populateTime(final int id, final FilmPerformance performance, final TableRow r, final Dialog dialog) {
		Button button = (Button) r.findViewById(id);
		button.setVisibility(View.VISIBLE);
		if (performance.isAvailable()) {
			button.getBackground().setColorFilter(AVAILABLE_COLOUR, Mode.MULTIPLY);
		}
		else {
			button.getBackground().setColorFilter(UNAVAILABLE_COLOUR, Mode.MULTIPLY);
		}
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(android.net.Uri.parse(performance.getBookingUrl()));
				dialog.cancel();
				ListFilmPerformances.this.startActivity(intent);

			}
		});
		button.setText(performance.getTime());
	}

	/**
	 * From the {@link TableLayout} gets the child at the given index. If this child doesn't already exists a instance of
	 * {@link R.layout#film_performances_list_item_times} is inflated, added to the {@link TableLayout} and returned.
	 * 
	 * @param tableLayout
	 * @param index
	 * @param parent
	 * @return
	 */
	private TableRow getRow(final TableLayout tableLayout, final int index, final ViewGroup parent) {
		TableRow r = (TableRow) tableLayout.getChildAt(index);
		if (r == null) {
			LayoutInflater inflater = getWindow().getLayoutInflater();
			r = (TableRow) inflater.inflate(R.layout.film_performances_select_popup_times, parent, false);
			tableLayout.addView(r);
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityUtils.checkExtras(getIntent(), CINEMA_ID, FILM_EDI, FILM_TITLE, POSTER_URL, FILM_URL, RATING, ADVISORY);
		this.contentView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.film_performances_list,
				null);
		setContentView(this.contentView);

		this.cinemaPerformanceAdapter = new CinemaPerformanceAdapter(this);
		setListAdapter(this.cinemaPerformanceAdapter);

		Bundle extras = getIntent().getExtras();
		TextView filmName = (TextView) findViewById(R.id.FilmTitle);
		filmName.setText(extras.getString(FILM_TITLE));

		TextView filmRating = (TextView) findViewById(R.id.FilmRating);
		filmRating.setText(String.format("Cert. %s", extras.getString(RATING)));

		final String filmURL = extras.getString(FILM_URL);
		Button webButton = (Button) findViewById(R.id.FilmURL);
		webButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(android.net.Uri.parse(filmURL));
				ListFilmPerformances.this.startActivity(intent);

			}
		});

		ActivityCallback downloadFilmPosterCallback = AsyncTaskWithCallback.createCallback(this, "onImageDownloaded", Bitmap.class);
		new DownloadFilmPosterTask(downloadFilmPosterCallback).execute(extras.getString(POSTER_URL));

		ActivityCallback onDatesRetrievedCallback = AsyncTaskWithCallback.createCallback(this, "onDatesRetrieved", List.class);
		this.cinemaId = new BasicNameValuePair(RetrieveDatesTask.CINEMA_PARAM_KEY, Integer.toString(getIntent().getExtras().getInt(
				CINEMA_ID)));

		this.filmEdi = new BasicNameValuePair(RetrievePerformancesTask.FILM_PARAM_KEY, Integer.toString(getIntent().getExtras().getInt(
				FILM_EDI)));
		new RetrieveDatesTask(onDatesRetrievedCallback, getString(R.string.cineworld_api_key)).execute(this.cinemaId);

		this.progressDialog = ProgressDialog.show(this, "", "Retrieving performances, please wait...", false);
	}
}
