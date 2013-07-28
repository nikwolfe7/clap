package com.clap;

import java.util.ArrayList;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ClapDatabase {
	private SQLHelper databaseHelper;
	private SQLiteDatabase database;
	private Context context;
	private static final String WHERE = " is ?";

	public ClapDatabase(Context c) {
		this.context = c;
		databaseHelper = new SQLHelper(this.context);
	}

	public void open() throws SQLException {
		database = databaseHelper.getWritableDatabase();
	}

	public ArrayList<String> getCountryNames() {
		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the countries
		// yet
		Cursor cursor = database.query(SQLHelper.TABLE_COUNTRIES, new String[] {
				SQLHelper.COLUMN_ID , SQLHelper.COLUMN_COUNTRY }, null,
				null, null, null, null);

		if (cursor.moveToFirst()) {
			return getFromCursor(cursor, SQLHelper.COLUMN_COUNTRY);
		} else {
			// database table was empty
			// download the country names and populate the table
			String results;
			try {
				results = WebAPI.getJSONArray(WebAPI.HTTP_GET.COUNTRIES);
			} catch (Exception e) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getMessage());
				return temp;
			}
			
			// remove quotes
			results = results.replace("\"", "");
			// should have a list of countries, split by commas
			String[] strArray = results.split(",");

			ArrayList<String> countryNames = new ArrayList<String>();
			for (String s : strArray) {
				countryNames.add(s);
				
				// add to the database
				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_COUNTRY, s);
				database.insert(SQLHelper.TABLE_COUNTRIES, null, values);
			}
			return countryNames;
		}
	}

	public ArrayList<String> getLanguageNames(String countryName) {
		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the languages yet
		Cursor cursor = database.query(SQLHelper.TABLE_LANGUAGES, new String[] {
				SQLHelper.COLUMN_ID, SQLHelper.COLUMN_COUNTRY, SQLHelper.COLUMN_LANGUAGE },
				SQLHelper.COLUMN_COUNTRY + WHERE, new String[] { countryName },
				null, null, null);

		if (cursor.moveToFirst()) {
			return getFromCursor(cursor, SQLHelper.COLUMN_LANGUAGE);
		} else {
			// database table was empty
			// download the language names and populate the table
			String results;
			try {
				results = WebAPI.getJSONArray(WebAPI.HTTP_GET.LANGUAGES, countryName);
			} catch (Exception e) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getMessage());
				return temp;
			}
			
			// remove quotes
			results = results.replace("\"", "");
			// should have a list of languages split by commas
			String[] strArray = results.split(",");

			ArrayList<String> languageNames = new ArrayList<String>();
			for (String s : strArray) {
				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_COUNTRY, countryName);
				values.put(SQLHelper.COLUMN_LANGUAGE, s);
				database.insert(SQLHelper.TABLE_LANGUAGES, null, values);
				languageNames.add(s);
			}
			return languageNames;
		}
	}

	public ArrayList<String> getLessonNames(String languageName) {
		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the lessons yet
		Cursor cursor = database.query(SQLHelper.TABLE_LESSONS, new String[] {
				SQLHelper.COLUMN_ID, SQLHelper.COLUMN_LANGUAGE, SQLHelper.COLUMN_LESSON,
				SQLHelper.COLUMN_LESSON_ID },
				SQLHelper.COLUMN_LANGUAGE + WHERE,
				new String[] { languageName }, null, null, null);

		if (cursor.moveToFirst()) {
			return getFromCursor(cursor, SQLHelper.COLUMN_LESSON);
		} else {
			// database table was empty
			// download the lesson names and populate the table
			String results;
			try {
				results = WebAPI.getJSONArray(WebAPI.HTTP_GET.LESSONS, languageName);
			} catch (Exception e) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getMessage());
				return temp;
			}

			// remove quotes
			results = results.replace("\"", "");
			// remove first bracket of first group and last bracket of last group
			results = results.substring(1, results.length() - 1); 
			// split at '],['
			// \\Q and \\E are used so the brackets are escaped properly
			String[] lessonNameAndIDArray = results.split(Pattern.compile(
					"\\Q]\\E,\\Q[\\E").pattern());

			ArrayList<String> lessonNames = new ArrayList<String>();
			for (String s : lessonNameAndIDArray) {
				// String s should be a Lesson Name, Lesson ID pair
				// Split it at the comma
				String[] tempArray = s.split(",");
				String lessonName = tempArray[0];
				String lessonID = tempArray[1];

				lessonNames.add(lessonName);

				// add to the database
				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_LANGUAGE, languageName);
				values.put(SQLHelper.COLUMN_LESSON, lessonName);
				values.put(SQLHelper.COLUMN_LESSON_ID, lessonID);
				database.insert(SQLHelper.TABLE_LESSONS, null, values);
			}
			return lessonNames;
		}
	}

	public String getLessonId(String lessonName) {
		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		Cursor cursor = database.query(SQLHelper.TABLE_LESSONS, new String[] {
				SQLHelper.COLUMN_ID, SQLHelper.COLUMN_LANGUAGE, SQLHelper.COLUMN_LESSON,
				SQLHelper.COLUMN_LESSON_ID },
				SQLHelper.COLUMN_LESSON + WHERE,
				new String[] { lessonName }, null, null, null);
		
		if (cursor.moveToFirst()) {
			return cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_LESSON_ID));
		} else {
			return "";
		}
	}
	public ArrayList<Phrase> getPhrases(String lessonId) throws Exception {
		ArrayList<Phrase> phrases = new ArrayList<Phrase>();

		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the phrase
		// information yet
		Cursor cursor = database.query(SQLHelper.TABLE_PHRASES, new String[] { SQLHelper.COLUMN_ID,
				SQLHelper.COLUMN_LESSON_ID, SQLHelper.COLUMN_PHRASE_ID,
				SQLHelper.COLUMN_PHRASE_TEXT, SQLHelper.COLUMN_TRANSLATED_TEXT, SQLHelper.COLUMN_AUDIO_URL},
				SQLHelper.COLUMN_LESSON_ID + WHERE, new String[] { lessonId },
				null, null, null);
		
		if (cursor.moveToFirst()) {
			int phraseIdColIndex = cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_PHRASE_ID);
			int phraseTextColIndex = cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_PHRASE_TEXT);
			int translatedTextColIndex = cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_TRANSLATED_TEXT);
			int audioURLColIndex = cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_AUDIO_URL);
			while (!cursor.isAfterLast()) {
				phrases.add(new Phrase(cursor.getString(phraseIdColIndex),
						cursor.getString(phraseTextColIndex),
						cursor.getString(translatedTextColIndex),
						cursor.getString(audioURLColIndex)));
				cursor.moveToNext();
			}
			// Make sure to close the cursor
			cursor.close();
		} else {
			// database table was empty
			// download the phrase information and populate the table
			String results = WebAPI.getJSONArray(WebAPI.HTTP_GET.PHRASES, lessonId);
			// remove first bracket of first group and last bracket of last group
			results = results.substring(1, results.length() - 1); 
			// split at '],['
			// \\Q and \\E are used so the brackets are escaped properly
			String[] phraseEntries = results.split(Pattern.compile(
					"\\Q]\\E,\\Q[\\E").pattern());

			for (String entry : phraseEntries) {
				// s should have the format: "PHRASE ID","PHRASE TEXT","TRANSLATED TEXT","AUDIO URL"
				// sometimes an entry like "PHRASE TEXT" or "TRANSLATED TEXT" is just: null

				// use StringBuilder to edit the String
				StringBuilder entryStringBuilder = new StringBuilder(entry);
				
				// check the string for commas, make sure we keep the ones
				// inside of quotes, otherwise change the comma to @
				boolean inQuotes = false;
				for (int i = 0; i < entryStringBuilder.length(); i++) {
					if (entryStringBuilder.charAt(i) == '\"') {
						// inQuotes gets toggled
						inQuotes = !inQuotes;
					} else if (entryStringBuilder.charAt(i) == ',' && !inQuotes) {
						// found a comma that was not in quotes
						// replace it with @ so we can split using @
						entryStringBuilder.setCharAt(i, '@');
					}
				}
			
				// convert the StringBuilder back to a String
				String tempEntry = entryStringBuilder.toString();
				// get rid of quotes
				tempEntry = tempEntry.replace("\"", "");
				// split at @, which is where commas not in quotes used to be
				String[] splitEntry = tempEntry.split("@");
				
				if (splitEntry != null) {
					// add to the list of phrases
					String phraseId = splitEntry[0];
					String phraseText = splitEntry[1];
					String translatedText = splitEntry[2];
					String audioURL = splitEntry[3];
					phrases.add(new Phrase(phraseId, phraseText, translatedText, audioURL));

					// add to the database
					ContentValues values = new ContentValues();
					values.put(SQLHelper.COLUMN_LESSON_ID, lessonId);
					values.put(SQLHelper.COLUMN_PHRASE_ID, phraseId);
					values.put(SQLHelper.COLUMN_PHRASE_TEXT, phraseText);
					values.put(SQLHelper.COLUMN_TRANSLATED_TEXT, translatedText);
					values.put(SQLHelper.COLUMN_AUDIO_URL, audioURL);
					database.insert(SQLHelper.TABLE_PHRASES, null, values);
				}
			}
		}
		return phrases;
	}
	
	public ArrayList<String> getPhraseOrder(String lessonId) {
		String results;
		ArrayList<String> phraseOrder = new ArrayList<String>();

		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the lessons yet
		Cursor cursor = database.query(SQLHelper.TABLE_PHRASE_ORDER, new String[] {
				SQLHelper.COLUMN_ID, SQLHelper.COLUMN_LESSON_ID, SQLHelper.COLUMN_PHRASE_ORDERING },
				SQLHelper.COLUMN_LESSON_ID + WHERE, new String[] { lessonId },
				null, null, null);

		if (cursor.moveToFirst()) {
			results = cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_PHRASE_ORDERING));
		} else {
			// database table was empty
			// download the lesson names and populate the table
			try {
				results = WebAPI.getJSONArray(WebAPI.HTTP_GET.PHRASE_ORDER, lessonId);

				// add to the database
				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_LESSON_ID, lessonId);
				values.put(SQLHelper.COLUMN_PHRASE_ORDERING, results);
				database.insert(SQLHelper.TABLE_PHRASE_ORDER, null, values);
			} catch (Exception e) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getMessage());
				return temp;
			}
		}

		// remove quotes
		results = results.replace("\"", "");
		// remove first bracket of first group and last bracket of last group
		results = results.substring(1, results.length() - 1); 
		// should have a list of phrase ids, split by commas
		String[] tempPhraseOrder = results.split(",");

		for (String s : tempPhraseOrder) {
			phraseOrder.add(s);
		}

		return phraseOrder;
	}

	public void reset() {
		databaseHelper.onUpgrade(database, 1, 1);
	}

	private ArrayList<String> getFromCursor(Cursor cursor, String columnName) {
		ArrayList<String> tempList = new ArrayList<String>();
		// Get all of the names from the table
		while (!cursor.isAfterLast()) {
			tempList.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tempList;
	}
}