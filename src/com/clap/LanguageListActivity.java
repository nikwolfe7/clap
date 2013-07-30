package com.clap;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class LanguageListActivity extends ListActivity {
	private String countryName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent i = getIntent();
		Bundle b = i.getExtras();
		countryName = b.getString(EXTRA_COUNTRY_NAME);
		title = "Language List for " + countryName;

		super.onCreate(savedInstanceState);

		new LoadLanguageListTask(this).execute();
	}

	protected class LoadLanguageListTask extends LoadListTask {

		public LoadLanguageListTask(Context c) { super(c);	}
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState) getApplication();
			return state.getLanguageNames(countryName);
		}
		
		@Override
		protected void showEmptyListMessage() {
			showErrorMessage("Language lessons currently unavailable for " + countryName);
		}
		
		@Override
		protected void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			// What was selected?
			String languageSelected = (parent.getAdapter().getItem(position).toString());

			// Start the lesson list activity
			// Pass it the language name and country name
			Intent lessonActivity = new Intent(context, LessonListActivity.class);
			lessonActivity.putExtra(EXTRA_LANGUAGE_NAME, languageSelected);
			startActivity(lessonActivity);
		}
	}
}
