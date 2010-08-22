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

import org.cinedroid.R;
import org.cinedroid.data.impl.Cinema;

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
public class CinemaLocationAdapter extends ArrayAdapter<Cinema> {
	/**
	 * 
	 */
	private final Context context;

	/**
	 * @param context
	 * @param resource
	 * @param textViewResourceId
	 */
	public CinemaLocationAdapter(final Context context, final int resource, final int textViewResourceId) {
		super(context, resource, textViewResourceId);
		this.context = context;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.cinema_list_item, parent, false);
		}
		Cinema cinema = getItem(position);
		TextView cinemaName = (TextView) v.findViewById(R.id.CinemaName);
		cinemaName.setText(cinema.getName());

		return v;
	}

}