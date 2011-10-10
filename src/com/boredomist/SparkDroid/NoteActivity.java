package com.boredomist.SparkDroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class NoteActivity extends Activity {

	private NoteSection mSection;

	@Override
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

	@Override
	public boolean onSearchRequested() {

		Intent intent = new Intent(getApplicationContext(),
				SearchActivity.class);
		startActivity(intent);

		return false;
	}
}
