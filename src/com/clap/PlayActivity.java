package com.clap;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
	private PlayAudio playAudio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_activity);

		Intent i = getIntent();
		Bundle b = i.getExtras();

		lesson = b.getString(EXTRA_LESSON_NAME);
		playingTitle += lesson;
		pausedTitle += lesson;

		// Set the Language TextView to the current language
		TextView textView = (TextView)findViewById(R.id.language);
		textView.setText(lesson.split(" ")[0]);

		setTitle(lesson);

		new LoadLessonTask(this).execute();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		
		savedInstanceState.putSerializable("PlayAudio", playAudio);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		playAudio = (PlayAudio) savedInstanceState.getSerializable("PlayAudio");
	}

	private class LoadLessonTask extends AsyncTask<Void, Integer, ArrayList<Phrase>> {
		private Context context;
		private ProgressDialog progressDialog;
		private Exception exception = null;
		
		public LoadLessonTask(Context c) {
			context = c;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading phrases for " + lesson + "...");
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
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
				exception = e;
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
				if (exception == null || exception.getMessage() == WebAPI.ERROR_EMPTY_LIST) {
					showErrorMessage("Phrases for " + lesson + " are currently unavailable!");
				} else {
					showErrorMessage(exception);
				}
			} else {
				new DownloadAudio(context, phraseList).execute();
			}		}
	}

	private class DownloadAudio extends AsyncTask<Void, Integer, Void> {
		private Context context;
		private ArrayList<Phrase> phraseList;
		private ProgressDialog progressDialog;
		private Exception exception = null;
		
		public DownloadAudio(Context c, ArrayList<Phrase> phrases) {
			context = c;
			phraseList = phrases;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading phrases for " + lesson + "...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMax(phraseList.size());
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
			progressDialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				int currentPhrase = 1;
				for (Phrase p : phraseList) {
					p.downloadAudio(context);
					publishProgress(currentPhrase);
					currentPhrase++;
				}
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}
	
		protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
		}

		@Override
		protected void onPostExecute(Void param) {
			progressDialog.dismiss();
			
			if (exception != null) {
				showErrorMessage(exception);
			} else {
				Button playButton = (Button)findViewById(R.id.btnPlay);
				Button prevButton = (Button)findViewById(R.id.btnPrevious);
				Button nextButton = (Button)findViewById(R.id.btnNext);

				// PlayAudio will be the OnClickListener for all the Buttons
				playAudio = new PlayAudio(phraseList, context);

				playButton.setOnClickListener(playAudio);
				prevButton.setOnClickListener(playAudio);
				nextButton.setOnClickListener(playAudio);
			}		}
	}

	private class PlayAudio implements OnClickListener, OnCompletionListener, Serializable {
		private static final long serialVersionUID = 1L;

		private ArrayList<Audio> phraseAudioList = new ArrayList<Audio>();
		private int currentPhrase = 0;
		private boolean isPlaying = false;
		
		public PlayAudio(ArrayList<Phrase> phrases, Context c) {
			for (Phrase p : phrases) {
				try {
					// PlayAudio will be the OnCompletionListener for when MediaPlayer finishes
					phraseAudioList.add(new Audio(p, this));
				} catch (Exception e) {
					showErrorMessage(e, false);
					return;
				}
			}
		}

		@Override
	    public void onCompletion(MediaPlayer mediaPlayer) {
			// Release the resources that MediaPlayer was using
			mediaPlayer.release();
			
			try {
				// Wait 1.5 seconds before playing the next track
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Move to the next track unless we are at the last track
			if (currentPhrase < phraseAudioList.size() - 1) {
				// Play the next track
				currentPhrase++;
				play();
			} else { 
				// We've reached the last track, reset
				isPlaying = false;
				currentPhrase = 0;
				
				// Set the Title and the Play Button
				setTitle(lesson);
				Button playButton = (Button)findViewById(R.id.btnPlay);
				playButton.setBackgroundResource(R.drawable.play_button);
			}
	    }

		@Override
		public void onClick(View v) {
			try {
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
			} catch (Exception e) {
				showErrorMessage(e);
			}
		}
		
		private void play() {
			try {
				if (isPlaying) {
					Audio a = phraseAudioList.get(currentPhrase);

					// Set the Text Views to the Phrase Text and Translated Text
					TextView textView = (TextView)findViewById(R.id.phraseText);
					textView.setText(a.getPhraseText());
					textView = (TextView)findViewById(R.id.translatedText);
					textView.setText(a.getTranslatedText());

					// Set the Title and the Play Button
					setTitle(playingTitle);
					Button playButton = (Button)findViewById(R.id.btnPlay);
					playButton.setBackgroundResource(R.drawable.pause_button);

					a.play();
				}
			} catch (Exception e) {
				showErrorMessage(e, false);
			}
		}
		
		private void pause() {
			phraseAudioList.get(currentPhrase).pause();
			setTitle(pausedTitle);
			Button playButton = (Button)findViewById(R.id.btnPlay);
			playButton.setBackgroundResource(R.drawable.play_button);
		}

		private void next() {
			if (currentPhrase < phraseAudioList.size() - 1) {
				// Stop whatever is currently playing, then start playing the next phrase
				phraseAudioList.get(currentPhrase).stop();
				currentPhrase++;
				isPlaying = true;
				play();
			}
		}
		
		private void previous() {
			if (currentPhrase > 0) {
				// Stop whatever is currently playing, then start playing the previous phrase
				phraseAudioList.get(currentPhrase).stop();
				currentPhrase--;
				isPlaying = true;
				play();
			}
		}
	}
}