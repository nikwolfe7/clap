package com.clap;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.os.Environment;

public class ApplicationState extends Application {
	private String directory = Environment.getExternalStorageDirectory().toString();
	private ClapDatabase database = new ClapDatabase(this);

	public String getAppDirectory() {
		return directory + "/" + getResources().getString(R.string.app_name);
	}

	public void addLessonToMyLessons(String lessonName) {
		String lessonId = database.getLessonId(lessonName);
		database.addLessonToMyLessons(lessonName, lessonId);
	}
	
	public void removeLessonFromMyLessons(String lessonName) {
		database.removeLessonFromMyLessons(lessonName);
	}

	public boolean lessonIsInMyLessons(String lessonName) {
		return database.lessonIsInMyLessons(lessonName);
	}

	public ArrayList<String> getMyLessons() {
		return database.getMyLessons();
	}

	public ArrayList<String> getCountryNames() throws Exception {
		return database.getCountryNames();
	}

	public ArrayList<String> getLanguageNames(String countryName) throws Exception {
		return database.getLanguageNames(countryName);
	}

	public ArrayList<String> getLessonNames(String languageName) throws Exception {
		return database.getLessonNames(languageName);
	}

	public ArrayList<Phrase> getPhrases(String lessonName) throws Exception {
		String languageName = lessonName.split(" ")[0];
		String lessonId = database.getLessonId(lessonName);
		File dir = new File(getAppDirectory() + "/"	+ languageName + "/" + lessonName);
		dir.mkdirs();
		return database.getPhrases(lessonId, dir.getAbsolutePath());
	}
	
	public ArrayList<String> getPhraseOrder(String lessonName) throws Exception {
		return database.getPhraseOrder(database.getLessonId(lessonName));
	}

	public void resetDatabase() {
		database.reset();
	}
	
	public void deleteAllFiles() {
		File dir = new File(getAppDirectory());
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