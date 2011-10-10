package com.boredomist.SparkDroid;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NotesCache implements Serializable {

	private static final long serialVersionUID = 303313901179783177L;

	private ArrayList<Note> mNotes;
	private boolean mUpdated;
	private int mCompletion;
	transient private SearchActivity mSearchActivity;

	public NotesCache() {
		mNotes = new ArrayList<Note>();
		mUpdated = false;
		mCompletion = 0;
		mSearchActivity = null;
	}

	public void setCompletion(int i) {
		mCompletion = i;
	}

	public void addNote(Note n) {
		mNotes.add(n);
	}

	public void addNote(String book, String auth, String url) {
		mNotes.add(new Note(book, auth, url));
	}

	public ArrayList<Note> getNotes() {
		return mNotes;
	}

	public void update(SearchActivity act, boolean force) {
		if (mUpdated && !force) {
			mUpdated = true;
			mCompletion = 100;
			return;
		} else {
			mUpdated = false;
			mCompletion = 0;

			mSearchActivity = act;
			new PopulateCache().execute(this);

		}
	}

	public int getCompletion() {
		return mCompletion;
	}

	public boolean isUpToDate() {
		return mUpdated;
	}

	public List<String> getNoteArray() {
		ArrayList<String> notes = new ArrayList<String>();

		for (Note n : mNotes) {
			notes.add(n.getBook());
		}

		return notes;
	}

	private class PopulateCache extends AsyncTask<NotesCache, Void, Void> {
		public Void doInBackground(NotesCache... caches) {

			NotesCache cache = caches[0];

			SearchActivity act = cache.mSearchActivity;

			File cacheDir = cache.mSearchActivity.getCacheDir();
			File notesListFile = new File(cacheDir, "notes_cache");

			if (notesListFile.exists()) {

				Log.i("SD", "Cache file exists, reading...");

				FileInputStream fin = null;
				try {
					fin = new FileInputStream(notesListFile);

					ObjectInputStream oin = new ObjectInputStream(fin);
					NotesCache serial = (NotesCache) oin.readObject();

					for (Note n : serial.mNotes) {
						cache.addNote(n);
					}

				} catch (Exception e) {

				}
				/*
				 * Scanner scan = new Scanner(fin); while (scan.hasNextLine()) {
				 * cache.addNote(scan.nextLine(), scan.nextLine(), "TODO"); }
				 */

				cache.mCompletion = 100;

				act.handler.post(act.updateResults);

				Log.i("SD", "Loaded from file.");

			} else {
				ObjectOutputStream oout = null;

				try {
					notesListFile.createNewFile();
					oout = new ObjectOutputStream(new FileOutputStream(
							notesListFile));

					Document doc = null;

					for (int i = 0; i < 26; ++i) {
						char let = (char) ('a' + i);

						doc = Jsoup
								.connect(
										"http://sparknotes.com/lit/index_"
												+ let + ".html").timeout(7000)
								.userAgent("Mozilla/5.0").get();

						Elements entries = doc.getElementsByClass("entry");

						for (Element entry : entries) {

							Element book = entry.select("a").first();
							Element author = entry.select("span").first();

							Log.i("SD",
									book.ownText() + " - " + author.ownText()
											+ " " + book.attr("abs:href"));

							cache.addNote(book.ownText(), author.ownText(),
									book.attr("abs:href"));

						}

						cache.mCompletion = (int) (((i + 1) / 26.0) * 100);

						act.handler.post(act.updateResults);
					}

					Log.i("SD", "Serializing cache");
					oout.writeObject(cache);
					oout.close();
					Log.i("SD", "Finished writing");
				} catch (Exception e) {
					Log.e("SD", "Error" + e);
				}
				return null;
			}
			return null;
		}
	}

}
