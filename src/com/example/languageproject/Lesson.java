package com.example.languageproject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Lesson {
	private static String GET_PHRASES_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_phrases_by_lesson_id&id=";
	private static String GET_PHRASE_ORDER_HTTP_STRING = "http://www.celebrate-language.com/public-api/?action=get_order_by_lesson_id&id=";

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
		directory = new File(language.getDirectoryName() + "/" + name);
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
	
	private String encodedId() {
		try {
			return URLEncoder.encode(id, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return id;
		}
	}

	public ArrayList<String> getPhraseText() {
		if (phrases.isEmpty()) {
			// get the phrase list from the lesson id
			return processJSONArray(WebAPI.getJSONArray(GET_PHRASES_HTTP_STRING + encodedId()));
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			for (Phrase p : phrases) {
				temp.add(p.getPhraseText());
			}
			return temp;
		}
	}
	
	public ArrayList<Phrase> getPhrases() {
		if (phrases.isEmpty()) {
			processJSONArray(WebAPI.getJSONArray(GET_PHRASES_HTTP_STRING + encodedId()));
		}
		return phrases;
	}
	
	public ArrayList<String> getPhraseOrder() {
		if (phraseOrder.isEmpty()) {
			processJSONArrayPhraseOrder(WebAPI.getJSONArray(GET_PHRASE_ORDER_HTTP_STRING + encodedId()));
		}
		return phraseOrder;
	}

	// processes the JSON array returned from the website
	// returns list of phrase ids
	// will populate the ArrayList phrases with
	// the phrase entries (phrase id, phrase text, translated text, audio url)
	private ArrayList<String> processJSONArray(String results) {
		if (results.startsWith("Error:")) {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(results);
			return temp;
		} else {
			String temp = results.substring(2, results.length() - 2); // remove surrounding brackets
			Pattern p = Pattern.compile("\\],\\[");
			String[] strPhraseEntries = temp.split(p.pattern()); // break the string into separate phrase entries
			ArrayList<String> phraseIDs = new ArrayList<String>();
			for (String s : strPhraseEntries) {
				StringBuilder sBuilder = new StringBuilder(s);
				boolean inQuotes = false;
				for (int i = 0; i < sBuilder.length(); i++) {
					if (sBuilder.charAt(i) == '\"') {
						inQuotes = !inQuotes;
					} else if (sBuilder.charAt(i) == ',' && !inQuotes) {
						sBuilder.setCharAt(i, '@');
					}
				}
			
				String tempEntry = sBuilder.toString();
				tempEntry = tempEntry.replace("\"", "");
				String[] entry = tempEntry.split("@");
				// remove surrounding quotes
				//String tempEntry = s.substring(1, s.length() - 1);
				
				// break the string into:
				// phrase id, phrase text, translated text, audio url
			/*	tempEntry = tempEntry.replaceAll("[\"][,]", "@");
				tempEntry = tempEntry.replaceAll("[,][\"]", "@");
				tempEntry = tempEntry.replace("\"", "");
			*/	
				//String[] entry = s.split("\"");
				
				if (entry != null) {
					phrases.add(new Phrase(entry, this));
					phraseIDs.add(entry[0]);
				}
			}
			return phraseIDs;
		}
	}
	
	private void processJSONArrayPhraseOrder(String results) {
		String temp = results.replace("\"", "");
		temp = temp.substring(1, temp.length() - 1); // remove surrounding brackets
		String[] tempPhraseOrder = temp.split(","); // break the string into separate phrase ids
		for (String s : tempPhraseOrder) {
			phraseOrder.add(s);
		}
	}
}