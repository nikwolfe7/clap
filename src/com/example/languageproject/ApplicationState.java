package com.example.languageproject;

import java.util.ArrayList;

import android.app.Application;

public class ApplicationState extends Application {
	private static String GET_COUNTRIES_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_country_list";
	private ArrayList<Country> countries = new ArrayList<Country>();

	public ArrayList<String> getCountryNames() {
		if (countries.isEmpty()) {
			// get the list of countries
			return processJSONArray(WebAPI.getJSONArray(GET_COUNTRIES_HTTP_STRING));
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
				countries.add(new Country(s));
			}
			return temp;
		}
	}
}