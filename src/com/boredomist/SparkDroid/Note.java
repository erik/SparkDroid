package com.boredomist.SparkDroid;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.jsoup.Jsoup;

public class Note implements Serializable {

	private static final long serialVersionUID = 2501669370206572984L;

	private String mAuthor;
	private String mBook;
	private String mUrl;
	private String mContent;
	private boolean mCached;

	private ArrayList<NoteSection> mSections;

	private NoteIndex mIndex;

	public Note(String book, String auth, String url) {
		mAuthor = auth;
		mBook = book;

		mUrl = url;
		mCached = false;

		mSections = new ArrayList<NoteSection>();
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getBook() {
		return mBook;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public void fetchIndex() {
		this.mIndex = new NoteIndex(this);
		this.mIndex.update();
	}

	public void fetchSection(String sect) {
		if (this.mIndex == null) {
			fetchIndex();
		}
		
		for(NoteSection s : mSections) {
			if(s.getName().equals(sect)) {
				s.fetch();
			}
		}

	}

	public void fetchAll() {

	}

	public ArrayList<NoteSection> getSections() {
		return this.mIndex.getSections();
	}
}