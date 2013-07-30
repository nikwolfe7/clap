package com.clap;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends ClapActivity {
	private String playingTitle = "Playing: ";
	private String pausedTitle = "Paused: ";
	private String lesson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);

		Intent i = getIntent();
		Bundle b = i.getExtras();

		lesson = b.getString(EXTRA_LESSON_NAME);
		playingTitle += lesson;
		pausedTitle += lesson;

		setTitle(playingTitle);

		new LoadLessonTask(this).execute();
	}

	private class LoadLessonTask extends AsyncTask<Void, Void, ArrayList<Phrase>> {
		private Context context;
		private ProgressDialog progressDialog;
		
		public LoadLessonTask(Context c) {
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
				ArrayList<Phrase> phrases = state.getPhrases(lesson);
				ArrayList<String> phraseOrder = state.getPhraseOrder(lesson);
				return orderPhrases(phrases, phraseOrder);
			} catch (Exception e) {
				progressDialog.dismiss();
				showErrorMessage(e.getMessage());
				return null;
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
		
		
		protected void onPostExecute(ArrayList<Phrase> phraseList) {
			progressDialog.dismiss();
			
			if (phraseList == null || phraseList.isEmpty()) {
				showErrorMessage("Phrases for this lesson are currently unavailable!");
			} else {

				//new DownloadAudio(phraseList, context).execute();
				
				Button playButton = (Button)findViewById(R.id.btnPlay);
				playButton.setOnClickListener(new PlayAudio(phraseList, context));
			}		}
	}

	/*private class DownloadAudio extends AsyncTask<Void, Void, Void> {
		private Context context;
		private ArrayList<Phrase> phraseList;

		public DownloadAudio(ArrayList<Phrase> phrases, Context c) {
			phraseList = phrases;
			context = c;
		}

		@Override
		protected Void doInBackground(Void... params) {
			for (Phrase p : phraseList) {
				try {
					p.downloadAudio(context);
				} catch(Exception e) {
					showErrorMessage(e.getMessage(), false);
				}
			}
			return null;
		}
	}*/

	private class PlayAudio implements OnClickListener {
		private ArrayList<Audio> phraseAudioList = new ArrayList<Audio>();
		private int currentPhrase = 0;
		private boolean isPlaying = false;
		
		public PlayAudio(ArrayList<Phrase> phrases, Context c) {
			for (Phrase p : phrases) {
				try {
					phraseAudioList.add(new Audio(p, c));
				} catch (Exception e) {
					showErrorMessage(e.getMessage(), false);
				}
			}
		}

		@Override
		public void onClick(View v) {
			int itemClickedId = v.getId();
			switch(itemClickedId) {
				case R.id.btnPlay:
					synchronized(this) {
						if (isPlaying) {
							isPlaying = false;
							pause();
						} else {
							isPlaying = true;
							play();
						}
					}
					return;
				case R.id.btnPrevious:
					previous();
					return;
				case R.id.btnNext:
					next();
					return;
				default:
					return;
			}
		}
		
		private void play() {
			try {
				if (isPlaying) {
					Audio a = phraseAudioList.get(currentPhrase);

					TextView textView = (TextView)findViewById(R.id.phraseText);
					textView.setText(a.getPhraseText());
					textView = (TextView)findViewById(R.id.translatedText);
					textView.setText(a.getTranslatedText());

					a.play();
					
					setTitle(playingTitle);
				}
			} catch (Exception e) {
				showErrorMessage(e.toString(), false);
			}
		}
		
		private void pause() {
			phraseAudioList.get(currentPhrase).pause();
			setTitle(pausedTitle);
		}

		private void next() {
			if (currentPhrase < phraseAudioList.size() - 1) {
				currentPhrase++;
			}
		}
		
		private void previous() {
			if (currentPhrase > 0) {
				currentPhrase--;
			}
		}
	}
}