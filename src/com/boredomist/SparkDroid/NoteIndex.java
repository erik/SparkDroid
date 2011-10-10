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
	private boolean updated;

	public NoteIndex(Note n) {
		mNote = n;
		mSections = new ArrayList<NoteSection>();
		updated = false;
	}

	public ArrayList<NoteSection> getSections() {
		return mSections;
	}

	public void update() {
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

				String tmpName = section.attr("abs:name");

				/*
				 * // category if (tmpName != null && tmpName.equals("none")) {
				 * NoteSection category = new NoteSection(mNote, name, null);
				 * boolean indented = false; do { entry = iter.next();
				 * 
				 * section = entry.select("a").first(); name =
				 * section.ownText(); url = section.attr("abs:href");
				 * 
				 * category.addSubSection(new NoteSection(mNote, name, url));
				 * 
				 * Log.i("SD", "SUB - " + name + " " + url);
				 * 
				 * indented = entry.select("p").first().className()
				 * .equals("indented"); } while (indented && iter.hasNext()); }
				 */
				mSections.add(new NoteSection(mNote, name, url));

			}

		} catch (Exception e) {
			Log.e("SD", "ERROR " + e);
		}
	}
}
