package com.example.languageproject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
		String lt = b.getString(LessonList.LESSON_TITLE).replace(" L", "\n\nL");
		TextView lessonTitle = (TextView)findViewById(R.id.lesson_title);
		lessonTitle.setText(lt);
		
		// set button listeners
		Button btnReturn = (Button) findViewById(R.id.btn_quit_lesson);
		Button btnStudy = (Button) findViewById(R.id.btn_study_phrases);
		Button btnPlay = (Button) findViewById(R.id.btn_play_lesson);
		
		btnReturn.setOnClickListener(new ReturnButton());
		btnStudy.setOnClickListener(new StudyButton());
		btnPlay.setOnClickListener(new PlayLesson());
		
		new LongRunningGetIO(this).execute();
		
	}
	
	// play the damn lesson
	private class PlayLesson implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// study the damn stuff
	private class StudyButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	// return to the previous screen
	private class ReturnButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
		
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
