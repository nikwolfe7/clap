package com.clap;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class LessonListActivity extends ClapListActivity {
	private String languageName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the information passed to us from the last activity
		Intent i = getIntent();
		Bundle b = i.getExtras();
		languageName = b.getString(EXTRA_LANGUAGE_NAME);
		title = "Lesson List for " + languageName;

		super.onCreate(savedInstanceState);

		new LoadLessonListTask(this).execute();
	}

	protected class LoadLessonListTask extends LoadListTask {

		public LoadLessonListTask(Context c) {
			super(c);
			emptyListMessage = "Language lessons currently unavailable for " + languageName;
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			try {
				ApplicationState state = (ApplicationState) getApplication();
				return state.getLessonNames(languageName);
			} catch (Exception e) {
				exception = e;
				return null;
			}
		}	
		
		@Override
		public void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			// What was selected?
			String lessonSelected = (parent.getAdapter().getItem(position).toString());
			
			// create the lesson activity 
			Intent lessonActivity = new Intent(context, LessonActivity.class);
			lessonActivity.putExtra(EXTRA_LESSON_NAME, lessonSelected);
			startActivity(lessonActivity);
		}
	}
}