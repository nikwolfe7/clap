package com.example.languageproject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.Environment;

public class Country {
	private static String GET_LANGUAGES_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_lang_list_by&country=";

	private String name;
	private ArrayList<Language> languages = new ArrayList<Language>();
	private File directory;

	public Country(String countryName) {
		try {
			name = countryName;
			String root = Environment.getExternalStorageDirectory().toString() + "/LanguageProject/";
			directory = new File(root + name);
			if (!directory.mkdirs()) {
				throw new Exception("Error making dirs");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
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
	public ArrayList<String> getLanguageNames() {
		if (languages.isEmpty()) {
			// get the list of languages from the country name
			return processJSONArray(WebAPI.getJSONArray(GET_LANGUAGES_HTTP_STRING + encodedName()));
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			for (Language l : languages) {
				temp.add(l.getName());
			}
			return temp;
		}
	}

	public ArrayList<Language> getLanguages() {
		if (languages.isEmpty()) {
			// get the list of languages from the country name
			processJSONArray(WebAPI.getJSONArray(GET_LANGUAGES_HTTP_STRING + encodedName()));
		}
		return languages;
	}

	public Language getLanguage(String languageName) {
		for (Language l : languages) {
			if (l.getName().equals(languageName)) {
				return l;
			}
		}
		return null;
	}

	private ArrayList<String> processJSONArray(String results) {
		if (results.startsWith("Error:")) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(results);
			return temp;
		} else {
			results = results.replace("\"", "");
			results = results.substring(1, results.length() - 1);
			String[] strArray = results.split(",");
			ArrayList<String> temp = new ArrayList<String>();
			for (String s : strArray) {
				temp.add(s);
				languages.add(new Language(s, this));
			}
			return temp;
		}
	}
}