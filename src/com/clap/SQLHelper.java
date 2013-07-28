package com.clap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "CLAP";
	public static final int DATABASE_VERSION = 1;

	// Table Names
	public static final String TABLE_COUNTRIES = "COUNTRIES";
	public static final String TABLE_LANGUAGES = "LANGUAGES";
	public static final String TABLE_LESSONS = "LESSONS";
	public static final String TABLE_PHRASES = "PHRASES";
	public static final String TABLE_PHRASE_ORDER = "PHRASE_ORDER";

	// Columns
	public static final String COLUMN_ID = "_ID";
	//public static final String COLUMN_COUNTRY_ID = "COUNTRY_ID";
	public static final String COLUMN_COUNTRY = "COUNTRY";
	public static final String COLUMN_LANGUAGE = "LANGUAGE";
	public static final String COLUMN_LESSON = "LESSON";
	public static final String COLUMN_LESSON_ID = "LESSON_ID";
	public static final String COLUMN_PHRASE_ID = "PHRASE_ID";
	public static final String COLUMN_PHRASE_TEXT = "PHRASE_TEXT";
	public static final String COLUMN_TRANSLATED_TEXT = "TRANSLATED_TEXT";
	public static final String COLUMN_AUDIO_URL = "AUDIO_URL";
	public static final String COLUMN_PHRASE_ORDERING = "PHRASE_ORDERING";

	// Helper Strings
	private static final String INT = " INTEGER";
	private static final String PRIMARYKEY = " PRIMARY KEY AUTOINCREMENT";
	private static final String TEXT = " TEXT NOT NULL";
	private static final String BLOB = " BLOB";
	private static final String COMMA = ",";
	private static final String CREATE = "CREATE TABLE IF NOT EXISTS ";

	// Create Tables
	private static final String CREATE_COUNTRIES = CREATE
			+ TABLE_COUNTRIES + " ("
			+ COLUMN_ID + INT + PRIMARYKEY + COMMA
			+ COLUMN_COUNTRY + TEXT + ");";
	private static final String CREATE_LANGUAGES = CREATE
			+ TABLE_LANGUAGES + " ("
			+ COLUMN_ID + INT + PRIMARYKEY + COMMA
			+ COLUMN_COUNTRY + TEXT + COMMA
			+ COLUMN_LANGUAGE + TEXT + ");";
	private static final String CREATE_LESSONS = CREATE
			+ TABLE_LESSONS + " ("
			+ COLUMN_ID + INT + PRIMARYKEY + COMMA
			+ COLUMN_LANGUAGE + TEXT + COMMA
			+ COLUMN_LESSON + TEXT + COMMA
			+ COLUMN_LESSON_ID + INT + ");";
	private static final String CREATE_PHRASES = CREATE
			+ TABLE_PHRASES + " ("
			+ COLUMN_ID + INT + PRIMARYKEY + COMMA
			+ COLUMN_LESSON_ID + INT + COMMA
			+ COLUMN_PHRASE_ID + INT + COMMA
			+ COLUMN_PHRASE_TEXT + TEXT + COMMA
			+ COLUMN_TRANSLATED_TEXT + TEXT + COMMA
			+ COLUMN_AUDIO_URL + TEXT + ");";
	private static final String CREATE_PHRASE_ORDER = CREATE
			+ TABLE_PHRASE_ORDER + " ("
			+ COLUMN_ID + INT + PRIMARYKEY + COMMA
			+ COLUMN_LESSON_ID + INT + COMMA
			+ COLUMN_PHRASE_ORDERING + BLOB + ");";

	public SQLHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_COUNTRIES);
		database.execSQL(CREATE_LANGUAGES);
		database.execSQL(CREATE_LESSONS);
		database.execSQL(CREATE_PHRASES);
		database.execSQL(CREATE_PHRASE_ORDER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		//Log.w(SQLHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
		dropTable(database, TABLE_COUNTRIES);
		dropTable(database, TABLE_LANGUAGES);
		dropTable(database, TABLE_LESSONS);
		dropTable(database, TABLE_PHRASES);
		dropTable(database, TABLE_PHRASE_ORDER);
		onCreate(database);
	}
	
	private void dropTable(SQLiteDatabase database, String table) {
		database.execSQL("DROP TABLE IF EXISTS " + table);
	}
}