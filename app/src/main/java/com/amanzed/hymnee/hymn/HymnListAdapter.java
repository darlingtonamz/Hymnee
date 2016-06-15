package com.amanzed.hymnee.hymn;

import java.util.List;

import com.amanzed.hymnee.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HymnListAdapter extends BaseAdapter {
	private Activity activity;
	private LayoutInflater inflater;
	private List<Hymn> hymnItems;

	public HymnListAdapter(Activity activity, List<Hymn> hymnItems) {
		this.activity = activity;
		this.hymnItems = hymnItems;
	}

	@Override
	public int getCount() {
		return hymnItems.size();
	}

	@Override
	public Object getItem(int location) {
		return hymnItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.hymn_list_item, null);
		TextView id = (TextView) convertView.findViewById(R.id.hymn_list_numb);
		TextView title = (TextView) convertView.findViewById(R.id.hymn_list_name);

		// getting hymn data for the row
		Hymn m = hymnItems.get(position);

		id.setText(String.valueOf(m.getNumb()));
		title.setText(m.getTitle());

		return convertView;
	}


}