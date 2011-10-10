package com.boredomist.SparkDroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class NoteIndexActivity extends Activity implements OnItemClickListener {
	private Note mNote;

	@Override
	public boolean onSearchRequested() {

		Intent intent = new Intent(getApplicationContext(),
				SearchActivity.class);
		startActivity(intent);

		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.note_index);

		Intent caller = getIntent();

		Bundle extras = caller.getExtras();
		if (extras != null) {
			Note note = (Note) extras.get("note");
			mNote = note;

			setTitle(note.getBook() + " - " + note.getAuthor());

			ListView listView = (ListView) findViewById(R.id.note_index_list);

			Log.i("SD", note.getSections().toString());

			listView.setAdapter((ListAdapter) new NoteIndexAdapter(
					getApplicationContext(), note.getSections()));

			listView.setTextFilterEnabled(true);
			
			listView.setOnItemClickListener(this);
		}
	}

	public void onItemClick(AdapterView<?> a, View b, final int pos, long id) {
		final ProgressDialog dialog = ProgressDialog.show(
				NoteIndexActivity.this, "", "Loading", true);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {
			public void run() {

				NoteSection noteSection = mNote.getSections().get(pos);

				noteSection.fetch();

				dialog.dismiss();

				Intent intent = new Intent(getApplicationContext(),
						NoteActivity.class);
				intent.putExtra("noteSection", noteSection);

				startActivity(intent);
			}
		}.start();
	}
}
