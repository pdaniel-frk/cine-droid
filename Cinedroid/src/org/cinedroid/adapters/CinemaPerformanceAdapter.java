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
package org.cinedroid.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cinedroid.R;
import org.cinedroid.data.FilmDate;
import org.cinedroid.data.FilmPerformance;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * This {@link BaseAdapter} returns a view which has a single line of text with the performance date, and a table view of performances
 * underneath.
 * 
 * @author Kingamajick
 */
public class CinemaPerformanceAdapter extends BaseAdapter {

	/**
	 * Film date comparator.
	 */
	private static final Comparator<FilmDate> FILM_DATE_COMPARATOR = new Comparator<FilmDate>() {

		@Override
		public int compare(final FilmDate object1, final FilmDate object2) {
			return object1.getDate().compareTo(object2.getDate());
		}
	};

	/**
	 * Colour for unavailable film performances.
	 */
	private static final int UNAVAILABLE_COLOUR = 0xFFFF0000;
	/**
	 * Colour for available film performances.
	 */
	private static final int AVAILABLE_COLOUR = 0xFF00FF00;
	/**
	 * Number of columns to display in the performance table.
	 */
	private final static int NUMBER_OF_COLUMNS = 6;
	/**
	 * Array of the id's of each of the {@link TextView}s to add a performance time. Stored in an array for ease of iteration.
	 */
	private final static int[] PERFORMANCE_TIME_VIEW_IDS = new int[] { R.id.PerformanceTime0, R.id.PerformanceTime1, R.id.PerformanceTime2,
			R.id.PerformanceTime3, R.id.PerformanceTime4, R.id.PerformanceTime5 };
	/**
	 * List of dates when films may be show.
	 */
	private final List<FilmDate> filmDates = new ArrayList<FilmDate>();

	private final Context context;
	private LayoutInflater inflater;

	/**
	 * 
	 * @param context
	 */
	public CinemaPerformanceAdapter(final Context context) {
		this.context = context;
		// this.zoomIn = AnimationUtils.loadAnimation(this.context, R.anim.zoom_in);
	}

	public void addDate(final FilmDate filmDate) {
		this.filmDates.add(filmDate);
		Collections.sort(this.filmDates, FILM_DATE_COMPARATOR);
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = getInflater();
			v = inflater.inflate(R.layout.film_performances_list_item, parent, false);
		}
		TextView performanceDate = (TextView) v.findViewById(R.id.PerformanceDate);
		FilmDate filmDate = this.filmDates.get(position);

		performanceDate.setText(FilmDate.formatDate(filmDate));

		List<FilmPerformance> performances = filmDate.getPerformances();

		int numOfPerformances = performances.size();
		int fullRows = numOfPerformances / CinemaPerformanceAdapter.NUMBER_OF_COLUMNS;
		int remainingColumns = numOfPerformances % CinemaPerformanceAdapter.NUMBER_OF_COLUMNS;
		TableLayout table = (TableLayout) v.findViewById(R.id.PerformanceTable);
		// Populate each of the full rows.
		for (int i = 0; i < fullRows; i++) {
			TableRow r = getRow(table, i + 1, parent); // +1 as first row contains the date
			int rowStartingIndex = i * CinemaPerformanceAdapter.NUMBER_OF_COLUMNS;
			for (int j = 0; j < CinemaPerformanceAdapter.NUMBER_OF_COLUMNS; j++) {
				populateTime(PERFORMANCE_TIME_VIEW_IDS[j], performances.get(rowStartingIndex + j), r);
			}
		}
		// Populate the remaining half filled row, if any.
		if (remainingColumns > 0) {
			int rowStartingIndex = fullRows * CinemaPerformanceAdapter.NUMBER_OF_COLUMNS;
			TableRow r = getRow(table, fullRows + 1, parent); // +1 as first row contains the date
			for (int i = 0; i < remainingColumns; i++) {
				populateTime(PERFORMANCE_TIME_VIEW_IDS[i], performances.get(rowStartingIndex + i), r);
			}
			// Hide the additional text views in the row which are currently not showing data.
			for (int i = remainingColumns; i < CinemaPerformanceAdapter.NUMBER_OF_COLUMNS; i++) {
				TextView as = (TextView) r.findViewById(PERFORMANCE_TIME_VIEW_IDS[i]);
				as.setVisibility(View.INVISIBLE); // View will be show via call to populateTime
			}
		}
		return v;
	}

	/**
	 * @return a {@link LayoutInflater} instance.
	 */
	private LayoutInflater getInflater() {
		if (this.inflater == null) {
			this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		return this.inflater;
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
			LayoutInflater inflater = getInflater();
			r = (TableRow) inflater.inflate(R.layout.film_performances_list_item_times, parent, false);
			tableLayout.addView(r);
		}
		return r;
	}

	/**
	 * Populate the given {@link TextView} specified by the id {@link #PERFORMANCE_TIME_VIEW_IDS}, with the performance date. The
	 * {@link TableRow} argument is used to locate the view.
	 * 
	 * @param id
	 * @param performance
	 * @param r
	 */
	public void populateTime(final int id, final FilmPerformance performance, final TableRow r) {
		TextView as = (TextView) r.findViewById(id);
		// if (as.getVisibility() == View.INVISIBLE) {
		// as.startAnimation(this.zoomIn);
		// }
		as.setVisibility(View.VISIBLE);
		if (performance.isAvailable()) {
			as.getBackground().setColorFilter(AVAILABLE_COLOUR, Mode.MULTIPLY);
		}
		else {
			as.getBackground().setColorFilter(UNAVAILABLE_COLOUR, Mode.MULTIPLY);
		}

		as.setText(performance.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return this.filmDates.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public FilmDate getItem(final int position) {
		return this.filmDates.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(final int position) {
		return position;
	}
}
