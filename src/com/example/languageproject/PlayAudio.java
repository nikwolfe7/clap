package com.example.languageproject;

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

public class PlayAudio extends Activity {
	private String lesson_name;
	private String language_name;
	private String country_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_audio);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		lesson_name = b.getString("lessonName");
		language_name = b.getString("languageName");
		country_name = b.getString("countryName");
		setTitle(country_name + " : " + language_name + " : " + lesson_name);
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
			progressDialog.setMessage("Loading...");
			progressDialog.show();
		}
		
		@Override
		protected ArrayList<Phrase> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState)getApplication();
			return state.getPhrases(language_name, lesson_name);
			/*Country currentCountry = state.getCountry(country_name);
			if (currentCountry != null) {
				Language currentLanguage = currentCountry.getLanguage(language_name);
				if (currentLanguage != null) {
					Lesson currentLesson = currentLanguage.getLesson(lesson_name);
					if (currentLesson != null) {
						ArrayList<Phrase> phrases = currentLesson.getPhrases();
						for (Phrase p : phrases) {
							try {
								p.downloadAudioFile();
							} catch (Exception e) {
								showErrorMessage(e.toString());
							}
						}
						ArrayList<String> phraseOrder = currentLesson.getPhraseOrder();
						return orderPhrases(phrases, phraseOrder);
					} else {
						// couldn't get current lesson
						return null;
					}
				} else {
					// couldn't get current language
					return null;
				}
			} else {
				// couldn't get current country
				return null;
			}*/
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
				showErrorMessage("Empty List");
			} else {
				// Do something
				Phrase p = phraseList.get(0);
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					mediaPlayer.setDataSource(p.getAudioLocation());
					mediaPlayer.prepareAsync();
					mediaPlayer.start();
				} catch (Exception e) {
					showErrorMessage(e.toString());
				}
			}		}
	}
}
