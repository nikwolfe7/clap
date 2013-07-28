package com.clap;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.os.Environment;

public class ApplicationState extends Application {
	private String directory = Environment.getExternalStorageDirectory().toString() + "/LanguageProject/";
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
		File dir = new File(directory + languageName + "/" + lessonName);
		dir.mkdirs();
		return database.getPhrases(database.getLessonId(lessonName), dir.getAbsolutePath());
	}

	public void resetDatabase() {
		database.reset();
	}
}