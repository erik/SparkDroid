package com.boredomist.SparkDroid;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteIndexAdapter extends BaseAdapter {
	private Context context;

	private ArrayList<NoteSection> mSections;

	public NoteIndexAdapter(Context context, ArrayList<NoteSection> sections) {
		this.context = context;
		this.mSections = sections;
	}

	
	public int getCount() {
		return mSections.size();
	}


	public NoteSection getItem(int position) {
		return mSections.get(position);
	}


	public long getItemId(int position) {
		return position;
	}


	public View getView(int position, View convertView, ViewGroup viewGroup) {
		NoteSection sect = mSections.get(position);
		String sectName = sect.getName();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.index_list_item, null);
		}
		TextView textViewSect = (TextView) convertView
				.findViewById(R.id.index_list_item_item);
		textViewSect.setText(sectName);

		return convertView;
	}


}
