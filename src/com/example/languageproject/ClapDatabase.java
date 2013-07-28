package com.example.languageproject;

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
				SQLHelper.COLUMN_COUNTRY_ID, SQLHelper.COLUMN_COUNTRY }, null,
				null, null, null, null);

		if (cursor.moveToFirst()) {
			return getNamesFromCursor(cursor);
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
				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_COUNTRY, s);
				database.insert(SQLHelper.TABLE_COUNTRIES, null, values);
				countryNames.add(s);
			}
			return countryNames;
		}
	}

	public ArrayList<String> getLanguageNames(String countryName) {
		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the languages
		// yet
		Cursor cursor = database.query(SQLHelper.TABLE_LANGUAGES, new String[] {
				SQLHelper.COLUMN_COUNTRY, SQLHelper.COLUMN_LANGUAGE },
				SQLHelper.COLUMN_COUNTRY + WHERE, new String[] { countryName },
				null, null, null);

		if (cursor.moveToFirst()) {
			return getNamesFromCursor(cursor);
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
				SQLHelper.COLUMN_LANGUAGE, SQLHelper.COLUMN_LESSON,
				SQLHelper.COLUMN_LESSON_ID },
				SQLHelper.COLUMN_LANGUAGE + WHERE,
				new String[] { languageName }, null, null, null);

		if (cursor.moveToFirst()) {
			return getNamesFromCursor(cursor);
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

				ContentValues values = new ContentValues();
				values.put(SQLHelper.COLUMN_LANGUAGE, languageName);
				values.put(SQLHelper.COLUMN_LESSON, lessonName);
				values.put(SQLHelper.COLUMN_LESSON_ID, lessonID);
				database.insert(SQLHelper.TABLE_LESSONS, null, values);
			}
			return lessonNames;
		}
	}

	public ArrayList<Phrase> getPhrases(String lessonId) {
		ArrayList<Phrase> phrases = new ArrayList<Phrase>();

		if (database == null) {
			database = databaseHelper.getWritableDatabase();
		}

		// Check the database table to see if we have downloaded the phrase
		// information yet
		Cursor cursor = database.query(SQLHelper.TABLE_PHRASES, new String[] { SQLHelper.COLUMN_LESSON_ID, SQLHelper.COLUMN_PHRASE_ID,
				SQLHelper.COLUMN_PHRASE_TEXT, SQLHelper.COLUMN_TRANSLATED_TEXT, SQLHelper.COLUMN_AUDIO_URL},
				SQLHelper.COLUMN_LANGUAGE + WHERE, new String[] { lessonId },
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
		//} else {
			// database table was empty
			// download the phrase information and populate the table
			//String results = WebAPI.getJSONArray(WebAPI.HTTP_GET.PHRASES, lessonName);
		}
		return new ArrayList<Phrase>();
	}
	
	public void reset() {
		databaseHelper.onUpgrade(database, 1, 1);
	}

	private ArrayList<String> getNamesFromCursor(Cursor cursor) {
		ArrayList<String> tempList = new ArrayList<String>();
		// Get all of the names from the table
		while (!cursor.isAfterLast()) {
			tempList.add(cursor.getString(1));
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return tempList;
	}
}