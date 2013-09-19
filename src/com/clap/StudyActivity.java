package com.clap;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class StudyActivity extends ClapListActivity {

	private String lesson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		USE_OPTIONS_MENU_EXPORT = true;
		USE_OPTIONS_MENU_ADD = true;
		
		Intent i = getIntent();
		Bundle b = i.getExtras();

		lesson = b.getString(EXTRA_LESSON_NAME);
		title = lesson;
		super.onCreate(savedInstanceState);

		new LoadPhraseListTask(this).execute();
	}
	
	protected class LoadPhraseListTask extends LoadListTask {
		private HashMap<String, Phrase> phraseMap = new HashMap<String, Phrase>();
		
		public LoadPhraseListTask(Context c) {
			super(c);
			emptyListMessage = "Phrases for " + lesson + " are currently unavailable!";
			loadMessage = "Loading phrases for " + lesson + "...";
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState) getApplication();
			try {
				// Get the phrases for this lesson then create a hash map with
				//   the phrase text as the key and the Phrase object as the value.
				//   Return a list of all the phrases as strings for OnPostExecute
				//   to display.
				ArrayList<Phrase> phraseList = state.getPhrases(lesson);
				ArrayList<String> keyList = new ArrayList<String>();
				for( Phrase p : phraseList ) {
					phraseMap.put(p.getPhraseText(), p);
					keyList.add(p.getPhraseText());
				}
				return keyList;
			} catch (Exception e) {
				exception = e;
				return null;
			}
		}
		
		@Override
		public void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			// When a phrase is clicked, display the phrase with the translated text
			String phraseSelected = parent.getAdapter().getItem(position).toString();
			displayPhrase(phraseSelected);
		}

		private void displayPhrase(String phraseSelected) {
			Phrase phrase = phraseMap.get(phraseSelected);
			
			// Build the dialog box to pop up with the title as the phrase and the message
			//   as the translated. Allow the user to play the audio by clicking on the 
			//   play button.
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			
			dialog.setPositiveButton(R.string.play, new PlayPhrase(phrase, context));
			dialog.setNeutralButton(R.string.close, null);
			
			dialog.setCancelable(true);
			
			dialog.setMessage(phrase.getTranslatedText());
			dialog.setTitle(phraseSelected);
			
			dialog.create().show();
		}
		
		private class PlayPhrase implements OnClickListener {
			private Phrase phrase;
			private Context context;

			public PlayPhrase(Phrase p, Context c) {
				// We need the Context to download the audio file
				phrase = p;
				context = c;
			}

			@Override
			public void onClick(final DialogInterface dialog, int which) {
				// Create a new thread, that doesn't run on the UI thread
				// Download Audio cannot be on the UI thread
				Thread thread = new Thread(new Runnable() {
					public void run() {
						MediaPlayer mediaPlayer = new MediaPlayer();
						mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
						try {
							if (!phrase.audioFileExists()) {
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(context, "Downloading Audio File",
												Toast.LENGTH_SHORT).show();
									}
								});
								phrase.downloadAudio(context);
								runOnUiThread(new Runnable() {
									public void run() {
										Toast.makeText(context,
												"\'" + phrase.getPhraseText()+ "\' Audio Downloaded",
												Toast.LENGTH_SHORT).show();
									}
								});
							}
							mediaPlayer.setDataSource(phrase.getAudioLocation());
							mediaPlayer.prepare();
							mediaPlayer.start();
						} catch (final Exception e) {
							// Before displaying the error, dismiss the current dialog
							dialog.dismiss();
							// Run showErrorMessage on the UI thread, it creates a dialog box
							runOnUiThread(new Runnable() {
								public void run() {
									showErrorMessage(e, false);
								}
							});
						}
					}
				});
				thread.start();
			}
		}
	}
}
