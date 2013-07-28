package com.example.languageproject;

import java.util.ArrayList;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class LessonDialog extends Activity {
	
	// Any params from previous activities which will
	// be used to set the title, etc.
	private ArrayList<String> params;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_dialog);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		params = b.getStringArrayList(LessonList.EXTRAS);
		String title = "";
		for(int x = 0; x < params.size(); x++) {
			if( x == params.size() - 1 ) {
				title += params.get(x);
			} else {
				title += params.get(x) + " : ";
			}
		}
		setTitle(title);
		TextView lessonTitle = (TextView)findViewById(R.id.lesson_title);
		lessonTitle.setText(b.getString(LessonList.LESSON_TITLE));
		new LongRunningGetIO(this).execute();
		
	}
	
	// screen IO class
	private class LongRunningGetIO extends AsyncTask<Void, Void, ArrayList<String>> {

		// The lesson dialog context
		private Context c;
	
		public LongRunningGetIO(Context context) {
			c = context;
		}

		@Override
		protected ArrayList<String> doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

}
