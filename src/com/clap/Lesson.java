package com.clap;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Lesson {
	private String name;
	private String id;
	private ArrayList<Phrase> phrases = new ArrayList<Phrase>();
	private ArrayList<String> phraseOrder = new ArrayList<String>();
	private Language language;
	private File directory;

	public Lesson(String lessonName, String lessonID, Language lessonLanguage) {
		name = lessonName;
		id = lessonID;
		language = lessonLanguage;
		directory = new File(language.getDirectoryName() + "/" + name + " (" + id + ")");
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public String getDirectoryName() {
		return directory.getAbsolutePath();
	}
}