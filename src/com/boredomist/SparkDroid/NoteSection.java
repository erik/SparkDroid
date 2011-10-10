package com.boredomist.SparkDroid;

import java.io.Serializable;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import android.util.Log;

public class NoteSection implements Serializable {
	private static final long serialVersionUID = 8937241032269666362L;

	private String mText;
	private Note mNote;
	private ArrayList<NoteSection> mSubSections;
	private String mName;
	private String mUrl;
	private boolean mFetched;

	public NoteSection(Note note, String name, String url) {
		setNote(note);
		setName(name);
		setUrl(url);
		mFetched = false;
		mSubSections = new ArrayList<NoteSection>();
		mText = null;
	}

	public void addSubSection(NoteSection section) {
		mSubSections.add(section);
	}

	public void fetch() {
		if (mFetched) {
			return;
		}

		try {
			Document doc = Jsoup.connect(mUrl).userAgent("Mozilla/5.0")
					.timeout(7000).get();

			Elements ads = doc.getElementsByClass("floatingad");
			for (Node ad : ads) {
				ad.remove();
			}
			
			Element e = doc.getElementsByClass("studyGuideText").first();

			mText = e.html();
			mFetched = true;

		} catch (Exception e) {
			Log.e("SD", "ERROR " + e);
		}
	}

	public String getName() {
		return mName;
	}

	public Note getNote() {
		return mNote;
	}

	public ArrayList<NoteSection> getSubSections() {
		return mSubSections;
	}

	public String getText() {
		return mText;
	}

	public String getUrl() {
		return mUrl;
	}

	public int numSections() {
		return mSubSections.size();
	}

	public void setName(String name) {
		mName = name;
	}

	public void setNote(Note note) {
		mNote = note;
	}

	public void setUrl(String url) {
		mUrl = url;
	}
}
