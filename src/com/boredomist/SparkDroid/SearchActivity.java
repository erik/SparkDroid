package com.boredomist.SparkDroid;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

public class SearchActivity extends Activity implements OnItemClickListener,
		OnClickListener {

	public static File cacheDir;

	private ProgressDialog mDialog;

	public final Handler handler = new Handler();
	public final Runnable updateResults = new Runnable() {
		public void run() {
			updateData();
		}
	};

	public void onClick(View view) {
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchbox);

		if (view.getId() == R.id.searchbutton) {
			int i = 0;
			for (Note n : NotesCache.getInstance().getNotes()) {
				if (n.getBook().equals(textView.getText().toString())) {
					selectNote(i);
				}
				i++;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SearchActivity.cacheDir = getCacheDir();

		setContentView(R.layout.search);

		mDialog = new ProgressDialog(SearchActivity.this);
		mDialog.setMessage("Loading list");
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setCancelable(false);
		mDialog.show();

		NotesCache.setInstance(null);
		NotesCache.getInstance().update(this, false);
	}

	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		selectNote(position);
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable("NotesCache", NotesCache.getInstance());
	}

	private void selectNote(final int pos) {
		mDialog = ProgressDialog.show(SearchActivity.this, "", "Loading", true);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {
			@Override
			public void run() {

				Note note = NotesCache.getInstance().getNote(pos);
				note.fetchIndex();

				mDialog.dismiss();

				Intent intent = new Intent(getApplicationContext(),
						NoteIndexActivity.class);
				intent.putExtra("note", pos);

				startActivity(intent);
			}
		}.start();
	}

	private void updateData() {

		mDialog.setProgress(NotesCache.getInstance().getCompletion());

		if (NotesCache.getInstance().getCompletion() == 100) {
			mDialog.dismiss();
		}

		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchbox);

		ArrayList<Note> notes = NotesCache.getInstance().getNotes();
		ArrayList<String> noteNames = new ArrayList<String>();
		for (Note n : notes) {
			noteNames.add(n.getBook());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.autocomplete_list_item, noteNames);

		textView.setAdapter(adapter);

		ListView listView = (ListView) findViewById(R.id.notes_list);

		listView.setAdapter(new NotesAdapter(getApplicationContext(),
				NotesCache.getInstance().getNotes()));

		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(this);

		Button button = (Button) findViewById(R.id.searchbutton);
		button.setOnClickListener(this);
	}
}
