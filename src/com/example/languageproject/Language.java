package com.example.languageproject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Language {
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

	/*public ArrayList<String> getLessonNames() {
		if (lessons.isEmpty()) {
			// get the list of lessons from the language name
			return processJSONArray(WebAPI.getJSONArray(WebAPI.HTTP_GET.LESSONS, encodedName()));
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
			processJSONArray(WebAPI.getJSONArray(WebAPI.HTTP_GET.LESSONS, encodedName()));
		}
		return lessons;
	}*/
	
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
			results = results.substring(1, results.length() - 1); // remove first bracket of first group and
																  // last bracket of last group
			String[] lessonNameAndIDArray = results.split(Pattern.compile("\\Q]\\E,\\Q[\\E").pattern());
	
			ArrayList<String> lessonNames = new ArrayList<String>();
			for (String s : lessonNameAndIDArray) {
				String[] tempArray = s.split(",");
				String lessonName = tempArray[0];
				String lessonID = tempArray[1];
				lessonNames.add(lessonName);
				lessons.add(new Lesson(lessonName, lessonID, this));
			}
			return lessonNames;
		}
	}
}
