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

public class StudyActivity extends ListActivity {

	private String lesson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent i = getIntent();
		Bundle b = i.getExtras();

		lesson = b.getString(EXTRA_LESSON_NAME);
		title = "Phrase List for " + lesson;

		super.onCreate(savedInstanceState);

		new LoadPhraseListTask(this).execute();
	}
	
	protected class LoadPhraseListTask extends LoadListTask {
		private HashMap<String, Phrase> phraseMap = new HashMap<String, Phrase>();
		
		public LoadPhraseListTask(Context c) {	super(c); }

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState) getApplication();
			try {
				ArrayList<Phrase> phraseList = state.getPhrases(lesson);
				ArrayList<String> keyList = new ArrayList<String>();
				for( Phrase p : phraseList ) {
					phraseMap.put(p.getPhraseText(), p);
					keyList.add(p.getPhraseText());
				}
				return keyList;
			} catch (Exception e) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(e.getMessage());
				return temp;
			}
		}
		
		@Override
		protected void showEmptyListMessage() {
			showErrorMessage("Phrases for " + lesson + " are currently unavailable!");
		}

		@Override
		public void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			String phraseSelected = parent.getAdapter().getItem(position).toString();
			displayPhrase(phraseSelected);
		}

		private void displayPhrase(String phraseSelected) {
			Phrase phrase = phraseMap.get(phraseSelected);
			
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
				phrase = p;
				context = c;
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				try {
					phrase.downloadAudio(context);
					mediaPlayer.setDataSource(phrase.getAudioLocation());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
