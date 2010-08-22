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

import java.util.List;

import org.cinedroid.R;
import org.cinedroid.data.impl.Film;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Kingamajick
 * 
 */
public class FilmAdapter extends ArrayAdapter<Film> {
	final Context context;

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public FilmAdapter(final Context context) {
		// getView is to be overridden so the values of the second two arguments are unimportant.
		super(context, 0, 0);
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.film_list_item, parent, false);
		}
		Film film = getItem(position);

		TextView title = (TextView) v.findViewById(R.id.FilmTitle);
		title.setText(film.getTitle());

		TextView rating = (TextView) v.findViewById(R.id.FilmRating);
		rating.setText(String.format("Cert. %s", film.getClassification()));
		return v;
	}

	public void addItems(final List<Film> films) {
		for (Film film : films) {
			add(film);
		}
		notifyDataSetChanged();
	}

}
