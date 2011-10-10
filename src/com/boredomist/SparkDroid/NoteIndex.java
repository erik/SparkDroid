package com.boredomist.SparkDroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class NoteIndex implements Serializable {

	private static final long serialVersionUID = 6849498101668878397L;

	private Note mNote;
	private ArrayList<NoteSection> mSections;
	private boolean mUpdated;

	public NoteIndex(Note n) {
		mNote = n;
		mSections = new ArrayList<NoteSection>();
		mUpdated = false;
	}

	public ArrayList<NoteSection> getSections() {
		return mSections;
	}

	public void update() {
		if (mUpdated) {
			Log.i("SD", "Index already up to date, skipping");
			return;
		}
		try {
			Document doc = Jsoup.connect(mNote.getUrl()).timeout(7000)
					.userAgent("Mozilla/5.0").get();

			Elements entries = doc.getElementsByClass("entry");
			Iterator<Element> iter = entries.iterator();

			while (iter.hasNext()) {

				Element entry = iter.next();

				Element section = entry.select("a").first();

				String name = section.ownText();
				String url = section.attr("abs:href");

				Log.i("SD", name + " " + url);

				mSections.add(new NoteSection(mNote, name, url));
			}
			mUpdated = true;
		} catch (Exception e) {
			Log.e("SD", "ERROR " + e);
		}
	}
}
