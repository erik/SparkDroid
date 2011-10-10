package com.boredomist.SparkDroid;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
	public boolean onContextItemSelected(MenuItem item) {

		String book = ((TextView) (((AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo())).targetView.findViewById(R.id.book_name))
				.getText().toString();

		int i = 0;
		for (Note n : NotesCache.getInstance().getNotes()) {
			if (n.getBook().equals(book)) {
				break;
			}
			++i;
		}

		final int pos = i;

		switch (item.getItemId()) {

		case R.id.note_cached:
			mDialog = ProgressDialog.show(SearchActivity.this, "", "Deleting",
					true);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			new Thread() {
				@Override
				public void run() {

					Note note = NotesCache.getInstance().getNote(pos);

					note.unFetch();
					handler.post(updateResults);

					mDialog.dismiss();

				}
			}.start();

			break;
		case R.id.note_uncached:
			mDialog = ProgressDialog.show(SearchActivity.this, "",
					"Downloading", true);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			new Thread() {
				@Override
				public void run() {

					Note note = NotesCache.getInstance().getNote(pos);

					note.fetchAll();
					handler.post(updateResults);

					mDialog.dismiss();
				}
			}.start();
			break;
		case R.id.view_note:
			selectNote(pos);
			break;

		default:
			return super.onContextItemSelected(item);
		}

		return true;
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

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		String book = ((TextView) (info.targetView)
				.findViewById(R.id.book_name)).getText().toString();

		Note note = null;
		for (Note n : NotesCache.getInstance().getNotes()) {
			if (n.getBook().equals(book)) {
				note = n;
				break;
			}
		}

		menu.setHeaderTitle(note.getBook());

		int cached = note.cachedState();

		if (cached < 1) {
			menu.add(Menu.NONE, R.id.note_uncached, Menu.NONE,
					"Save This Note For Later");
		} else {
			menu.add(Menu.NONE, R.id.note_cached, Menu.NONE, "Delete This Note");
		}

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_menu, menu);

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
		ConnectivityManager manager = (ConnectivityManager) getSystemService(SearchActivity.CONNECTIVITY_SERVICE);

		Note n = NotesCache.getInstance().getNote(pos);
		if (n.cachedState() < 0) {
			if (!manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.isConnectedOrConnecting()
					&& !manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
							.isConnectedOrConnecting()) {

				AlertDialog dialog = new AlertDialog.Builder(
						SearchActivity.this)
						.setTitle("Not connected to a wireless network")
						.setMessage(
								"You must be connected to a wireless network in order to view a note that hasn't been previously stored.")
						.setNeutralButton("Dismiss", null)
						.setPositiveButton("View Network Settings",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										startActivity(new Intent(
												android.provider.Settings.ACTION_WIFI_SETTINGS));
									}
								})

						.create();
				dialog.show();
				return;
			}
		}

		mDialog = ProgressDialog.show(SearchActivity.this, "", "Loading", true);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		new Thread() {
			@Override
			public void run() {

				Note note = NotesCache.getInstance().getNote(pos);
				note.fetchIndex();

				if (note.cachedState() != -1) {
					handler.post(updateResults);

					mDialog.dismiss();

					Intent intent = new Intent(getApplicationContext(),
							NoteIndexActivity.class);
					intent.putExtra("note", pos);

					startActivity(intent);
				}
			}
		}.start();
	}

	private void updateData() {

		if (NotesCache.getInstance().getFailed()) {

			mDialog.dismiss();

			AlertDialog dialog = new AlertDialog.Builder(SearchActivity.this)
					.setTitle("Error")
					.setCancelable(false)
					.setPositiveButton("View Network Settings",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Log.e("SD",
											"Quitting due to error, jumping to networksettings first... ");

									startActivity(new Intent(
											android.provider.Settings.ACTION_WIFI_SETTINGS));

									System.exit(1);
								}
							})
					.setNegativeButton("Quit",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Log.e("SD", "Quitting due to error... ");

									System.exit(1);
								}
							}).setMessage("Error ocurred while loading.")
					.create();
			dialog.show();
		}

		mDialog.setProgress(NotesCache.getInstance().getCompletion());
		mDialog.setMessage("Loading list "
				+ (int) ((NotesCache.getInstance().getCompletion() / 100.0) * 26)
				+ " / " + 26);

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
		textView.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int id, KeyEvent event) {
				if (id == EditorInfo.IME_ACTION_SEARCH
						|| id == EditorInfo.IME_ACTION_DONE
						|| event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					TextView textView = (AutoCompleteTextView) findViewById(R.id.searchbox);

					int i = 0;
					for (Note n : NotesCache.getInstance().getNotes()) {
						if (n.getBook().equals(textView.getText().toString())) {
							selectNote(i);
						}
						i++;
					}
					return true;
				}
				return false;
			}
		});

		ListView listView = (ListView) findViewById(R.id.notes_list);

		listView.setAdapter(new NotesAdapter(getApplicationContext(),
				NotesCache.getInstance().getNotes()));

		listView.setTextFilterEnabled(true);
		listView.setOnItemClickListener(this);

		registerForContextMenu(listView);

		Button button = (Button) findViewById(R.id.searchbutton);
		button.setOnClickListener(this);
	}
}
