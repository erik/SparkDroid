package com.boredomist.SparkDroid;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter  {
	private Context context;

	private ArrayList<Note> mNotes;

	public NotesAdapter(Context context, ArrayList<Note> notes) {
		this.context = context;
		this.mNotes = notes;
	}

	public int getCount() {
		return mNotes.size();
	}

	public Object getItem(int position) {
		return mNotes.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Note note = mNotes.get(position);
		String noteName = note.getBook();
		String author = note.getAuthor();

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = inflater.inflate(R.layout.list_item, null);
		}
		TextView  textViewBook = (TextView) convertView
				.findViewById(R.id.book_name);
		textViewBook.setText(noteName);

		TextView textViewAuthor = (TextView) convertView.findViewById(R.id.book_author);
		textViewAuthor.setText(author);

		return convertView;
	}
	/*
	 * @Override public void onClick(View view) { notifyDataSetChanged(); }
	 */

}
