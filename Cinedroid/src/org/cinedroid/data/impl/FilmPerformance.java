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
package org.cinedroid.data.impl;

import org.cinedroid.data.CineworldData;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kingamajick
 * 
 */
public class FilmPerformance implements CineworldData {

	private String time;
	private boolean available;
	private String type;
	private String bookingUrl;

	/**
	 * @return the time
	 */
	public String getTime() {
		return this.time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final String time) {
		this.time = time;
	}

	/**
	 * @return the available
	 */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * @param available
	 *            the available to set
	 */
	public void setAvailable(final boolean available) {
		this.available = available;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the bookingUrl
	 */
	public String getBookingUrl() {
		return this.bookingUrl;
	}

	/**
	 * @param bookingUrl
	 *            the bookingUrl to set
	 */
	public void setBookingUrl(final String bookingUrl) {
		this.bookingUrl = bookingUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.time;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.data.CineworldData#fromJSON(java.lang.String)
	 */
	@Override
	public void fromJSON(final String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		this.time = json.getString("time");
		this.available = json.getBoolean("available");
		this.type = json.getString("type");
		this.bookingUrl = json.getString("booking_url");
	}
}
