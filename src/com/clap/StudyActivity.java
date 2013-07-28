package com.clap;
import java.util.ArrayList;
import java.util.HashMap;

import com.clap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class StudyActivity extends Activity {

	private ArrayList<String> params;
	private String lesson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
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
	
	private class LongRunningGetIO extends AsyncTask<Void, Void, ArrayList<String>> {

		private Context c;
		private ProgressDialog pd;
		private HashMap<String, Phrase> phraseMap = new HashMap<String, Phrase>();
		
		public LongRunningGetIO(Context context) {
			this.c = context;
			this.pd = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			pd.setMessage("Loading phrases for " + lesson + "...");
			pd.show();
		}
		
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
				displayError(e.getMessage());
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<String> phraseNameList) {
			pd.dismiss(); // kill the load dialog
			
			if( phraseNameList == null || phraseNameList.size() < 1 ) {
				displayError("Phrases for this lesson are currently unavailable!");
			} else {
				
				// phrases list
				ListView lv = (ListView) findViewById(R.id.list);
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(c, R.layout.item, R.id.item_id, phraseNameList);
				lv.setAdapter(adapter);
				
				// item in the phrase is clicked
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						
					}
				
				
				});
				
				
			}
		}
		
		private void displayError(String msg) {
			AlertDialog.Builder b = new AlertDialog.Builder(c);
			b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			b.setMessage(msg).setTitle("Oops!");
			b.create().show();
		}
		
	}

}
