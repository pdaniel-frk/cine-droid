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
package org.cinedroid.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.util.Log;

/**
 * @author Kingamajick
 * 
 */
public class FilmDate {
	private final static String TAG = FilmDate.class.getName();
	private String date;
	private List<FilmPerformance> performances;

	/**
	 * @return the performances
	 */
	public List<FilmPerformance> getPerformances() {
		return this.performances;
	}

	/**
	 * @param performances
	 *            the performances to set
	 */
	public void setPerformances(final List<FilmPerformance> performances) {
		this.performances = performances;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(final String date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return this.date;
	}

	public final static String formatDate(final FilmDate date) {
		try {
			Date d = new SimpleDateFormat("yyyyMMdd").parse(date.getDate());
			return new SimpleDateFormat("EEE d MMM").format(d);
		}
		catch (ParseException e) {
			Log.e(TAG, String.format("Unable to parse date %s", date.getDate()), e);
			return "";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("%s - %s", this.date, this.performances.toString());
	}
}
