package com.example.languageproject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Language {
	private static String GET_LESSONS_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_lessons_by_lang&lang=";

	private String name;
	private ArrayList<Lesson> lessons = new ArrayList<Lesson>();
	private Country country;
	private File directory;

	public Language(String languageName, Country languageCountry) {
		name = languageName;
		country = languageCountry;
		directory = new File(country.getDirectoryName() + "/" + name);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public String getName() {
		return name;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public String getDirectoryName() {
		return directory.getAbsolutePath();
	}
	
	private String encodedName() {
		try {
			return URLEncoder.encode(name, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return name;
		}
	}

	public ArrayList<String> getLessonNames() {
		if (lessons.isEmpty()) {
			// get the list of lessons from the language name
			return processJSONArray(WebAPI.getJSONArray(GET_LESSONS_HTTP_STRING + encodedName()));
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			for (Lesson l : lessons) {
				temp.add(l.getName());
			}
			return temp;
		}
	}
	
	public ArrayList<Lesson> getLessons() {
		if (lessons.isEmpty()) {
			processJSONArray(WebAPI.getJSONArray(GET_LESSONS_HTTP_STRING + encodedName()));
		}
		return lessons;
	}
	
	public Lesson getLesson(String lessonName) {
		for (Lesson l : lessons) {
			if (l.getName().equals(lessonName)) {
				return l;
			}
		}
		return null;
	}

	// processes the JSON array returned from the website
	// will populate the ArrayList lessons with
	// lesson names and lesson ids
	private ArrayList<String> processJSONArray(String results) {
		if (results.startsWith("Error:")) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(results);
			return temp;
		} else {
			results = results.replace("\"", "");
			results = results.substring(1, results.length() - 1); // remove surrounding brackets
			String[] lessonNameAndIDArray = results.split(",");
	
			ArrayList<String> lessonNames = new ArrayList<String>();
			for (String s : lessonNameAndIDArray) {
				String temp = s.replace("{", "");
				temp = temp.replace("}", "");
				String[] tempArray = temp.split(":");
				String lessonName = tempArray[0];
				String lessonID = tempArray[1];
				lessonNames.add(lessonName);
				lessons.add(new Lesson(lessonName, lessonID, this));
			}
			return lessonNames;
		}
	}
}
