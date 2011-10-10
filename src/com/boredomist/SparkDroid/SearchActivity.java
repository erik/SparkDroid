package com.boredomist.SparkDroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

public class SearchActivity extends Activity implements AnimationListener,
		OnItemClickListener, OnClickListener {

	private static NotesCache notesCache;
	private ProgressDialog mDialog;

	public final Handler handler = new Handler();
	public final Runnable updateResults = new Runnable() {
		public void run() {
			updateData();
		}
	};
	public final Runnable showProgressDialog = new Runnable() {
		public void run() {
			mDialog.show();
		}
	};
	public final Runnable hideProgressDialog = new Runnable() {
		public void run() {
			mDialog.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			Log.i("SD", "RESTORING");
			Object cache = savedInstanceState.getSerializable("NotesCache");
			SearchActivity.notesCache = (NotesCache) cache;
		} else {
			SearchActivity.notesCache = new NotesCache();
		}

		setContentView(R.layout.search);

		SearchActivity.notesCache.update(this, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putSerializable("NotesCache", SearchActivity.notesCache);
	}

	public void onAnimationEnd(Animation animation) {
		ProgressBar downloadProgress = (ProgressBar) findViewById(R.id.listprogressbar);
		downloadProgress.setVisibility(View.GONE);

	}

	private void updateData() {
		mDialog = new ProgressDialog(getApplicationContext());
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setIndeterminate(true);
		mDialog.setMessage("Loading...");

		ProgressBar downloadProgress = (ProgressBar) findViewById(R.id.listprogressbar);
		downloadProgress.setProgress(notesCache.getCompletion());

		if (notesCache.getCompletion() == 100) {

			notesCache.setCompletion(101);

			Animation animation;

			animation = new AlphaAnimation(1.0f, 0.0f);
			animation.setInterpolator(new DecelerateInterpolator());
			animation.setDuration(500L);
			animation.setAnimationListener(this);

			downloadProgress.startAnimation(animation);
		}

		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchbox);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.autocomplete_list_item, notesCache.getNoteArray());

		textView.setAdapter(adapter);

		ListView listView = (ListView) findViewById(R.id.notes_list);

		listView.setAdapter(new NotesAdapter(getApplicationContext(),
				notesCache.getNotes()));

		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(this);

		Button button = (Button) findViewById(R.id.searchbutton);
		button.setOnClickListener(this);
	}

	private void selectNote(final int pos) {
		mDialog = ProgressDialog.show(SearchActivity.this, "", "Loading", true);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {
			public void run() {

				Note note = notesCache.getNotes().get(pos);
				note.fetchIndex();

				mDialog.dismiss();

				Intent intent = new Intent(getApplicationContext(),
						NoteIndexActivity.class);
				intent.putExtra("note", note);

				startActivity(intent);
			}
		}.start();
	}

	public void onClick(View view) {
		AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchbox);

		if (view.getId() == R.id.searchbutton) {
			int i = 0;
			for (Note n : notesCache.getNotes()) {
				if (n.getBook().equals(textView.getText().toString())) {
					selectNote(i);
				}
				i++;
			}
		}
	}

	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		selectNote(position);
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}
}
