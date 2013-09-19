package com.clap;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class MyLessonsActivity extends ClapListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Get the information passed to us from the last activity
		title = "My Lessons List";

		super.onCreate(savedInstanceState);

		new LoadLessonListTask(this).execute();
	}

	protected class LoadLessonListTask extends LoadListTask {

		public LoadLessonListTask(Context c) {
			super(c);
			emptyListMessage = "You Don't Have Saved Any Lessons";
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			try {
				ApplicationState state = (ApplicationState) getApplication();
				return state.getMyLessons();
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
