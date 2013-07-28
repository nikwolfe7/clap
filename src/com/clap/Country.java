package com.clap;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Country {
	private String name;
	private ArrayList<Language> languages = new ArrayList<Language>();
	private File directory;

	public Country(String countryName, String appDir) {
		try {
			name = countryName;
			directory = new File(appDir + name);
			directory.mkdirs();
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

	/*public ArrayList<String> getLanguageNames() {
		if (languages.isEmpty()) {
			// get the list of languages from the country name
			return processJSONArray(WebAPI
					.getJSONArray(WebAPI.HTTP_GET.LANGUAGES, encodedName()));
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
			processJSONArray(WebAPI.getJSONArray(WebAPI.HTTP_GET.LANGUAGES, encodedName()));
		}
		return languages;
	}*/

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