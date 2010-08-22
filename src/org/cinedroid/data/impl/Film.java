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
public class Film implements CineworldData {

	private int edi;
	private String title;
	private String classification;
	private String advisory;
	private String posterUrl;
	private String stillUrl;
	private String filmUrl;

	/**
	 * @return the edi
	 */
	public int getEdi() {
		return this.edi;
	}

	/**
	 * @param edi
	 *            the edi to set
	 */
	public void setEdi(final int edi) {
		this.edi = edi;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the classification, can be null.
	 */
	public String getClassification() {
		return this.classification;
	}

	/**
	 * @param classification
	 *            the classification to set
	 */
	public void setClassification(final String classification) {
		this.classification = classification;
	}

	/**
	 * @return the advisory, can be null.
	 */
	public String getAdvisory() {
		return this.advisory;
	}

	/**
	 * @param advisory
	 *            the advisory to set
	 */
	public void setAdvisory(final String advisory) {
		this.advisory = advisory;
	}

	/**
	 * @return the posterUrl
	 */
	public String getPosterUrl() {
		return this.posterUrl;
	}

	/**
	 * @param posterUrl
	 *            the posterUrl to set
	 */
	public void setPosterUrl(final String posterUrl) {
		this.posterUrl = posterUrl;
	}

	/**
	 * @return the stillUrl
	 */
	public String getStillUrl() {
		return this.stillUrl;
	}

	/**
	 * @param stillUrl
	 *            the stillUrl to set
	 */
	public void setStillUrl(final String stillUrl) {
		this.stillUrl = stillUrl;
	}

	/**
	 * @return the filmUrl
	 */
	public String getFilmUrl() {
		return this.filmUrl;
	}

	/**
	 * @param filmUrl
	 *            the filmUrl to set
	 */
	public void setFilmUrl(final String filmUrl) {
		this.filmUrl = filmUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.cinedroid.data.CineworldData#fromJSON(java.lang.String)
	 */
	@Override
	public void fromJSON(final String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		this.edi = json.getInt("edi");
		this.title = json.getString("title");
		if (!json.isNull("classification")) {
			this.classification = json.getString("classification");
		}
		else {
			this.classification = "??";
		}
		if (!json.isNull("advisory")) {
			this.advisory = json.getString("advisory");
		}
		if (!json.isNull("poster_url")) {
			this.posterUrl = json.getString("poster_url");
		}
		if (!json.isNull("still_url")) {
			this.stillUrl = json.getString("still_url");
		}
		if (!json.isNull("film_url")) {
			this.filmUrl = json.getString("film_url");
		}

	}
}
