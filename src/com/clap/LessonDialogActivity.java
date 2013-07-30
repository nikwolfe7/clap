package com.clap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LessonDialogActivity extends ClapActivity {
	private String lesson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_dialog);

		Intent i = getIntent();
		Bundle b = i.getExtras();
		lesson = b.getString(EXTRA_LESSON_NAME);
		setTitle(lesson);

		TextView lessonTitle = (TextView)findViewById(R.id.lesson_title);
		lessonTitle.setText(lesson.replace(" L", "\nL"));
		
		// set button listeners
		Button btnReturn = (Button) findViewById(R.id.btn_quit_lesson);
		Button btnStudy = (Button) findViewById(R.id.btn_study_phrases);
		Button btnPlay = (Button) findViewById(R.id.btn_play_lesson);
		
		btnReturn.setOnClickListener(new ReturnButton());
		btnStudy.setOnClickListener(new StudyButton());
		btnPlay.setOnClickListener(new PlayLesson());
	}
	
	// play the damn lesson
	private class PlayLesson implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), PlayActivity.class);
			i.putExtra(EXTRA_LESSON_NAME, lesson);
			startActivity(i);
		}
		
	}
	
	// study the damn stuff
	private class StudyButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), StudyActivity.class);
			i.putExtra(EXTRA_LESSON_NAME, lesson);
			startActivity(i);
		}
		
	}
	
	// return to the previous screen
	private class ReturnButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}
}
