package com.boredomist.SparkDroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NoteActivity extends Activity {

	private NoteSection mSection;

	@Override
	public boolean onSearchRequested() {

		Intent intent = new Intent(getApplicationContext(),
				SearchActivity.class);
		startActivity(intent);

		return false;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();

		mSection = null;

		if (bundle != null) {
			mSection = (NoteSection) bundle.get("noteSection");
		}

		setContentView(R.layout.note_section);

		setTitle(mSection.getName());
		
		TextView view = (TextView) findViewById(R.id.section_content);
		view.setText(Html.fromHtml(mSection.getText()));
	}
}
