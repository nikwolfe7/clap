package com.example.languageproject;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.os.Environment;

public class ApplicationState extends Application {
	private static String GET_COUNTRIES_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_country_list";
	private ArrayList<Country> countries = new ArrayList<Country>();
	private String directory = Environment.getExternalStorageDirectory().toString() + "/LanguageProject/";

	public ArrayList<String> getCountryNames() {
		if (countries.isEmpty()) {
			/*File rootDir = new File(directory);
			if (rootDir.exists() && rootDir.isDirectory()) {
				ArrayList<String> temp = new ArrayList<String>();
				for (File f : rootDir.listFiles()) {
					if (f.isDirectory()) {
						temp.add(f.getName());
						countries.add(new Country(f.getName(), directory));
					}
				}
				return temp;
			} else {*/
				// get the list of countries from the website
				return processJSONArray(WebAPI
						.getJSONArray(GET_COUNTRIES_HTTP_STRING));
			//}
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			for (Country country : countries) {
				temp.add(country.getName());
			}
			return temp;
		}
	}

	public Country getCountry(String countryName) {
		for (Country c : countries) {
			if (c.getName().equals(countryName)) {
				return c;
			}
		}
		return null;
	}

	public String getApplicationDirectory() {
		return directory;
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
				countries.add(new Country(s, directory));
			}
			return temp;
		}
	}
}