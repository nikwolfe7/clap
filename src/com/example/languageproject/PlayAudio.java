package com.example.languageproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

public class PlayAudio extends Activity {
	private static String HTTP_GET_STRING = "http://www.celebrate-language.com/public-api/?action=get_phrases_by_lesson_id&id=";
	
	private String lesson_name;
	private String lesson_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_audio);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		lesson_name = b.getString("lessonName");
		lesson_id = b.getString("lessonID");
		setTitle(lesson_name);
		new LongRunningGetIO().execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);
				if (n > 0)
					out.append(new String(b, 0, n));
			}
			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(HTTP_GET_STRING + lesson_id);
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			return text;
		}
		
		protected void onPostExecute(String results) {
			if (results != null) {
				String temp = results.substring(2, results.length()-2); // remove surrounding brackets
				String[] strPhraseEntries = temp.split("],[");
				ArrayList<PhraseEntry> phraseEntries = new ArrayList<PhraseEntry>();
				for (int i=0; i<strPhraseEntries.length; i++) {
					String tempEntry = strPhraseEntries[i].substring(1, strPhraseEntries[i].length()-1); // remove surrounding quotes
					String[] entry = tempEntry.split("\",\"");
					if (entry != null) {
						// should have four entries
						// TODO: better error handling
						phraseEntries.add(new PhraseEntry(entry[0], entry[1], entry[2], entry[3]));
					}
				}
			}		}
	}
	
	public class PhraseEntry {
		private String _phraseId;
		private String _phraseText;
		private String _translatedText;
		private String _audioURL;
		
		// Constructors
		public PhraseEntry(String id, String pText, String tText, String url) {
			_phraseId = id;
			_phraseText = pText;
			_translatedText = tText;
			_audioURL = url;
		}
		
		public PhraseEntry() {
			_phraseId = "";
			_phraseText = "";
			_translatedText = "";
			_audioURL = "";
		}
		
		// Setters
		public void setPhraseId(String id) {
			_phraseId = id;
		}
		
		public void setPhraseText(String text) {
			_phraseText = text;
		}
		
		public void setTranslatedText(String text) {
			_translatedText = text;
		}
		
		public void setAudioURL(String url) {
			_audioURL = url;
		}
		
		// Getters
		public String getPhraseId() {
			return _phraseId;
		}
		
		public String getPhraseText() {
			return _phraseText;
		}
		
		public String getTranslatedText() {
			return _translatedText;
		}
		
		public String getAudioURL() {
			return _audioURL;
		}
	}
}
