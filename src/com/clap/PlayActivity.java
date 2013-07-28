package com.clap;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

public class PlayActivity extends Activity {
	private ArrayList<String> params;
	private String lesson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_audio);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		params = b.getStringArrayList(LessonDialog.EXTRAS);
		String title = "";
		for(int x = 0; x < params.size(); x++) {
			if( x == params.size() - 1 ) {
				title += params.get(x);
			} else {
				title += params.get(x) + " : ";
			}
		}
		setTitle(title);
		lesson = b.getString(LessonDialog.LESSON_TITLE);
		new LongRunningGetIO(this).execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, ArrayList<Phrase>> {
		private Context context;
		private ProgressDialog progressDialog;
		
		public LongRunningGetIO(Context c) {
			context = c;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading phrases for " + lesson + "...");
			progressDialog.show();
		}
		
		@Override
		protected ArrayList<Phrase> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState)getApplication();
			try {
				return state.getPhrases(lesson);
			} catch (Exception e) {
				showErrorMessage(e.getMessage());
				return new ArrayList<Phrase>();
			}
		}
		
		private ArrayList<Phrase> orderPhrases(ArrayList<Phrase> phraseList, ArrayList<String> phraseOrder) {
			ArrayList<Phrase> temp = new ArrayList<Phrase>();
			for (String s : phraseOrder) {
				for (Phrase p : phraseList) {
					if (p.getPhraseId().equals(s)) {
						temp.add(p);
						break;
					}
				}
			}
			return temp;
		}
		
		private void showErrorMessage(String error) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// return to the lesson activity
					finish();
				}
			});
			builder.setMessage(error)
			.setTitle("Error");
			AlertDialog errorDialog = builder.create();
			errorDialog.show();
		}
		
		protected void onPostExecute(ArrayList<Phrase> phraseList) {
			progressDialog.dismiss();
			
			if (phraseList == null || phraseList.isEmpty()) {
				showErrorMessage("Phrases for this lesson are currently unavailable!");
			} else {
				// Do something
				Phrase p = phraseList.get(0);
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					p.downloadAudio();
					mediaPlayer.setDataSource(p.getAudioLocation());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (Exception e) {
					showErrorMessage(e.toString());
				}
			}
	}
}