package com.clap;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.os.Environment;

public class ApplicationState extends Application {
	private String directory = Environment.getExternalStorageDirectory().toString(); 
	private ClapDatabase database = new ClapDatabase(this);

	public ArrayList<String> getCountryNames() {
		return database.getCountryNames();
	}

	public ArrayList<String> getLanguageNames(String countryName) {
		return database.getLanguageNames(countryName);
	}

	public ArrayList<String> getLessonNames(String languageName) {
		return database.getLessonNames(languageName);
	}

	public ArrayList<Phrase> getPhrases(String lessonName) throws Exception {
		String languageName = lessonName.split(" ")[0];
		File dir = new File(directory + "/" + getResources().getString(R.string.app_name) + "/"
				+ languageName + "/" + lessonName);
		dir.mkdirs();
		return database.getPhrases(database.getLessonId(lessonName), dir.getAbsolutePath());
	}
	
	public ArrayList<String> getPhraseOrder(String lessonName) {
		return database.getPhraseOrder(database.getLessonId(lessonName));
	}

	public void resetDatabase() {
		database.reset();
	}
	
	public void deleteAllFiles() {
		File dir = new File(directory + "/" + getResources().getString(R.string.app_name));
		if (dir.exists()) {
			deleteAllFilesInFolder(dir);
		}
	}
	
	private void deleteAllFilesInFolder(File dir) {
		// recursively delete all files in 'dir'
		for (String s : dir.list()) {
			File f = new File(dir, s);
			if (f.isDirectory()) {
				deleteAllFilesInFolder(f);
			} else {
				f.delete();
			}
		}
		// delete the folder
		dir.delete();
	}
}