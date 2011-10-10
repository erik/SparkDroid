package com.boredomist.SparkDroid;

import java.io.Serializable;
import java.util.ArrayList;

import android.util.Log;

public class Note implements Serializable {

	private static final long serialVersionUID = 2501669370206572984L;

	private String mAuthor;
	private String mBook;
	private String mUrl;
	private String mContent;
	private boolean mUpdated;

	private ArrayList<NoteSection> mSections;

	private NoteIndex mIndex;

	private int mCompletion;

	public Note(String book, String auth, String url) {
		mAuthor = auth;
		mBook = book;

		mUrl = url;
		mUpdated = false;

		mCompletion = 0;

		mIndex = new NoteIndex(this);

		mSections = new ArrayList<NoteSection>();
	}

	// 0 is partial
	// 1 is full
	// -1 is uncached
	public int cachedState() {

		if (mIndex == null || !mIndex.getFetched()) {
			return -1;
		}

		for (NoteSection section : mIndex.getSections()) {
			if (!section.isFetched()) {
				Log.i("SD", getBook() + " is partially cached");
				return 0;
			}
		}

		return 1;
	}

	public void fetchAll() {
		if (!mUpdated) {
			// mIndex = new NoteIndex(this);
			mUpdated = true;
		}
		mIndex.update();

		mCompletion = 1;

		for (NoteSection s : mIndex.getSections()) {
			s.fetch();
			mCompletion++;
		}

	}

	public void fetchIndex() {
		if (!mUpdated) {
			// this.mIndex = new NoteIndex(this);
			mUpdated = true;
		}
		mIndex.update();

		if (mIndex.getFetched() == true) {
			mUpdated = false;
		} else {
			mSections = mIndex.getSections();
			mCompletion = 1;
		}
	}

	public void fetchSection(String sect) {
		if (this.mIndex == null) {
			fetchIndex();
		}

		for (NoteSection s : mSections) {
			if (s.getName().equals(sect)) {
				s.fetch();
				mCompletion++;
			}
		}
	}

	public String getAuthor() {
		return mAuthor;
	}

	public String getBook() {
		return mBook;
	}

	public int getCompletion() {
		return mCompletion;
	}

	public String getContent() {
		return mContent;
	}

	public ArrayList<NoteSection> getSections() {
		return this.mIndex.getSections();
	}

	public String getUrl() {
		return mUrl;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	public void setUrl(String mUrl) {
		this.mUrl = mUrl;
	}

	public void unFetch() {
		this.mIndex = new NoteIndex(this);
		this.mSections = new ArrayList<NoteSection>();
		this.mUpdated = false;

		NotesCache.getInstance().writeCache();
	}

}
